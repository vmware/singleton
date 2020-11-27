/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;

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
		Map<String, String> map = new HashMap<String, String>();

		map.put("dc.unittest.value", "this is unit test value");

		single.setMessages(map);

		ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		pool.prestartAllCoreThreads();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				sourceDao.updateToBundle(single);
			}
		};

		map.put("dc.unittest.new", "this is unit test new value");

		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				sourceDao.updateToBundle(single);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sourceDao.getFromBundle(single);

	}

	@Test
	public void test002() throws IOException {
		SourceDao sourceDao = webApplicationContext.getBean(SourceDao.class);
		SingleComponentDao singleComponentDao = webApplicationContext.getBean(SingleComponentDao.class);
		String fileNamePrefix = "messages_";
		Files.walk(Paths.get("viprepo-bundle/l10n/bundles")).filter(Files::isRegularFile)
        .forEach((p)->{
        	int nameCount = p.getNameCount();
        	String fileName = p.getName(nameCount-1).toString();
        	if (!fileName.startsWith(fileNamePrefix)) {
        		return;
        	}
        	String locale = fileName.substring(fileNamePrefix.length(), fileName.lastIndexOf('.'));
        	String component = p.getName(nameCount-2).toString();
        	String version = p.getName(nameCount-3).toString();
        	String product = p.getName(nameCount-4).toString();
        	
        	ComponentMessagesDTO dto = new ComponentMessagesDTO();
        	dto.setProductName(product);
			dto.setVersion(version);
			dto.setComponent(component);
			dto.setLocale(locale);

			JSONObject obj;
			try {
				obj = (JSONObject) new JSONParser().parse(new FileReader(p.toString()));
				dto.setMessages(obj.get("messages"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (dto.getLocale().equals("latest")) {
				sourceDao.updateToBundle(dto);
				String result = sourceDao.getFromBundle(dto);
			} else {
				com.vmware.l10n.translation.dto.ComponentMessagesDTO translationDto = new com.vmware.l10n.translation.dto.ComponentMessagesDTO();
				BeanUtils.copyProperties(dto,  translationDto);
				singleComponentDao.writeLocalTranslationToFile(translationDto);
				
				try {
					com.vmware.l10n.translation.dto.ComponentMessagesDTO resultDTO = singleComponentDao.getLocalTranslationFromFile(translationDto);
				} catch (L10nAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        });
	}
}
