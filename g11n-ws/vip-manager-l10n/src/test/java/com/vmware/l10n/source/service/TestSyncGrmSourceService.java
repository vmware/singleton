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
import com.vmware.l10n.source.service.impl.SyncGrmSourceServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

import io.jsonwebtoken.lang.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestSyncGrmSourceService {
	private static Logger logger = LoggerFactory.getLogger(TestSyncGrmSourceService.class);

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	SyncGrmSourceServiceImpl syncSource;
	
	@Before
	public void testBefore() {
			StringSourceDTO csd = new StringSourceDTO();

			csd.setProductName("test");
			csd.setVersion("2.0.0");
			csd.setComponent("default");
			csd.setLocale("latest");
			csd.setKey("dc.myhome.open3");
			csd.setSource("this open3's value");
			csd.setComment("dc new string");

			StringSourceDTO sourceDTO = new StringSourceDTO();
			sourceDTO.setProductName("test");
			sourceDTO.setVersion("1.0.0");
			sourceDTO.setComponent("default");
			sourceDTO.setLocale(ConstantsKeys.LATEST);
			sourceDTO.setKey("dc.myhome.open3");
			sourceDTO.setSource("this open3's value");
			sourceDTO.setComment("dc new string");

			SourceServiceImpl sourceService = webApplicationContext.getBean(SourceServiceImpl.class);
			sourceService.cacheSource(sourceDTO);
			sourceService.cacheSource(csd);
			sourceService.writeSourceToCachedFile();
			
			String basePath = syncSource.getBasePath();
			List<File> files = DiskQueueUtils.listSourceQueueFile(basePath);
	        int fileNumb = files.size();
	        
			if (files != null) {
				for (File source : files) {
					try {
						DiskQueueUtils.moveFile2GRMPath(basePath, source);
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
						Assert.isNull(e);
					}
				}

			}

			List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
			Assert.isTrue(queueFiles.size() == fileNumb);
	}

	@Test
	public void test001sendSourceToGRM() {
		String basePath = syncSource.getBasePath();

		try {
			syncSource.sendSourceToGRM();
			List<File> i18nQueueFiles = DiskQueueUtils
					.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_I18N_PATH));
			Assert.isTrue(i18nQueueFiles.size() < 1);
			
			List<File> delFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
			for (File delFile : delFiles) {
				DiskQueueUtils.delQueueFile(delFile);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}

	}

}
