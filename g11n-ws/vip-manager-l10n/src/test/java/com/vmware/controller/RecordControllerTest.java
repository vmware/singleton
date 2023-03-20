/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;

/**
 * 
 *
 * @author shihu
 *
 *this class is the source collect from out of firewall
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class RecordControllerTest {
  private static Logger logger = LoggerFactory.getLogger(RecordControllerTest.class);
  private final static String GATEWAYPREF="/i18n"; 
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	
	@Test
	public void test000getRecoredModel() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String urlStr = GATEWAYPREF+L10NAPIV1.API_L10N+"/records";
		
		MvcResult mvcRS = mockMvc.perform(MockMvcRequestBuilders.get(urlStr)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	   String resultStr =   mvcRS.getResponse().getContentAsString();
	   
	   logger.info(resultStr);
	
	}
	
	

	@Test
	public void test002getSourceComponentModel() throws Exception {
		
		RecordModel record = new RecordModel();
		record.setProduct("unittest");
		record.setVersion("1.0.0");
		record.setComponent("default");
		record.setLocale("EN");
		
		String getComponentUrl =  GATEWAYPREF+L10NAPIV1.API_L10N + "/sourcecomponent/"
				+ record.getProduct() + "/" + record.getVersion() + "/" + record.getComponent() + "/"
				+ record.getLocale() + "/";
		
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		MvcResult mvcRS = mockMvc.perform(MockMvcRequestBuilders.get(getComponentUrl)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
		  String resultStr =   mvcRS.getResponse().getContentAsString();
		   
		   logger.info(resultStr);
		
	}
	

	@Test
	public void test003synchRecoredModel() throws Exception {
		String l10nsynchUrl =GATEWAYPREF+ L10NAPIV1.API_L10N+"/synchrecord";
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		
		MvcResult mvcRS =mockMvc.perform(
				post(l10nsynchUrl).param("product", "unittest")
						.param("version", "1.0.0")
						.param("component", "default")
						.param("locale", "EN")
						.param("status", "2")
						.accept(MediaType.APPLICATION_JSON)).andReturn();
		    String resultStr =   mvcRS.getResponse().getContentAsString();
		    
		    logger.info(resultStr);
	}
	
	@Test
	public void test004getRecoredModelfromS3() throws Exception {
		try {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
		String urlStr =L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV2;
		
		MvcResult mvcRS = mockMvc.perform(MockMvcRequestBuilders.get(urlStr)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
	   String resultStr =   mvcRS.getResponse().getContentAsString();
	   
	   logger.info(resultStr);
		}catch(Exception e) {
		}
	}
	
	
	
}
