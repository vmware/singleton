/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

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
    private CSPTokenConfig cspTokenConfig;


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
    public Claim  getTokenClaims(final String token) {
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
        // token can be null at the begin of login
        if (token == null) return null;

        // Parse token header
        SignedJWT signedJWT = SignedJWT.parse(token);
        String tokenKid = signedJWT.getHeader().getKeyID();

        JWK matchKey = null;
        // check kid already exists in memory.
        for (JWK key : jwksInMem.getKeys()) {
            if (key.getKeyID().equals(tokenKid)) {
                matchKey = key;
                break;
            }
        }

        // refresh jwks into memory, only when key is not found in memory (i.e possibly key is rotated)//
        if ((matchKey == null) && !allowRefreshJwkset(cspTokenConfig.getRefreshIntervalSec())) {
            LOGGER.info(
                    "Trying to hit public key endpoint within {} sec, possibly a DoS (Denial of Service) attack",
                    cspTokenConfig.getRefreshIntervalSec());
        }

        if (matchKey == null)
            return null;

        JWSAlgorithm alg = getKeyAlg(matchKey);

        //Default timeout was 250 ms, hence custom timeout for n/w slow.
        ResourceRetriever resourceRetriever =
                new DefaultResourceRetriever(2000, 2000);

        // Set up a JWT processor to parse
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

        JWKSource keySource = new RemoteJWKSet(url, resourceRetriever);

        // Configure the key selector to feed matching public RSA key sourced from the JWK set URL
        JWSKeySelector keySelector = new JWSVerificationKeySelector(alg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // optional context parameter, not required here
        SecurityContext ctx = null;

        // Process the token, throw Exception if expired or can't not parse
        JWTClaimsSet claimSet = jwtProcessor.process(token, ctx);

        // issuer check
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier() {
            @Override
            public void verify(JWTClaimsSet claimsSet, SecurityContext c) throws BadJWTException {
                final String issuer = claimsSet.getIssuer();
                if (!cspTokenConfig.getIssuer().equals(issuer)) {
                    throw new BadJWTException("Invalid token issuer");
                }
            }
        });

        jwtProcessor.getJWTClaimsSetVerifier().verify(claimSet, ctx);

        Claim claim = new Claim();
        claim.setSub(claimSet.getClaim("sub").toString());
        claim.setExp(claimSet.getClaim("exp").toString());
        claim.setIat(claimSet.getClaim("iat").toString());
        claim.setAcct(claimSet.getClaim("acct").toString());
        claim.setDomain(claimSet.getClaim("domain").toString());
        claim.setContext(claimSet.getClaim("context").toString());
        claim.setContextName(claimSet.getClaim("context_name").toString());
        claim.setPerms(claimSet.getStringArrayClaim("perms"));
        return claim;
    }

    //fetch alg for given CSP key set
    private JWSAlgorithm getKeyAlg(JWK key) throws BadJOSEException {
        if (key.getKeyType().toString().equals("RSA")) {
            return JWSAlgorithm.RS256;
        }
        else throw new BadJOSEException("Unsupported algorithm by CSP");
    }

    private synchronized void callCspJwksEndpoint()
    {
        try {
            //set white listed CSP public key endpoint url
            url = new URL(cspTokenConfig.getJwksUri());
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
            // allow endpoint access, only after grace period elapsed
            callCspJwksEndpoint();
            return true;
        }
        return false;
    }
}

