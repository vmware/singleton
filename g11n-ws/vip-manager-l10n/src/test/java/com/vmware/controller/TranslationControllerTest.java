/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.common.Constants;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;

/**
 * This class is the test case for source collect API.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationControllerTest {
	 private static Logger logger = LoggerFactory.getLogger(TranslationControllerTest.class);
	@Autowired
	private WebApplicationContext webApplicationContext;

	/**
	 * Test the source cache API.
	 * 
	 * @throws Exception
	 */
	/*@Test
	public void testAddStringForTranslation() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String API = L10NAPIV1.CREATE_SOURCE_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", "devCenter")
				.replace("{" + APIParamName.COMPONENT + "}", "default")
				.replace("{" +APIParamName.KEY2+ "}", "dc.myhome.open3");
		mockMvc.perform(
				post(API).param(Constants.VERSION, "1.0.0")
						.param(Constants.SOURCE, "dc.myhome.open3")
						.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(Constants.EXPECTRESULT));
	}
	*/
	
	
	@Test
	public void test001addStringForTranslation() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String API = L10NAPIV1.CREATE_SOURCE_GET
				.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
				.replace("{" + APIParamName.COMPONENT + "}", "default")
				.replace("{" +APIParamName.KEY2+ "}", "dc.myhome.open3");
		MvcResult mvcRS =mockMvc.perform(
				MockMvcRequestBuilders.get(API).param(Constants.VERSION, "1.0.0")
						.param(Constants.SOURCE, "this open3's value")
						.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		 String resultStr =   mvcRS.getResponse().getContentAsString();
		   
		   logger.info(resultStr);
		
		
	}
	
	
	@Test
	public void test002postSourceByKey() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String API = L10NAPIV1.CREATE_SOURCE_POST
				.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
				.replace("{" + APIParamName.COMPONENT + "}", "default")
				.replace("{" +APIParamName.KEY2+ "}", "dc.myhome.open3");
		MvcResult mvcRS =mockMvc.perform(
				MockMvcRequestBuilders.post(API).param(Constants.VERSION, "1.0.0")
						.param(Constants.SOURCE, "this open3's value")
						.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		 String resultStr =   mvcRS.getResponse().getContentAsString();
		   
		   logger.info(resultStr);
		
		
	}

	@Test
	public void test003postSourceByKey() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String API = L10NAPIV1.CREATE_SOURCE_POST
				.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
				.replace("{" + APIParamName.COMPONENT + "}", "default")
				.replace("{" +APIParamName.KEY2+ "}", "jsonkeyset");
		MvcResult mvcRS =mockMvc.perform(
				MockMvcRequestBuilders.post(API).param(Constants.VERSION, "1.0.0")
						.param(Constants.SOURCE, "{\"key1\":\"value1\", \"key2\":\"value2\"}")
						.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String resultStr =   mvcRS.getResponse().getContentAsString();
		logger.info(resultStr);
	}

}
