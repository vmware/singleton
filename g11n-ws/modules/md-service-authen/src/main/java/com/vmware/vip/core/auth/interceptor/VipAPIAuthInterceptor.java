/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.core.csp.service.JwtTokenService;
import com.vmware.vip.core.login.TokenObj;
import com.vmware.vip.core.security.RSAUtils;

@Component
public class VipAPIAuthInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(VipAPIAuthInterceptor.class); 
	private static final String AUTH_TOKEN = "token";
	private static final String AUTH_AUTH = "authorization";
    @Autowired
	private JwtTokenService jwtService;
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
	  
		logger.debug("VipAPIAuthInterceptor----------------------------preHandle---------------");
		
		String token = request.getHeader(AUTH_TOKEN);
	    
	    
		  if (token != null) {
			  
			 String tokenStr =  RSAUtils.rsaDecrypt(token);
			  ObjectMapper objectMapper = new ObjectMapper();
			  
			  TokenObj tokenObj =  objectMapper.readValue(tokenStr, TokenObj.class);
			  
			  if(tokenObj.getExpTime()> System.currentTimeMillis()) {
				  return true;
			  }
			  } else {
				  
					
					String authorization = request.getHeader(AUTH_AUTH);
				    
				  if (authorization != null) {
					  
					  boolean verf = false;
					  try {
						  String username = jwtService.verifyToken(authorization).get("username").asString();
						  
						  request.setAttribute("username", username);
							verf= true;
						} catch (Exception e) {
							// TODO Auto-generated catch block
						
							logger.error(e.getMessage(), e);
							
						
			
						}
					  
					  if (verf) {
						// The user is not authenticated.
						  return true;
					  }
					}
				  
				  
			  }
		  
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "you token has expired or other authorization error!!!");
            response.setHeader("UNAUTHORIZED", "token has expired or other authorization error!!!");
			logger.error("token has expired or other authorization error!!!");
			return false;
			
		} 
		
	
		
		
	

}
