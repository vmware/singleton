/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

import io.jsonwebtoken.lang.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SourceServiceTest {
	private static Logger logger = LoggerFactory.getLogger(SourceServiceTest.class);
	@Autowired
	private WebApplicationContext webApplicationContext;	

	@Test
	public void test001cacheSource() {
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
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
		
		
	}
	
	
	@Test
	public void test002writeSourceToCachedFile() throws IOException {
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("test");
		sourceDTO.setVersion("1.0.0");
		sourceDTO.setComponent("default");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		
		StringSourceDTO sourceDTO1 = new StringSourceDTO();
		sourceDTO1.setProductName("test");
		sourceDTO1.setVersion("1.0.0");
		sourceDTO1.setComponent("default");
		sourceDTO1.setLocale(ConstantsKeys.LATEST);
		sourceDTO1.setKey("dc.myhome.open1");
		sourceDTO1.setSource("this open3's value");
		sourceDTO1.setComment("dc new string");

		source.cacheSource(sourceDTO);
		source.cacheSource(sourceDTO1);
		source.writeSourceToCachedFile();
		List<File> files = DiskQueueUtils.listSourceQueueFile("viprepo-bundle" + File.separator);
		Assert.isTrue(files.size()>0);
		
		for(File file: files) {
			DiskQueueUtils.delQueueFile(file);
		}
		
	}
	
	
	

}
