/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;


@Service
public class CspValidateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CspValidateService.class);

    @Value("${csp.auth.issuer:###}")
    private String issuer;
    @Value("${csp.auth.url:###}")
    private String jwksUri;
    @Value("${csp.auth.refresh-interval-sec:30}")
    private int refreshIntervalSec;
    private Instant keyRotateEndpointLastAccess;
    private URL url;
    private JWKSet jwksInMem;

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
    public CspToken getTokenClaims(final String token) {
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

    private CspToken validate(String token) throws ParseException, BadJOSEException, JOSEException {
        if (token == null) return null;

        SignedJWT signedJWT = SignedJWT.parse(token);
        String tokenKid = signedJWT.getHeader().getKeyID();

        JWK matchKey = null;
        for (JWK key : jwksInMem.getKeys()) {
            if (key.getKeyID().equals(tokenKid)) {
                matchKey = key;
                break;
            }
        }

        if ((matchKey == null) && !allowRefreshJwkset(getRefreshIntervalSec())) {
            LOGGER.info("Trying to hit public key endpoint within {} sec, possibly a DoS (Denial of Service) attack",
                    getRefreshIntervalSec());
        }

        if (matchKey == null) return null;
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
                LOGGER.info("the remote issuer:{}", issuer);
                if (!getIssuer().equals(issuer)) {
                    throw new BadJWTException("Invalid token issuer");
                }
            }
        });
        jwtProcessor.getJWTClaimsSetVerifier().verify(claimSet, ctx);

        CspToken cspToken = new CspToken();
        cspToken.setSub(claimSet.getClaim("sub").toString());
        cspToken.setExp(claimSet.getClaim("exp").toString());
        cspToken.setIat(claimSet.getClaim("iat").toString());
        cspToken.setAcct(claimSet.getClaim("acct").toString());
        cspToken.setDomain(claimSet.getClaim("domain").toString());
        cspToken.setContext(claimSet.getClaim("context").toString());
        cspToken.setContextName(claimSet.getClaim("context_name").toString());
        cspToken.setPerms(claimSet.getStringArrayClaim("perms"));
        return cspToken;
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
            url = new URL(getJwksUri());
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

    public String getIssuer() {
        return issuer;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public int getRefreshIntervalSec() {
        return refreshIntervalSec;
    }

}

