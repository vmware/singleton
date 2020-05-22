/*
 * Copyright 2019 VMware, Inc.
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
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SourceServiceTest {
	private static Logger logger = LoggerFactory.getLogger(SourceDaoSource.class);
	@Autowired
	private WebApplicationContext webApplicationContext;	

	@Test
	public void test002SourceServiceImpl() {
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
		
	}
	
	
	@Test
	public void test003SourceServiceImpl() {
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("devCenter");
		sourceDTO.setVersion("1.0.0");
		sourceDTO.setComponent("default");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		
		String catcheKey  = PathUtil.generateCacheKey(sourceDTO);
		ComponentSourceDTO comp = new ComponentSourceDTO();
		BeanUtils.copyProperties(sourceDTO, comp);
		source.setParpareMap(comp, catcheKey);
		source.setParpareMap(comp, catcheKey);
		
		
		
		
		
		
	}
	
	

}
