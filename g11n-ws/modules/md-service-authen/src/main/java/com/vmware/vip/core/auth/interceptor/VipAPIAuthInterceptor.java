/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth.interceptor;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.csp.service.JwtTokenService;

@Component
public class VipAPIAuthInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(VipAPIAuthInterceptor.class); 
	private static final String AUTH_TOKEN = "token";
	private static final String AUTH_APPID = "appId";
	
    @Autowired
	private JwtTokenService jwtService;
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
	  
		logger.debug("VipAPIAuthInterceptor----------------------------preHandle---------------");
		
		String token = request.getHeader(AUTH_TOKEN);
	    
	    
		  if (token != null) {
			  logger.info("token {}", token);
			  String appId = request.getHeader(AUTH_APPID);
			  if(jwtService.verifyAPPToken(token, appId)) {
				  return true;
			  }
			  
		  }
		    
		    response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader("unauthorized", "The authentication fails, please validate the token!");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            APIResponseDTO d = new APIResponseDTO();
            Response r = new Response();
    		r.setCode(response.getStatus());
    		r.setMessage(response.getHeader("unauthorized"));
    		r.setServerTime(LocalDateTime.now().toString());
    		d.setResponse(r);
            String json = new ObjectMapper().writeValueAsString(d);	
            logger.error(response.getHeader("unauthorized"));
            response.getWriter().write(json);
            response.getWriter().flush();
            response.getWriter().close();
			return false;
			
		} 
		
}
