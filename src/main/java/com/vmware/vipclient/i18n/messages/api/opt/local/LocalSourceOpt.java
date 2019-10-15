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
	public static void loadResources(List<Map<String, Object>> components) throws IOException {
		if(!LocalSourceOpt.sources.isEmpty()) {return;}
		if (null == components || components.isEmpty()) {
			throw new VIPJavaClientException("No sources are provided in config file!");
		}

		for( Map<String, Object> entry : components) {
			String comp = (String) entry.get(ConstantsKeys.CONFIG_COMPONENT);
			List<Map<String, Object>> files = (List<Map<String, Object>>) entry.get(ConstantsKeys.CONFIG_RESOURCE);

			for (Map<String, Object> f : files) {
				String filepath = (String) f.get(ConstantsKeys.CONFIG_RESOURCE_FILE);
				Map<Object, Object> messages = FileUtil.readPropertiesFile(filepath);
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
