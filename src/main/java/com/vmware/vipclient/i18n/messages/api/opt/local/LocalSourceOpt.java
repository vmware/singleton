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

	private static Map<String, Map<?, ?>> defaultResources = new HashMap<>();

	// load sources from local files.
	@SuppressWarnings("unchecked")
	public static void loadResources(List<Map<String, Object>> resources) throws IOException {
		if (null == resources || resources.isEmpty()) {
			throw new VIPJavaClientException("No resources are provided in config file!");
		}
		
		for( Map<String, Object> e : resources) {
			String comp = (String) e.get(ConstantsKeys.COMPONENT_CONFIG);
			String res = (String) e.get(ConstantsKeys.RESOURCE_CONFIG);
			
			Map<Object, Object> messages;
//			if (res.endsWith(".json")) {
//				try {
//					messages = FileUtil.readJSONFile(res);
//				} catch (ParseException e1) {
//					throw new VIPJavaClientException("Failed to parse JSON file: "+res, e1);
//				}
//			}
//			else 
				if (res.endsWith(".properties")) {
				messages = FileUtil.readPropertiesFile(res);
			} else {
				throw new VIPJavaClientException("Unsupported resource format: "+res);
			}
			
			MessagesDTO dto = new MessagesDTO();
			dto.setComponent(comp);
			dto.setLocale(LocaleUtility.defaultLocale.toLanguageTag());
			defaultResources.put(dto.getCompositStrAsCacheKey(), messages);
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
