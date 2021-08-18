/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.csp.service.JwtTokenService;
import com.vmware.vip.core.login.ADAuthenticator;
import com.vmware.vip.core.login.VipAuthConfig;
import com.vmware.vip.core.validation.ValidationException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;



@RestController("authentication-authenticationLoginAPI")  
@Api(value = "Login Controller login operations")
public class AuthenticationLoginController {
	private static Logger logger = LoggerFactory.getLogger(AuthenticationLoginController.class);
	
	public final static String INVALID_LOGIN = "Invalid Login username, password error or authentication is expired";
	

	@Autowired
	private JwtTokenService tokenService;
	
	@Autowired
	private VipAuthConfig authConfig;
	

	@PostMapping("/auth/login")
	public APIResponseDTO vipLogin(
			 @ApiParam(name = APIParamName.USERNAME, value = APIParamValue.USERNAME) 
			 @RequestParam(value = APIParamName.USERNAME) String username,
			 @ApiParam(name = APIParamName.PASSWORD, value = APIParamValue.PASSWORD) 
			 @RequestParam(value = APIParamName.PASSWORD) String password,
			 @ApiParam(name = APIParamName.EXPIREDAYS, value = APIParamValue.EXPIREDAYS) 
			 @RequestParam(value = APIParamName.EXPIREDAYS) Integer expireDays
			) throws ValidationException {
		logger.info("{} begin to login", username );
		logger.debug(password);
		
		String userId = ADAuthenticator.doLogin(username, password, authConfig.getLdapServerUri(), authConfig.getTdomain(), authConfig.getSearchbase());
		
		if(userId != null) {
		   
			String tokenJason = tokenService.createLoginToken(username, expireDays);
			Map<String, String> map = new HashMap<String, String>();
			map.put("username", username);
			map.put("authentication", tokenJason);
						
			APIResponseDTO d = new APIResponseDTO();
			d.setData(map);
			d.getResponse().setServerTime(LocalDateTime.now().toString());
			logger.info("{} login successfully", username);
	        return d;
		}else {
			logger.warn("{} login failure", username);
			throw new ValidationException(INVALID_LOGIN);
		}
	}
	
	
	
	@PostMapping(value = "/auth/token")
	public APIResponseDTO generateToken(@RequestHeader(required = true) String authentication,@RequestParam String appId) {
	
		logger.debug(authentication+"-----------------"+appId);
		String username = null;
		APIResponseDTO d = new APIResponseDTO();
		try {
			username = tokenService.verifyToken(authentication).get("username").asString();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			Response r = new Response();
			r.setCode(HttpStatus.UNAUTHORIZED.value());
			r.setMessage("The authentication fails, please validate the token!");
			r.setServerTime(LocalDateTime.now().toString());
			d.setResponse(r);
		    return d;
		}
    	
		String result = tokenService.createAPPToken(appId, username);
		Map<String,Object> data = new HashMap<String,Object>();
	    data.put("token", result);
		d.setData(data);
		d.getResponse().setServerTime(LocalDateTime.now().toString());
		return d;
	}
	
}
