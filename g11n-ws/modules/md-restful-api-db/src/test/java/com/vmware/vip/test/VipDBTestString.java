/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
import com.vmware.vip.messages.data.dao.model.ResultI18Message;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DBTestAPP.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(resolver = ProfilesResolver.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class VipDBTestString {
	@Autowired
	private TestRestTemplate testRestTemplate;

	private String baseUrl = DbApiUrl.API_ROOT;
	
	private Logger logger = LoggerFactory.getLogger(VipDBTestString.class);

	InputStream jsonInputStream = TestDbJsonUtil.getZh_CNJson();
	private String viptest = "Testing";
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void test000Prepare() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test prepare-----------------------------------------");

		String resultconn = testRestTemplate.getForObject(baseUrl + "/test", String.class);
		logger.info("conn test result:" + resultconn);

		String url = baseUrl + "/product/{productName}";
		Map<String, String> multiValueMap = new HashMap<>();
		multiValueMap.put("productName", viptest);

		String result = testRestTemplate.getForObject(url, String.class, multiValueMap);
		logger.info(result);

		DbResponseStatus resultStatus = objectMapper.readValue(result, DbResponseStatus.class);

		if (resultStatus.getCode() == 1) {
			ResponseEntity<String> resultAdd = testRestTemplate.exchange(url, HttpMethod.PUT, null, String.class,
					multiValueMap);
			logger.info(resultAdd.getBody());
		}

		// ----------------------------

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> delComp = new HashMap<>();

		delComp.put("productName", i18msg.getProduct());
		delComp.put("version", i18msg.getVersion());
		delComp.put("component", i18msg.getComponent());
		delComp.put("locale", i18msg.getLocale());
		
	
		
		 String compUrl = DbApiUrl.API_ROOT + "/component/{productName}/{version}/{component}/{locale}/";
		
		testRestTemplate.exchange(compUrl, HttpMethod.DELETE, null, String.class, delComp);
		
		
		
		HttpHeaders requestHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		requestHeaders.setContentType(type);
		// body
		
		String requestBody = objectMapper.writeValueAsString(i18msg.getMessages());
		
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		  testRestTemplate.exchange(compUrl, HttpMethod.PUT, requestEntity, String.class,
				  delComp);
	}
	
	
	
	@Test
	public void test001AddString() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Add String-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());

		HttpHeaders requestHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		requestHeaders.setContentType(type);
		// body
		String requestBody = "{\r\n" + 
				"  \"vipteststr.email\": \"this is a test\",\r\n" + 
				"  \"abcd\":\"this is abcd\"\r\n" + 
				"}";
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		
		String addStrUrl = baseUrl+"/string/{productName}/{version}/{component}/{locale}/";
		ResponseEntity<String> result = testRestTemplate.exchange(addStrUrl, HttpMethod.PUT, requestEntity, String.class,
				multiValueMap);

		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}
	
	
	@Test
	public void test002UpdateString() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Update String-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());

		HttpHeaders requestHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		requestHeaders.setContentType(type);
		// body
		String requestBody = "{\r\n" + 
				"  \"vipteststr.email\": \"this is a test\",\r\n" + 
				"  \"abcd\":\"updateNewValue\"\r\n" + 
				"}";
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		
		String strUrl = baseUrl+"/string/{productName}/{version}/{component}/{locale}/";
		ResponseEntity<String> result = testRestTemplate.exchange(strUrl, HttpMethod.POST, requestEntity, String.class,
				multiValueMap);

		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}
	
	
	
	@Test
	public void test004GetString() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test GET String-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		//logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());
		multiValueMap.put("key", "abcd");
		String strUrl = baseUrl+"/string/{productName}/{version}/{component}/{locale}/{key}/";
		ResponseEntity<String> result = testRestTemplate.exchange(strUrl, HttpMethod.GET, null, String.class,
				multiValueMap);
		logger.info(result.getBody());
		
		Assert.assertTrue(result.getBody().contains("updateNewValue"));
		
	//	Assert.assertNotNull(resulti18msg);

	}

	
	

	@Test
	public void test005MultGetString() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test GET MUlt String-----------------------------------------");


		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());

		HttpHeaders requestHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		requestHeaders.setContentType(type);
		// body
		String requestBody = "{\"keys\": [\"vipteststr.email\", \"abcd\",\"sim.home\"]}";
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		
		String strUrl = baseUrl+"/multString/{productName}/{version}/{component}/{locale}/";
		ResponseEntity<String> result = testRestTemplate.exchange(strUrl, HttpMethod.POST, requestEntity, String.class,
				multiValueMap);

		logger.info(result.getBody());
		
		//Assert.assertNotNull(result.getBody());

	}
	
	
	@Test
	public void test006DeleteString() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Delete String-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		//logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());
		multiValueMap.put("key", "abcd");
		String strUrl = baseUrl+"/string/{productName}/{version}/{component}/{locale}/{key}/";
		ResponseEntity<String> result = testRestTemplate.exchange(strUrl, HttpMethod.DELETE, null, String.class,
				multiValueMap);
		logger.info(result.getBody());
		
		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}
	
	

	
}
