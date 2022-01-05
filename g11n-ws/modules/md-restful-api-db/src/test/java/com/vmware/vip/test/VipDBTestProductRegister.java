/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.DBTestAPP;
import com.vmware.vip.ProfilesResolver;
import com.vmware.vip.api.domain.DbApiUrl;
import com.vmware.vip.api.domain.DbResponseStatus;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DBTestAPP.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = ProfilesResolver.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)  
public class VipDBTestProductRegister {

/*	  @Autowired  
	  private WebApplicationContext context; */
	  
	 @Autowired
	 private TestRestTemplate testRestTemplate;
	 
	 private String baseUrl =DbApiUrl.API_ROOT;
	 private Logger logger = LoggerFactory.getLogger(VipDBTestProductRegister.class);
	 
	 private String viptest="Testing";
	 private ObjectMapper objectMapper = new ObjectMapper();

 @Test
 public void test000Prepare() throws JsonParseException, JsonMappingException, IOException {

	 logger.info("----------------------test prepare-----------------------------------------");
	 String  resultconn = testRestTemplate.getForObject(baseUrl+"/test",String.class);
	 logger.info("conn test result:"+resultconn);
	 
	 String url = baseUrl+"/product/{productName}";
	  Map<String,String> multiValueMap = new HashMap<>();
      multiValueMap.put("productName",viptest);
	 
      String  result = testRestTemplate.getForObject(url,String.class, multiValueMap);
      logger.info(result);
      
      DbResponseStatus resultStatus =  objectMapper.readValue(result , DbResponseStatus.class);
	 
	 if(resultStatus.getCode() == 1 ) {
		 ResponseEntity<String> resultAdd = testRestTemplate.exchange(url, HttpMethod.PUT,null,String.class,multiValueMap);
	     logger.info(resultAdd.getBody());
	  }
      
      
	 
	 
 }
 
 
	 

 @Test
 public void test001DelProduct() throws JsonParseException, JsonMappingException, IOException {

	 String url = baseUrl+"/product/{productName}";
	 logger.info("----------------------test Del product-----------------------------------------");
	 logger.info(url);
	  Map<String,String> multiValueMap = new HashMap<>();
      multiValueMap.put("productName",viptest);
      
	 ResponseEntity<String> resultdel = testRestTemplate.exchange(url, HttpMethod.DELETE,null,String.class,multiValueMap);
	   
    logger.info(resultdel.getBody());
    
    logger.info(resultdel.getBody());
    DbResponseStatus resultStatus =  objectMapper.readValue(resultdel.getBody() , DbResponseStatus.class);
   Assert.assertTrue(resultStatus.getCode() == 0);
    
    
 }
 
 
 
 @Test
 public void test002AddProduct() throws JsonParseException, JsonMappingException, IOException {
	 
	 String url = baseUrl+"/product/{productName}";
	 logger.info("----------------------test Add product-----------------------------------------");
	 logger.info(url);
	 Map<String,String> multiValueMap = new HashMap<>();
	 multiValueMap.put("productName",viptest);

	  ResponseEntity<String> result = testRestTemplate.exchange(url, HttpMethod.PUT,null,String.class,multiValueMap);
   
     
     
     logger.info(result.getBody());
      DbResponseStatus resultStatus =  objectMapper.readValue(result.getBody() , DbResponseStatus.class);
     Assert.assertTrue(resultStatus.getCode() == 0);
     
    
 }
 
 
 
 @Test
 public void test003GetProudct() throws JsonParseException, JsonMappingException, IOException {
	 String url = baseUrl+"/product/{productName}";
	  Map<String,String> multiValueMap = new HashMap<>();
     multiValueMap.put("productName",viptest);
	 
     String  result = testRestTemplate.getForObject(url,String.class, multiValueMap);
     logger.info("----------------------test Get product -----------------------------------------");
     logger.info(result);
     
     DbResponseStatus resultStatus =  objectMapper.readValue(result , DbResponseStatus.class);
	 
     Assert.assertTrue(resultStatus.getCode() == 0);
     
     
     
 }
	
	
}
