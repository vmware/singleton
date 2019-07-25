/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class CacheManagerTest {

	private CacheService cacheService;

	private MessagesDTO cacheDTO;

	@Before
	public void init() {
		VIPCfg gc = VIPCfg.getInstance();
		gc.initialize("vipconfig");
		gc.initializeVIPService();
		Cache c = gc.createTranslationCache(MessageCache.class);
		c.setExpiredTime(3600);
		cacheDTO = new MessagesDTO();
		cacheDTO = new MessagesDTO();
		cacheDTO.setProductID("dragon");
		cacheDTO.setVersion("1.0.0");
		cacheDTO.setComponent("JAVA");
		cacheDTO.setLocale("zh_CN");
		cacheService = new CacheService(cacheDTO);
	}

	@Test
	public void testLookForComponentTranslationInCache() {
		Map<String, String> msgObj = new HashMap<String, String>();
		msgObj.put("book", "@zh_CN@book");
		cacheService.addCacheOfComponent(msgObj);
		Map<String, String> messageMap = cacheService
				.getCacheOfComponent();
		Assert.assertTrue(messageMap.size() == 1);
		VIPCfg.getInstance().getCacheManager().clearCache();
	}

	@Test
	public void testLookForTranslationInCache() {
		Map<String, String> msgObj = new HashMap<String, String>();
		msgObj.put("book", "@zh_CN@book");
		cacheService.addCacheOfComponent(msgObj);
		Map<String, String> result = cacheService
				.getCacheOfComponent();
		Assert.assertTrue(result.size() > 0);
		VIPCfg.getInstance().getCacheManager().clearCache();
	}

}
