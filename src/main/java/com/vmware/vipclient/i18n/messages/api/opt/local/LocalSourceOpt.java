/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FileUtil;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class LocalSourceOpt {

	private static Map<String, Map<Object, Object>> defaultResources = new HashMap<>();

	// load sources from local files.
	@SuppressWarnings("unchecked")
	public static void loadResources(List<Map<String, Object>> resources) throws IOException {
		if (null == resources || resources.isEmpty()) {
			throw new VIPJavaClientException("No resources are provided in config file!");
		}
		
		for( Map<String, Object> e : resources) {
			String comp = (String) e.get(ConstantsKeys.COMPONENT_CONFIG);
			List<String> files = (List<String>) e.get(ConstantsKeys.RESOURCE_CONFIG);
			
			for (String f : files) {
    			Map<Object, Object> messages;
    //			if (f.endsWith(".json")) {
    //				try {
    //					messages = FileUtil.readJSONFile(f);
    //				} catch (ParseException e1) {
    //					throw new VIPJavaClientException("Failed to parse JSON file: "+f, e1);
    //				}
    //			}
    //			else 
    				if (f.endsWith(".properties")) {
    				messages = FileUtil.readPropertiesFile(f);
    			} else {
    				throw new VIPJavaClientException("Unsupported resource format: "+f);
    			}
    			
    			if(null == messages || messages.size() == 0) {
    				continue;
    			}

    			MessagesDTO dto = new MessagesDTO();
    			dto.setComponent(comp);
    			dto.setLocale(LocaleUtility.defaultLocale.toLanguageTag());
    			Map<Object, Object> existingMessages = defaultResources.get(dto.getCompositStrAsCacheKey());
    			if(null == existingMessages) {
    				defaultResources.put(comp, messages); 
    			}
    			else {
    				existingMessages.putAll(messages);
    				defaultResources.put(comp, existingMessages);
    			}
			}
		}
	}

	/**
	 * @param dto
	 * @return
	 */
	public static String get(MessagesDTO dto) {
		Map<?, ?> messages = defaultResources.get(dto.getCompositStrAsCacheKey());
		String result = (null == messages ? "" : (String) messages.get(dto.getKey()));
		return null == result ? "" : result;
	}
}
