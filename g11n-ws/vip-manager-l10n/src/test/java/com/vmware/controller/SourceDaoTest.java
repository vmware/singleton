/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SourceDaoTest {
	 private static Logger logger = LoggerFactory.getLogger(SourceDaoTest.class);
	
	@Autowired
	private WebApplicationContext webApplicationContext;


	
@Test
public void  test001() throws Exception {
	SourceDao sourceDao = webApplicationContext.getBean(SourceDao.class);	
	ComponentMessagesDTO single= new ComponentMessagesDTO();
	
	single.setProductName("unitTest");
	single.setComponent("default");
    single.setVersion("1.0.0");
	single.setLocale("en");
	Map<String, String> map = new HashMap<String, String>();
	
	map.put("dc.unittest.value", "this is unit test value");
	
	single.setMessages(map);
	sourceDao.updateToBundle(single);
	
	
	map.put("dc.unittest.new", "this is unit test new value");
	
	sourceDao.updateToBundle(single);
	
	sourceDao.getFromBundle(single);
	
}
	
	
	
	
}
