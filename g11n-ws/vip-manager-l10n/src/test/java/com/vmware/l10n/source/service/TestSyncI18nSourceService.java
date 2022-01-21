/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
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
import com.vmware.l10n.source.service.impl.SyncI18nSourceServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

import io.jsonwebtoken.lang.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestSyncI18nSourceService {
	private static Logger logger = LoggerFactory.getLogger(TestSyncI18nSourceService.class);
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void testBefore() {
		StringSourceDTO csd = new StringSourceDTO();

		csd.setProductName("test");
		csd.setVersion("3.0.0");
		csd.setComponent("default");
		csd.setLocale("latest");
		csd.setKey("dc.myhome.open3");
		csd.setSource("this open3's value");
		csd.setComment("dc new string");
		
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("test");
		sourceDTO.setVersion("3.0.0");
		sourceDTO.setComponent("test");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		
		try {
			source.cacheSource(sourceDTO);
			source.cacheSource(csd);
			source.writeSourceToCachedFile();
			List<File> files = DiskQueueUtils.listExceptQueueFile("viprepo-bundle"+File.separator);
			Assert.notEmpty(files);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
	}

	
	@Test
	public void test001sendSourceToI18n() {
		SyncI18nSourceServiceImpl syncSource = webApplicationContext.getBean(SyncI18nSourceServiceImpl.class);
		String basePath = syncSource.getBasePath();
		List<File> files = DiskQueueUtils.listSourceQueueFile(basePath);
		
		if(files != null) {
			for(File source: files) {
				try {
					DiskQueueUtils.moveFile2I18nPath(basePath, source);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					Assert.isNull(e);
				}
			}
		
		}
		List<File> i18nQueueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_I18N_PATH));
        Assert.notEmpty(i18nQueueFiles);
		
		try {
			syncSource.sendSourceToI18n();
			List<File> backupQueueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_BACKUP_PATH));
	        Assert.notEmpty(backupQueueFiles);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}

		
		
	}

	
	

}
