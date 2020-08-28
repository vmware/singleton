/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.login.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.csp.service.JwtTokenService;
import com.vmware.vip.core.csp.service.SqlLiteService;
import com.vmware.vip.core.login.ADAuthenticator;
import com.vmware.vip.core.login.AuthModel;
import com.vmware.vip.core.login.TokenObj;
import com.vmware.vip.core.login.VipAuthConfig;
import com.vmware.vip.core.login.VipUser;
import com.vmware.vip.core.security.RSAUtils;
import com.vmware.vip.core.validation.ValidationException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;



@RestController  
@Api(value = "Login Controller login operations")
public class AuthorizationLoginController {
	private static Logger logger = LoggerFactory.getLogger(AuthorizationLoginController.class);
	
	public final static String INVALID_LOGIN = "Invalid Login username, password error or Authorization is expired";
	
    private long period=((long)(1000*3600))*72; 
	@Autowired
	private JwtTokenService tokenService;
	
	@Autowired
	private VipAuthConfig authConfig;
	
	@Autowired
	private SqlLiteService sqlService;

	@PostMapping("/auth/login")
	  @ApiImplicitParams({ @ApiImplicitParam(paramType = "form", dataType = "String", name = "username", required = true),
	@ApiImplicitParam(paramType = "form", dataType = "String", name = "password", required = true)} )
	public  String vipLogin(@ApiIgnore @ModelAttribute VipUser user ) throws ValidationException {
		
		String username = user.getUsername();
		logger.info("{} begin to login", username );
		
		
		logger.debug(user.getPassword());
		
		String userId = ADAuthenticator.doLogin(user.getUsername(), user.getPassword(), authConfig.getLdapServerUri(), authConfig.getTdomain(), authConfig.getSearchbase());
		
		if(userId != null) {
		   
			String tokenJason = tokenService.createLoginToken(user.getUsername());
			sqlService.createAuth(user.getUsername());			
			tokenJason = "{\"username\":\""+user.getUsername()+"\", \"authorization\":\""+tokenJason+"\"}";
			logger.info("{} login successfully", username);
	        return tokenJason;
		}else {
			logger.warn("{} login failure", username);
			throw new ValidationException(INVALID_LOGIN);
		}
	}
	
	
	
	@PostMapping(value = "/auth/pubkey")
	public String addPubKey(@RequestHeader(required = true) String authorization,@RequestBody String pubkey) {
	
		String username = null;
		try {
			username = tokenService.verifyToken(authorization).get("username").asString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage());
			Response r = new Response();
			r.setCode(HttpStatus.UNAUTHORIZED.value());
			r.setMessage("you token has expired or other authorization error!!!");
			String result = null;
			try {
				result = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(r);
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				logger.error(e1.getMessage(), e1);
				result = e1.getMessage();
			}
		    return result;
		}
    	
   
		
		
		logger.info("current binding pubkey username is "+username);
		logger.info("pubkey is "+ pubkey);
		
		int result = sqlService.addPubkey(new AuthModel(username, pubkey));
	   
		
		
		if(result >0) {
			return "{\"result\":\"successful\"}";
			
		}else {
			return "{\"result\":\"add pubKey error\"}";
		}
		
	
		
	}
	
	
	
	@GetMapping(value = "/auth/pubkey")
	public String getPubKey(@RequestHeader(required = true) String authorization) {
	
		String username = null;
		try {
			username = tokenService.verifyToken(authorization).get("username").asString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
    	
   
		
		
		logger.info("current binding pubkey username is "+username);
	
		
		AuthModel result = sqlService.getAuth(username);
	   
		
		
		if(result != null) {
			return "{\"username\":\""+username+"\", \"pubkey\": \""+result.getPubkey()+"\"}";
			
		}else {
			return "{\"result\":\"get public key error\"}";
		}
		
	
		
	}
	

 
 
 

 @ApiIgnore
@GetMapping(value = "/auth/getcode/{username}")

public String getRequireCode(@PathVariable String username) {
	   AuthModel model = sqlService.getAuth(username);
	   String verfykey = username+String.valueOf(System.currentTimeMillis());
	   String result = null;
	    try {
			result =  RSAUtils.rsaEncrypt(verfykey, model.getPubkey());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	   
	   
	   if(result != null) {
		   
		  logger.debug("--------------verifykey---"+verfykey);
		   model.setVerifyKey(verfykey);
		   model.setUsername(username);
		   sqlService .addVerfyKey(model);
		   
		   return "{\"username\":\""+username+"\",\"code\":\""+result+"\"}";
	   }else {
		   return "{\"error\":\""+" can not find the verifykey"+"\"}";
	   }
	   
	   
	   
		
	}
	

  @ApiIgnore
  @PostMapping(value = "/auth/gettoken")
 public String getToken(@RequestBody AuthModel data) {
	   String verifykey = data.getVerifyKey();
	
	   AuthModel model = sqlService.getAuth(data.getUsername());
	   
	   if(verifykey.equals(model.getVerifyKey())) {
		   TokenObj obj = new TokenObj();
		   obj.setUsername(data.getUsername());
		   long currTime = System.currentTimeMillis();
		   
		  if(authConfig.getTokenExpire()>0) {
			  obj.setExpTime(currTime + ((1000*3600)*authConfig.getTokenExpire()));
		  }else {
			  obj.setExpTime(currTime + this.period);
		  }
		   
		   obj.setIssTime(currTime);
		   
		   
		   ObjectMapper objectMapper = new ObjectMapper();
		   
		   
		   String objStr = null;
		try {
			objStr = objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			logger.error(e1.getMessage(), e1);
		}
		   
		   String token = null;
		try {
			token = RSAUtils.rsaEncrypt(objStr);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		   
		   
		   return "{\"token\":\""+token+"\"}";
		   
	   }else {
		   return "{\"error\":\""+"get RSA Code error"+"\"}";
		   
	   }
	   
	   
		
	 }
}
