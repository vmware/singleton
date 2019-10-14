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

public class LocalSourceOpt {

	private static Map<String, Map<Object, Object>> sources = new HashMap<>();

	// load sources from local files.
	@SuppressWarnings("unchecked")
	public static void loadSources(List<Map<String, Object>> sourcebundles) throws IOException {
		if(!LocalSourceOpt.sources.isEmpty()) {return;}
		if (null == sourcebundles || sourcebundles.isEmpty()) {
			throw new VIPJavaClientException("No sources are provided in config file!");
		}
		
		for( Map<String, Object> e : sourcebundles) {
			String comp = (String) e.get(ConstantsKeys.CONFIG_COMPONENT);
			List<String> files = (List<String>) e.get(ConstantsKeys.CONFIG_COMPONENT_FILE);
			
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
//    				if (f.endsWith(".properties")) {
    				messages = FileUtil.readPropertiesFile(f);
//    			} else {
//    				throw new VIPJavaClientException("Unsupported file format: "+f);
//    			}
    			
    			if(null == messages || messages.size() == 0) {
    				continue;
    			}

    			Map<Object, Object> existingMessages = LocalSourceOpt.sources.get(comp);
    			if(null == existingMessages) {
    				LocalSourceOpt.sources.put(comp, messages); 
    			}
    			else {
    				existingMessages.putAll(messages);
    				LocalSourceOpt.sources.put(comp, existingMessages);
    			}
			}
		}
	}

	/**
	 * @param dto
	 * @return
	 */
	public static String get(MessagesDTO dto) {
		Map<?, ?> messages = sources.get(dto.getComponent());
		String result = (null == messages ? "" : (String) messages.get(dto.getKey()));
		return null == result ? "" : result;
	}
}
