/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

import io.jsonwebtoken.lang.Assert;

public class TestDiskQueueUtils {
	private static Logger logger = LoggerFactory.getLogger(TestDiskQueueUtils.class);
	private String basePath = "viprepo-bundle" + File.separator;

	@Test
	public void test001moveFile2ExceptPath() {
		ConcurrentMap<String, ComponentSourceDTO> prepareMap = new ConcurrentHashMap<String, ComponentSourceDTO>();
		ComponentSourceDTO csd = new ComponentSourceDTO();
		csd.setProductName("test");
		csd.setVersion("1.0.0");
		csd.setComponent("default");
		csd.setLocale("latest");
		csd.setMessages("test1.l10n", "this is a test1");
		csd.setMessages("test2.l10n", "this is a test2");
		prepareMap.put(csd.getProductName() + "." + csd.getComponent() + "." + csd.getVersion(), csd);
		
		try {
			File file = DiskQueueUtils.createQueueFile(prepareMap, basePath);
			System.out.println("prepareMap=" + prepareMap);
			DiskQueueUtils.moveFile2ExceptPath(basePath, file, "locale");
			List<File> exepQueueFiles = DiskQueueUtils.listExceptQueueFile(basePath);
			System.out.println("basePath=" + basePath);
			System.out.println("exepQueueFiles=" + exepQueueFiles);
			System.out.println("exepQueueFiles.size()=" + exepQueueFiles.size());
	        Assert.isTrue(exepQueueFiles.size() == 1);
	        
			for(File delFile : exepQueueFiles) {
				DiskQueueUtils.delQueueFile(delFile);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
		
		
		

	}

	@Test
	public void test002moveFile2I18nPath() {
		ConcurrentMap<String, ComponentSourceDTO> prepareMap = new ConcurrentHashMap<String, ComponentSourceDTO>();

		ComponentSourceDTO csd = new ComponentSourceDTO();
		csd.setProductName("test");
		csd.setVersion("2.0.0");
		csd.setComponent("default");
		csd.setLocale("latest");
		csd.setMessages("test1.l10n", "this is a test1");
		csd.setMessages("test2.l10n", "this is a test2");
		prepareMap.put(csd.getProductName() + "." + csd.getComponent() + "." + csd.getVersion(), csd);
		try {
			File file = DiskQueueUtils.createQueueFile(prepareMap, basePath);
			DiskQueueUtils.moveFile2I18nPath(basePath, file);
			List<File> i18nQueueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_I18N_PATH));
	        Assert.isTrue(i18nQueueFiles.size() == 1);
	        for(File delFile : i18nQueueFiles) {
				DiskQueueUtils.delQueueFile(delFile);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
		

	}

	@Test
	public void test003delQueueFile() {

		ConcurrentMap<String, ComponentSourceDTO> prepareMap = new ConcurrentHashMap<String, ComponentSourceDTO>();

		ComponentSourceDTO csd = new ComponentSourceDTO();
		csd.setProductName("test");
		csd.setVersion("2.0.0");
		csd.setComponent("default");
		csd.setLocale("latest");
		csd.setMessages("test1.l10n", "this is a test1");
		csd.setMessages("test2.l10n", "this is a test2");
		prepareMap.put(csd.getProductName() + "." + csd.getComponent() + "." + csd.getVersion(), csd);
		try {
			File file = DiskQueueUtils.createQueueFile(prepareMap, basePath);
			DiskQueueUtils.delQueueFile(file);
			Assert.isTrue(!file.exists());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			Assert.isNull(e);
		}
	}
}
