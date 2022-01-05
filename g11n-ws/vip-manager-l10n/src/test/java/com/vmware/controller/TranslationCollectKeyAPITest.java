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
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationCollectKeyAPITest {
	 private static Logger logger = LoggerFactory.getLogger(TranslationCollectKeyAPITest.class);
		@Autowired
		private WebApplicationContext webApplicationContext;
		
		@Test
		public void test001collectV1StringTranslation() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API =L10nI18nAPI.TRANSLATION_KEY_APIV1;
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API).param(Constants.VERSION, "1.0.0")
					        .param(APIParamName.PRODUCT_NAME, "PRODUCTTEST")
					        .param(APIParamName.COMPONENT, "testComp")
					        .param(APIParamName.LOCALE, "en")
					        .param(APIParamName.KEY, "testsourcekey")
					        .param(APIParamName.SOURCE_FORMAT, "")
					        .param(APIParamName.COLLECT_SOURCE, "true")
							.param(Constants.SOURCE, "this open3's value")
							.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			 
				MvcResult mvcRS2 =mockMvc.perform(
						MockMvcRequestBuilders.post(API).param(Constants.VERSION, "1.0.0")
						        .param(APIParamName.PRODUCT_NAME, "PRODUCTTEST")
						        .param(APIParamName.COMPONENT, "testComp")
						       /// .param(APIParamName.LOCALE, "en")
						        .param(APIParamName.KEY, "testsourcenullkey")
						       // .param(APIParamName.SOURCE_FORMAT, "")
						        .param(APIParamName.COLLECT_SOURCE, "true")
								//.param(Constants.SOURCE, "this open3's value")
								//.param(Constants.COMMENT_FOR_SOURCE, "dc new string")
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
				
				 String resultStr2 =   mvcRS2.getResponse().getContentAsString();
				   
				   logger.info(resultStr2);
			
		}

		
		
		@Test
		public void test002collectV1KeyTranslation() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.TRANSLATION_PRODUCT_COMOPONENT_KEY_APIV1
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
					.replace("{" + APIParamName.COMPONENT + "}", "default")
					.replace("{" +APIParamName.KEY2+ "}", "dc.myhome.open3");
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API)
					        .param(Constants.VERSION, "1.1.0")
					        .param(APIParamName.LOCALE, "en")
							.param(APIParamValue.SOURCE, "this open3's value")
							.param(APIParamName.COMMENT_SOURCE, "dc new string")
							.param(APIParamName.COLLECT_SOURCE, "true")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			   MvcResult mvcRS2 =mockMvc.perform(
						MockMvcRequestBuilders.post(API)
						        .param(Constants.VERSION, "1.1.0")
						        .content("test body request")
								.param(APIParamName.COLLECT_SOURCE, "true")
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
				
				 String resultStr2 =   mvcRS2.getResponse().getContentAsString();
				   
				   logger.info(resultStr2);
		}
		
		@Test
		public void test003collectV1KeyTranslationNoComponent() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.TRANSLATION_PRODUCT_NOCOMOPONENT_KEY_APIV1
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "NOComponent")
					//.replace("{" + APIParamName.COMPONENT + "}", "default")
					.replace("{" +APIParamName.KEY2+ "}", "dc.myhome.open3");
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API)
					        .param(Constants.VERSION, "1.1.0")
					        .param(APIParamName.LOCALE, "en")
							.param(APIParamValue.SOURCE, "this open3's value")
							.param(APIParamName.COMMENT_SOURCE, "dc new string")
							.param(APIParamName.SOURCE_FORMAT, "")
							.param(APIParamName.COLLECT_SOURCE, "true")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			   MvcResult mvcRS2 =mockMvc.perform(
						MockMvcRequestBuilders.post(API)
						        .param(Constants.VERSION, "1.1.0")
						        .param(APIParamName.LOCALE, "en")
								.param(APIParamValue.SOURCE, "this open3's value")
								.param(APIParamName.COMMENT_SOURCE, "dc new string")
								.param(APIParamName.COLLECT_SOURCE, "true")
								.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk()).andReturn();
				
				 String resultStr2 =   mvcRS2.getResponse().getContentAsString();
				   
				   logger.info(resultStr2);
		}
		
		@Test
		public void test004collectV2KeyTranslation() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.KEY_TRANSLATION_APIV2
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "TestCenter")
					.replace("{" + APIParamName.VERSION + "}", "2.0.0")
					.replace("{" + APIParamName.COMPONENT + "}", "testing")
					.replace("{" + APIParamName.LOCALE + "}", "en")
					.replace("{" +APIParamName.KEY2+ "}", "testkey");
			 logger.info(API);
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API)
							.content("this open3's value")
							.param(APIParamName.COMMENT_SOURCE, "dc new string")
							.param(APIParamName.SOURCE_FORMAT, "")
							.param(APIParamName.COLLECT_SOURCE, "true")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			
		}
		
		@Test
		public void test005collectV2KeysTranslation() throws Exception {
			
			String jsonStr = "[\r\n" + 
					"  {\r\n" + 
					"    \"commentForSource\": \"testa comment\",\r\n" + 
					"    \"key\": \"testa\",\r\n" + 
					"    \"source\": \"this is a testa source\"\r\n" + 
					"  }\r\n" + ","+
					"  {\r\n" + 
					"    \"commentForSource\": \"testb comment\",\r\n" + 
					"    \"key\": \"testb\",\r\n" + 
					"    \"source\": \"this is a testb source\"\r\n" + 
					"  }\r\n" + 
					"]";
			logger.info(jsonStr);
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.KEYS_TRANSLATION_APIV2
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
					.replace("{" + APIParamName.VERSION + "}", "2.1.0")
					.replace("{" + APIParamName.COMPONENT + "}", "testmult")
					.replace("{" + APIParamName.LOCALE + "}", "en");
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API)
							.content(jsonStr)
							.param(APIParamName.COLLECT_SOURCE, "true")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			
		}
}
