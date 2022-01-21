/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.l10n.source.service.impl.SyncLocalBundleServiceImpl;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

import io.jsonwebtoken.lang.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TestSyncLocalBundleService {
	private static Logger logger = LoggerFactory.getLogger(TestSyncLocalBundleService.class);
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	SyncLocalBundleServiceImpl syncSource;

	@Test
	public void test001MergeSourceToLocalBundle() {
		SourceServiceImpl source = webApplicationContext.getBean(SourceServiceImpl.class);
		StringSourceDTO sourceDTO = new StringSourceDTO();
		sourceDTO.setProductName("test");
		sourceDTO.setVersion("1.0.0");
		sourceDTO.setComponent("default");
		sourceDTO.setLocale(ConstantsKeys.LATEST);
		sourceDTO.setKey("dc.myhome.open3");
		sourceDTO.setSource("this open3's value");
		sourceDTO.setComment("dc new string");
		source.cacheSource(sourceDTO);
		source.writeSourceToCachedFile();
		try {
			syncSource.mergeSourceToLocalBundle();
			SourceDao sourcedao = webApplicationContext.getBean(SourceDao.class);
			SingleComponentDTO sdto = new SingleComponentDTO();
			sdto.setProductName("test");
			sdto.setVersion("1.0.0");
			sdto.setComponent("default");
			sdto.setLocale(ConstantsKeys.LATEST);
			Assert.notNull(sourcedao.getFromBundle(sdto));

			List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File("viprepo-bundle" + File.separator + DiskQueueUtils.L10N_TMP_GRM_PATH));
			for (File delFile : queueFiles) {
				DiskQueueUtils.delQueueFile(delFile);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
	}

}
