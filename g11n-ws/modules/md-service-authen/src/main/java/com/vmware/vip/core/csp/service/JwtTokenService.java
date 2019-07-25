/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vmware.vip.core.login.VipAuthConfig;

@Service
public class JwtTokenService {
	private final static String SECRET = UUID.randomUUID().toString();
	private static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
	@Autowired
	private VipAuthConfig authConfig;
	
	private int expiresTime;
	

	public String createLoginToken(String username) {
		Date iatDate = new Date();
		Calendar nowTime = Calendar.getInstance();
		if (authConfig.getSessionExpire() < 1) {
			this.expiresTime = 30;
		}else {
			this.expiresTime= authConfig.getSessionExpire();
		}

		nowTime.add(Calendar.MINUTE, expiresTime);
		Date expriesDate = nowTime.getTime();

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("alg", "HS256");

		map.put("typ", "JWT");
		String token = null;
		try {
			token = JWT.create().withHeader(map).withClaim("username", username).withExpiresAt(expriesDate)
					.withIssuedAt(iatDate).sign(Algorithm.HMAC256(SECRET));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (JWTCreationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return token;

	}
	
	
	
	
	
	public  Map<String, Claim> verifyToken(String token) throws Exception{
		JWTVerifier verifier = null;
		verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
		DecodedJWT decoded = null;
		try {
		    decoded = verifier.verify(token);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} 
	
	  return decoded.getClaims();
	}
	

}
