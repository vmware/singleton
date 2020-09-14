/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vmware.vip.core.login.VipAuthConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;


@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class.getName());
    private static final Clock CLOCK = DefaultClock.INSTANCE;
    private static final String KEYS_ALGORITHM = "RSA";
    @Autowired
    private VipAuthConfig config;
    private final RestTemplate restTemplate = new RestTemplate();
    /**
     * Public Key Caching, so we will retrieve it only once
     */
    private PublicKey publicKey;
    private String publicKeyIssuer;

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
     * @param token to validate and extract it's claims
     * @return the token claims
     */
    public Claims getTokenClaims(final String token) {
        if (publicKeyIssuer == null || publicKey == null) {
            populatePublicKeyDetails();
        }
        try {
            final Jws<Claims> claimsJws = Jwts.parser()
                    .setClock(CLOCK)
                    .requireIssuer(publicKeyIssuer)
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);
            if (claimsJws != null) {
                return claimsJws.getBody();
            }
        } catch (final ExpiredJwtException e) {
            LOGGER.error("JWT token has expired");
        } catch (final JwtException e) {
            LOGGER.error("JWT token is unsupported");
        } catch (final Exception e) {
            LOGGER.error("Unknown exception");
        }
        return null;
    }

    /**
     * Get the public key details (value and issuer) for CSP API
     */
    private void populatePublicKeyDetails() {
        try {
	        final PublicKeyResponse response = restTemplate.getForObject(config.getCspAuthUrl(), PublicKeyResponse.class);
	       	publicKeyIssuer = response.getIssuer();
	        final String rawPublicKey = response.getValue();
	        String pem = rawPublicKey.replaceAll("-----BEGIN (.*)-----", "")
	                .replaceAll("-----END (.*)----", "")
	                .replaceAll("\n", "");
            publicKey = KeyFactory.getInstance(KEYS_ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder()
                            .decode(pem)));
        } catch (Exception e) {
            LOGGER.error("Failed to generate public key");
        }
    }

}
