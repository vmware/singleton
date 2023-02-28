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
import java.util.List;
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
import com.fasterxml.jackson.core.type.TypeReference;
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
public class VipDBTestCompProduct {

	@Autowired
	private TestRestTemplate testRestTemplate;

	private String baseUrl = DbApiUrl.API_ROOT;
	private String compUrl = DbApiUrl.API_ROOT + "/component/{productName}/{version}/{component}/{locale}/";
	private Logger logger = LoggerFactory.getLogger(VipDBTestCompProduct.class);

	InputStream jsonInputStream = TestDbJsonUtil.getEn_USJson();
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

		testRestTemplate.exchange(compUrl, HttpMethod.DELETE, null, String.class, delComp);

	}

	@Test
	public void test001AddComp() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Add Component-----------------------------------------");

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
		String requestBody = objectMapper.writeValueAsString(i18msg.getMessages());
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		ResponseEntity<String> result = testRestTemplate.exchange(compUrl, HttpMethod.PUT, requestEntity, String.class,
				multiValueMap);

		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}

	@Test
	public void test003UpateComp() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Update Component-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		// logger.info(sb.toString());

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

		i18msg.getMessages().clear();

		i18msg.setMessages(multiValueMap);

		String requestBody = objectMapper.writeValueAsString(i18msg.getMessages());
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		ResponseEntity<String> result = testRestTemplate.exchange(compUrl, HttpMethod.POST, requestEntity, String.class,
				multiValueMap);

		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}

	@Test
	public void test004GetComp() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test GET Component-----------------------------------------");

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

		ResponseEntity<String> result = testRestTemplate.exchange(compUrl, HttpMethod.GET, null, String.class,
				multiValueMap);

		logger.info(result.getBody());
		
		
		ResultI18Message resulti18msg = objectMapper.readValue(result.getBody(), ResultI18Message.class);
		

		Assert.assertNotNull(resulti18msg);

	}

	@Test
	public void test005MulComp() throws JsonParseException, JsonMappingException, IOException {
		logger.info("----------------------test mult Component-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());

		HttpHeaders requestHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		requestHeaders.setContentType(type);
		// body

		String requestBody = "{ \"components\": [\"JAVA\",\"default\"],\r\n"
				+ " 	\"locales\": [\"de\",\"zh_CN\",\"en_US\"]}";
		HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);
		String multCompUrl = baseUrl + "/multComponent/{productName}/{version}/";
		ResponseEntity<String> result = testRestTemplate.exchange(multCompUrl, HttpMethod.POST, requestEntity,
				String.class, multiValueMap);

		logger.info(result.getBody());
		

		List<ResultI18Message> resulti18msglist = objectMapper.readValue(result.getBody(),new TypeReference<List<ResultI18Message>>() { });
		

		Assert.assertTrue(resulti18msglist.size()>0);
	
	}

	@Test
	public void test006CompList() throws JsonParseException, JsonMappingException, IOException {
		logger.info("----------------------test product Component list-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());

		String multCompUrl = baseUrl + "/componentList/{productName}/{version}/";
		ResponseEntity<String> result = testRestTemplate.exchange(multCompUrl, HttpMethod.GET, null, String.class,
				multiValueMap);

		logger.info(result.getBody());
		Assert.assertNotNull(result.getBody());
	}

	@Test
	public void test007LocaleList() throws JsonParseException, JsonMappingException, IOException {
		logger.info("----------------------test product Locale list-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());

		String multCompUrl = baseUrl + "/localeList/{productName}/{version}/";
		ResponseEntity<String> result = testRestTemplate.exchange(multCompUrl, HttpMethod.GET, null, String.class,
				multiValueMap);

		logger.info(result.getBody());
		Assert.assertNotNull(result.getBody());
	}

	@Test
	public void test008DelComp() throws JsonParseException, JsonMappingException, IOException {

		logger.info("----------------------test Delete Component-----------------------------------------");

		BufferedReader br = new BufferedReader(new InputStreamReader(jsonInputStream));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		logger.info(sb.toString());

		ResultI18Message i18msg = objectMapper.readValue(sb.toString(), ResultI18Message.class);

		Map<String, String> multiValueMap = new HashMap<>();

		multiValueMap.put("productName", i18msg.getProduct());
		multiValueMap.put("version", i18msg.getVersion());
		multiValueMap.put("component", i18msg.getComponent());
		multiValueMap.put("locale", i18msg.getLocale());

		ResponseEntity<String> result = testRestTemplate.exchange(compUrl, HttpMethod.DELETE, null, String.class,
				multiValueMap);

		logger.info(result.getBody());

		logger.info(result.getBody());
		DbResponseStatus resultStatus = objectMapper.readValue(result.getBody(), DbResponseStatus.class);
		Assert.assertTrue(resultStatus.getCode() == 0);

	}

}
