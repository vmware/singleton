/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.core.login.VipAuthConfig;
import com.vmware.vip.core.security.RSAUtils;

@Service
public class JwtTokenService {


	private static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
	@Autowired
	private VipAuthConfig authConfig;
	

	public String createLoginToken(String username, int expiresDays) {
		Date iatDate = new Date();
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.DATE, expiresDays);
		Date expriesDate = nowTime.getTime();

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("alg", "HS256");

		map.put("typ", "JWT");
		String token = null;
		try {
			token = JWT.create().withHeader(map).withClaim("username", username).withExpiresAt(expriesDate)
					.withIssuedAt(iatDate).sign(Algorithm.HMAC256(authConfig.getJwtSecret()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
		return token;

	}
	
	
	
	
	
	public  Map<String, Claim> verifyToken(String token) throws Exception{
		JWTVerifier verifier = null;
		verifier = JWT.require(Algorithm.HMAC256(authConfig.getJwtSecret())).build();
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
		
	public String createAPPToken(String appId, String username) {
		Calendar nowTime = Calendar.getInstance();
		long issueTime = nowTime.getTimeInMillis();
		nowTime.add(Calendar.MINUTE, authConfig.getTokenExpire());
		long exprTime = nowTime.getTimeInMillis();
		
		StringBuilder sb = new StringBuilder();
		sb.append(appId);
		sb.append(ConstantsChar.COLON);
		sb.append(String.valueOf(exprTime));
		sb.append(ConstantsChar.COLON);
		sb.append(username);
		sb.append(ConstantsChar.COLON);
		sb.append(issueTime);
		
		try {
			return RSAUtils.encryptData(sb.toString(), authConfig.getPrivateKey());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} 

	}
	
	public boolean verifyAPPToken(String token, String appId) {
		
		 try {
			String tokenStr =  RSAUtils.decryptData(token, authConfig.getPublicKey());
			String[] tokens = tokenStr.split(ConstantsChar.COLON);
			String tokenAppId = tokens[0];
			long tokenExpTime = Long.parseLong(tokens[1]);
			if(tokenAppId.equals(appId) && tokenExpTime > System.currentTimeMillis()) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} 
	}
	
	
	

}
