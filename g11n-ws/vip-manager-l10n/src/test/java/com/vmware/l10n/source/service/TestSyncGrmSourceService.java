/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.service.impl.SyncGrmSourceServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

import io.jsonwebtoken.lang.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestSyncGrmSourceService {
	private static Logger logger = LoggerFactory.getLogger(TestSyncGrmSourceService.class);
	
	@Autowired
	SyncGrmSourceServiceImpl syncSource;
	
	@Before
	public void testBefore() throws IOException {


			ConcurrentMap<String, ComponentSourceDTO> prepareMap = new ConcurrentHashMap<String, ComponentSourceDTO>();
			ComponentSourceDTO csd = new ComponentSourceDTO();
			csd.setProductName("test");
			csd.setVersion("1.0.0");
			csd.setComponent("default");
			csd.setLocale("latest");
			csd.setMessages("test1.l10n", "this is a test1");
			csd.setMessages("test2.l10n", "this is a test2");
			ComponentSourceDTO csd2 = new ComponentSourceDTO();
			csd2.setProductName("test");
			csd2.setVersion("2.0.0");
			csd2.setComponent("default");
			csd2.setLocale("latest");
			csd2.setMessages("test1.l10n", "this is a test1");
			csd2.setMessages("test2.l10n", "this is a test2");
			
			prepareMap.put(csd.getProductName() + "." + csd.getComponent() + "." + csd.getVersion(), csd);
			prepareMap.put(csd2.getProductName() + "." + csd2.getComponent() + "." + csd2.getVersion(), csd);
			
			String basePath = syncSource.getBasePath();
	        File sourceFile =  DiskQueueUtils.createQueueFile(prepareMap, basePath);
	        
	        List<File> delFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
	        if(delFiles != null) {
	        	for (File delFile : delFiles) {
					DiskQueueUtils.delQueueFile(delFile);
				}
	        }
			
			DiskQueueUtils.moveFile2GRMPath(basePath, sourceFile);
			List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
			Assert.isTrue(queueFiles.size() == 1);
	}

	@Test
	public void test001sendSourceToGRM() {
		String basePath = syncSource.getBasePath();

		try {
			syncSource.sendSourceToGRM();
			List<File> i18nQueueFiles = DiskQueueUtils
					.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_I18N_PATH));
			Assert.isTrue(i18nQueueFiles == null || i18nQueueFiles.size() < 1);
			
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
