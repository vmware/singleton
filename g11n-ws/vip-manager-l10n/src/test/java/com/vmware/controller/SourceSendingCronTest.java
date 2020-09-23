/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.source.crons.SourceSendingCron;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SourceSendingCronTest {
	 private static Logger logger = LoggerFactory.getLogger(SourceDaoSource.class);
		
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Test
	public void test001init() {
		SourceSendingCron sourceSend =  webApplicationContext.getBean(SourceSendingCron.class);
		try {
			StringSourceDTO sourceDTO = new StringSourceDTO();
			sourceDTO.setProductName("devCenter");
			sourceDTO.setVersion("1.0.0");
			sourceDTO.setComponent("default");
			sourceDTO.setLocale(ConstantsKeys.LATEST);
			sourceDTO.setKey("dc.myhome.open3");
			sourceDTO.setSource("this open3's value");
			sourceDTO.setComment("dc new string");
			ComponentSourceDTO comp = new ComponentSourceDTO();
			BeanUtils.copyProperties(sourceDTO, comp);
			comp.setMessages(sourceDTO.getKey(), sourceDTO.getSource());
			String catcheKey = PathUtil.generateCacheKey(sourceDTO);
			TranslationCache3.addCachedObject(CacheName.SOURCEBACKUP, catcheKey, comp);
			sourceSend.setRemoteGRMURL("local");
			sourceSend.setSyncEnabled(true);
			sourceSend.init();
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void test002syncSourceToRemoteAndLocal() {
		
		SourceSendingCron sourceSend =  webApplicationContext.getBean(SourceSendingCron.class);
		sourceSend.syncSourceToRemoteAndLocalInstrument();
		sourceSend.syncSourceToRemoteAndLocalInstrument();
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("devCenter");
		sourceDTO.setVersion("1.0.0");
		sourceDTO.setComponent("default");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		try {
			source.cacheSource(sourceDTO);
		} catch (L10nAPIException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		sourceSend.setSyncEnabled(true);
		sourceSend.syncSourceToRemoteAndLocal();
		logger.info("test002syncSourceToRemoteAndLocal");
	}
	
	@Test
	public void test003syncBkSourceToRemote() {
		SourceSendingCron sourceSend =  webApplicationContext.getBean(SourceSendingCron.class);
		sourceSend.setSyncEnabled(true);
		sourceSend.syncBkSourceToRemote();
		logger.info("test003syncBkSourceToRemote");
	}
	
	@Test
	public void test004syncSourceToRemoteAndLocal() {
		SourceSendingCron sourceSend =  webApplicationContext.getBean(SourceSendingCron.class);
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("devCenter");
		sourceDTO.setVersion("1.0.0");
		sourceDTO.setComponent("default");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		try {
			source.cacheSource(sourceDTO);
		} catch (L10nAPIException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		source.syncSourceToRemoteAndLocal();
		sourceSend.setSyncEnabled(true);
		sourceSend.setRemoteGRMURL("local");
		sourceSend.setRemoteVIPURL("local");
		sourceSend.syncSourceToRemoteAndLocal();
		
		sourceDTO.setSource("this open3's value update");
		sourceDTO.setComment("dc new string update");
		try {
			source.cacheSource(sourceDTO);
		} catch (L10nAPIException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		
		source.syncSourceToRemoteAndLocal();
		sourceSend.setSyncEnabled(true);
		sourceSend.setRemoteGRMURL("local");
		sourceSend.setRemoteVIPURL("local");
		sourceSend.syncSourceToRemoteAndLocal();
		logger.info("test004syncSourceToRemoteAndLocal");
		
	}
	
	@Test
	public void test005syncBkSourceToRemote() {
		SourceSendingCron sourceSend =  webApplicationContext.getBean(SourceSendingCron.class);
		sourceSend.setSyncEnabled(true);
		sourceSend.setRemoteGRMURL("local");
		sourceSend.syncBkSourceToRemote();
		logger.info("test005syncBkSourceToRemote");
	}
	


	
}
