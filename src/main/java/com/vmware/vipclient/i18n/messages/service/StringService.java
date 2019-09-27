/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FileUtil;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class StringService {
	private MessagesDTO dto = null;

	private static Map<String, Map<?, ?>> defaultResources = new HashMap<>();
	Logger logger = LoggerFactory.getLogger(StringService.class);

	public StringService(MessagesDTO dto) {
		this.dto = dto;
	}

	@SuppressWarnings("unchecked")
	public String getString() {
		String key = dto.getKey();
		CacheService cacheservice = new CacheService(dto);
		Map<String, String> map = cacheservice.getCacheOfComponent();
		if (map == null) {
			if (!cacheservice.isContainComponent()) {
				Object o = new ComponentService(dto).getMessages();
				map = (Map<String, String>) o;
				cacheservice.addCacheOfComponent(map);
			}
		}
		return (map == null || map.get(key) == null ? "" : map.get(key));
	}

	public String postString() {
		String r = "";
		if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
			ComponentBasedOpt dao = new ComponentBasedOpt(dto);
			r = dao.postString();
		}
		if(r != "") {
			dto.setLocale(ConstantsKeys.LATEST);
			CacheService c = new CacheService(dto);
			Map<String, String> dataMap = new HashMap<>();
			dataMap.put(dto.getKey(), dto.getSource());
			c.updateCacheOfComponent(dataMap);
		}
		return r;
	}

	public boolean postStrings(List<JSONObject> sources) {
		boolean r = false;
		if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
			ComponentBasedOpt dao = new ComponentBasedOpt(dto);
			r = "200".equalsIgnoreCase(dao.postSourceSet(sources.toString()));
		}
		if(r) {
			dto.setLocale(ConstantsKeys.LATEST);
			CacheService c = new CacheService(dto);
			Map<String, String> dataMap = new HashMap<>();
			for(JSONObject jo: sources) {
				dataMap.put((String)jo.get(ConstantsKeys.KEY), jo.get(ConstantsKeys.SOURCE) == null ? "" : (String)jo.get(ConstantsKeys.SOURCE));
			}
			c.updateCacheOfComponent(dataMap);
		}
		return r;
	}
	
	public boolean isStringAvailable() {
		boolean r = false;
		String status = "";
		if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
			CacheService c = new CacheService(dto);
			Map<String, String> statusMap = c.getCacheOfStatus();
			if (statusMap != null && !statusMap.isEmpty()) {
				status = statusMap.get(dto.getKey());
			} else if(!c.isContainStatus()){
				StringBasedOpt dao = new StringBasedOpt(dto);
				String json = dao.getTranslationStatus();
				Map m = null;
				if (!JSONUtils.isEmpty(json)) {
					try {
						m = (Map) JSONValue.parseWithException(json);
						if (m != null) {
							status = m.get(dto.getKey()) == null ? ""
									: (String) m.get(dto.getKey());
						}
					} catch (ParseException e) {
						logger.error(e.getMessage());
					}
				}
				c.addCacheOfStatus(m);

			}
			r = "1".equalsIgnoreCase(status);
		}
		return r;
	}

	// Get source
	public String getSource() {
		Map<?, ?> messages = StringService.defaultResources.get(dto.getCompositStrAsCacheKey());
		String result = (null == messages ? "" : (String) messages.get(this.dto.getKey()));
		return null == result ? "" : result; 
	}
	
	// load sources from local files.
	@SuppressWarnings("unchecked")
	public static void loadResources(List<Map<String, Object>> resources) throws IOException {
		if (null == resources || resources.isEmpty()) {
			throw new VIPJavaClientException("No resources are provided in config file!");
		}
		
		for( Map<String, Object> e : resources) {
			String comp = (String) e.get(VIPCfg.COMPONENT);
			String res = (String) e.get(VIPCfg.RESOURCE);
			
			Map<Object, Object> messages;
			try {
				messages = res.endsWith(".json") ? FileUtil.readJSONFile(res) : FileUtil.readPropertiesFile(res);
			} catch (ParseException e1) {
				throw new VIPJavaClientException("Failed to parse JSON file.", e1);
			}
			
			MessagesDTO dto = new MessagesDTO();
			dto.setComponent(comp);
			dto.setLocale(LocaleUtility.defaultLocale.toLanguageTag());
			StringService.defaultResources.put(dto.getCompositStrAsCacheKey(), messages);
		}
	}
	
}
