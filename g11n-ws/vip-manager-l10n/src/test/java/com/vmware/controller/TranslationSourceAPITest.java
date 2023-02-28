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
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationSourceAPITest {
	 private static Logger logger = LoggerFactory.getLogger(TranslationSourceAPITest.class);
		@Autowired
		private WebApplicationContext webApplicationContext;
		
		
		@Test
		public void test001postTranslationBySource() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.TRANSLATION_SOURCE_APIV1
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "TestSource")
					.replace("{" + APIParamName.COMPONENT + "}", "default");
					
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API).param(Constants.VERSION, "1.0.0")
					        .param(APIParamName.LOCALE, "en")
					        .param(APIParamName.SOURCE_FORMAT, "")
					        .param(APIParamName.COLLECT_SOURCE, "true")
					        .content("this a test source ")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			
		}
		
		@Test
		public void test002v2createsource() throws Exception {
			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
					webApplicationContext).build();
			String API = L10nI18nAPI.TRANSLATION_SOURCE_APIV2
					.replace("{" + APIParamName.PRODUCT_NAME + "}", "devTest")
					.replace("{" + APIParamName.VERSION + "}", "2.2.0")
					.replace("{" + APIParamName.COMPONENT + "}", "test")
					.replace("{" + APIParamName.LOCALE + "}", "en");
			MvcResult mvcRS =mockMvc.perform(
					MockMvcRequestBuilders.post(API)
					        .param(APIParamName.SOURCE_FORMAT, "")
					        .param(APIParamName.COLLECT_SOURCE, "true")
							.content("this a test v2 source ")
							.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andReturn();
			
			 String resultStr =   mvcRS.getResponse().getContentAsString();
			   
			   logger.info(resultStr);
			
			
		}

}
