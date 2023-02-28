/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

import com.vmware.vip.common.csp.Claim;
import com.vmware.vip.core.login.VipAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.Instant;


@Service
public class CSPTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSPTokenService.class);

    private Instant keyRotateEndpointLastAccess;
    private URL url;
    private JWKSet jwksInMem;

    @Autowired
    private VipAuthConfig vipAuthConfig;

    /**
     * @param token to validate
     * @return true if token is valid and not expired, o.w. return false
     */
    public boolean isTokenValid(final String token) {
        return getTokenClaims(token) != null;
    }

    /**
     * Verify and extract the token Claims section
     * <p>
     * if the token is not valid or expired, return null
     *
     * @param token to validate and extract it claims
     * @return the token claims
     */
    public Claim getTokenClaims(final String token) {
        if (jwksInMem == null || jwksInMem.getKeys() == null) {
            callCspJwksEndpoint();
        }
        try {
            return validate(token);
        } catch (final BadJWTException  e) {
            LOGGER.error("Token is not valid", e);
        } catch (final BadJOSEException e) {
            LOGGER.error("Bad JSON Object Signing and Encryption found", e);
        } catch (final ParseException  e) {
            LOGGER.error("Error while parsing token string", e);
        } catch (final JOSEException e) {
            LOGGER.error("Internal processing of token failed", e);
        }

        return null;
    }

    private Claim  validate(String token) throws ParseException, BadJOSEException, JOSEException {
        if (token == null) {
            return null;
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        String tokenKid = signedJWT.getHeader().getKeyID();
        JWK matchKey = null;
        for (JWK key : jwksInMem.getKeys()) {
            if (key.getKeyID().equals(tokenKid)) {
                matchKey = key;
                break;
            }
        }

        if ((matchKey == null) && !allowRefreshJwkset(vipAuthConfig.getRefreshIntervalSec())) {
            LOGGER.info("Trying to hit public key endpoint within {} sec, possibly a DoS (Denial of Service) attack",
                    vipAuthConfig.getRefreshIntervalSec());
        }
        if (matchKey == null){
            return null;
        }
        JWSAlgorithm alg = getKeyAlg(matchKey);
        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(2000, 2000);
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWKSource keySource = new RemoteJWKSet(url, resourceRetriever);

        JWSKeySelector keySelector = new JWSVerificationKeySelector(alg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        SecurityContext ctx = null;
        JWTClaimsSet claimSet = jwtProcessor.process(token, ctx);

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext c) throws BadJWTException {
                final String issuer = claimsSet.getIssuer();
                if (!vipAuthConfig.getIssuer().equals(issuer)) {
                    throw new BadJWTException("Invalid token issuer");
                }
            }
        });
        jwtProcessor.getJWTClaimsSetVerifier().verify(claimSet, ctx);

        Claim claim = new Claim();
        claim.setSub(claimSet.getClaim("sub").toString());
        claim.setExp(claimSet.getClaim("exp").toString());
        claim.setIat(claimSet.getClaim("iat").toString());
        claim.setContextName(claimSet.getClaim("context_name").toString());
        if (claimSet.getClaim("acct") != null){
            claim.setAcct(claimSet.getClaim("acct").toString());
        }
        if (claimSet.getClaim("domain") != null){
            claim.setDomain(claimSet.getClaim("domain").toString());
        }
        if (claimSet.getClaim("context") != null){
            claim.setContext(claimSet.getClaim("context").toString());
        }
        if (claimSet.getStringArrayClaim("perms") != null){
            claim.setPerms(claimSet.getStringArrayClaim("perms"));
        }
        return claim;
    }

    //fetch alg for given CSP key set
    private JWSAlgorithm getKeyAlg(JWK key) throws BadJOSEException {
        if (key.getKeyType().toString().equals("RSA")) {
            return JWSAlgorithm.RS256;
        } else {
            throw new BadJOSEException("Unsupported algorithm by CSP");
        }
    }

    private synchronized void callCspJwksEndpoint()
    {
        try {
            url = new URL(vipAuthConfig.getCspAuthUrl());
            jwksInMem = JWKSet.load(url);
            keyRotateEndpointLastAccess = Instant.now();
        } catch (final MalformedURLException e) {
            LOGGER.error("End Point URL not proper", e);
        } catch (final IOException e) {
            LOGGER.error("IO issue", e);
        } catch (final ParseException e) {
            LOGGER.error("JSON KeySet is not proper, could not parse", e);
        }
    }

    private synchronized boolean allowRefreshJwkset(int elapsed) {
        if (Instant.now().compareTo(keyRotateEndpointLastAccess.plusSeconds(elapsed)) > 0) {
            callCspJwksEndpoint();
            return true;
        }
        return false;
    }
}

