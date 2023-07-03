/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.l10n.source.service.impl.SyncLocalBundleServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.utils.JSONUtils;
import org.junit.Assert;
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

import java.io.File;
import java.util.List;
import java.util.Map;

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

	@Autowired
	SyncLocalBundleServiceImpl syncSource;

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
	public void test004getRecoredModelfromV2() throws Exception {
		try {

			SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
			StringSourceDTO sourceDTO = new StringSourceDTO();
			sourceDTO.setProductName("unittest");
			sourceDTO.setVersion("1.0.0");
			sourceDTO.setComponent("record");
			sourceDTO.setLocale(ConstantsKeys.LATEST);
			sourceDTO.setKey("dc.myhome.open3");
			sourceDTO.setSource("this open3's value");
			sourceDTO.setComment("dc new string");
			source.cacheSource(sourceDTO);
			source.writeSourceToCachedFile();
			syncSource.mergeSourceToLocalBundle();
			SourceDao sourcedao = webApplicationContext.getBean(SourceDao.class);
			SingleComponentDTO sdto = new SingleComponentDTO();
			sdto.setProductName("unittest");
			sdto.setVersion("1.0.0");
			sdto.setComponent("record");
			sdto.setLocale(ConstantsKeys.LATEST);
			io.jsonwebtoken.lang.Assert.notNull(sourcedao.getFromBundle(sdto));

			MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(
				webApplicationContext).build();
			String urlStr =L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV2+"?productName=unittest&&version=1.0.0";
			MvcResult mvcRS = mockMvc.perform(MockMvcRequestBuilders.get(urlStr)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
			String resultStr =   mvcRS.getResponse().getContentAsString();
			logger.info(resultStr);
			Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(resultStr).get("response");
			long code = (long) dataMap.get("code");
			Assert.assertTrue(code==200L);
			List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File("viprepo-bundle" + File.separator + DiskQueueUtils.L10N_TMP_GRM_PATH));
			for (File delFile : queueFiles) {
				DiskQueueUtils.delQueueFile(delFile);
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
	
}
