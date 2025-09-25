/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class S3Test {
	private static Logger logger = LoggerFactory.getLogger(S3Test.class);

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	public void test001() {
		SourceDao sourceDao = webApplicationContext.getBean(SourceDao.class);

		ComponentMessagesDTO single = new ComponentMessagesDTO();

		single.setProductName("unitTest");
		single.setComponent("default");
		single.setVersion("1.0.0");
		single.setLocale("en");
		Map<String, String> map = new HashMap<>();

		map.put("dc.unittest.value", "this is unit test value");

		single.setMessages(map);

		ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 1000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
		pool.prestartAllCoreThreads();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		Runnable r1 = () -> {
			try {
				sourceDao.updateToBundle(single);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		};

		map.put("dc.unittest.new", "this is unit test new value");

		Runnable r2 = () -> {
			try {
				sourceDao.updateToBundle(single);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		};

		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);

		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);
		pool.execute(r1);
		pool.execute(r2);

		pool.shutdown();
		try {
			pool.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}

		Assert.assertTrue(true);
	}

	@Test
	public void test002() throws Exception {
		SourceDao sourceDao = webApplicationContext.getBean(SourceDao.class);
		SingleComponentDao singleComponentDao = webApplicationContext.getBean(SingleComponentDao.class);
		String fileNamePrefix = "messages_";
		try (Stream<Path> input = Files.walk(Paths.get("viprepo-bundle/l10n/bundles")).filter(Files::isRegularFile)) {
			input.forEach(filePath -> {
				int nameCount = filePath.getNameCount();
				String fileName = filePath.getName(nameCount - 1).toString();
				if (!fileName.startsWith(fileNamePrefix)) {
					return;
				}
				String locale = fileName.substring(fileNamePrefix.length(), fileName.lastIndexOf('.'));
				String component = filePath.getName(nameCount - 2).toString();
				String version = filePath.getName(nameCount - 3).toString();
				String product = filePath.getName(nameCount - 4).toString();
	
				ComponentMessagesDTO dto = new ComponentMessagesDTO();
				dto.setProductName(product);
				dto.setVersion(version);
				dto.setComponent(component);
				dto.setLocale(locale);
	
				JSONObject obj;
				try {
					obj = new JSONObject(new JSONTokener(new FileReader(filePath.toString())));
					System.out.println("filePath=" + filePath.toString());
					System.out.println("obj=" + obj.toString());
					System.out.println("messages=" + obj.get("messages").toString());
					dto.setMessages(obj.get("messages"));
				} catch (IOException | JSONException e) {
					logger.error(e.getMessage(), e);
				}
	
				if (dto.getLocale().equals("latest")) {
					try {
						sourceDao.updateToBundle(dto);
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
					sourceDao.getFromBundle(dto);
				} else {
					com.vmware.l10n.translation.dto.ComponentMessagesDTO translationDto = new com.vmware.l10n.translation.dto.ComponentMessagesDTO();
					BeanUtils.copyProperties(dto, translationDto);
					try {
						singleComponentDao.writeTranslationToFile(translationDto);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(), e);
					}
	
					try {
						singleComponentDao.getTranslationFromFile(translationDto);
					} catch (L10nAPIException e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
		}

		Assert.assertTrue(true);
	}
	
	@Test
	public void test003() throws Exception {
		SourceDao sourceDao = webApplicationContext.getBean(SourceDao.class);

		ComponentMessagesDTO single = new ComponentMessagesDTO();

		single.setProductName("unitTest");
		single.setComponent("default");
		single.setVersion("1.0.0");
		single.setLocale("en");
		Map<String, String> map = new HashMap<>();

		map.put("dc.unittest.value", "this is unit test value");

		single.setMessages(map);

		

		for (int i=0; i< 30; i++) {
			sourceDao.updateToBundle(single);
		}


		Assert.assertTrue(true);
	}	
}
