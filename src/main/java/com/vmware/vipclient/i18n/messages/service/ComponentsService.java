/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentsBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class ComponentsService {

	Logger logger = LoggerFactory.getLogger(ComponentsService.class);
	private final List<String> components;
	private final String locale;

	/**
	 * @param components
	 * @param locale
	 */
	public ComponentsService(List<String> components, String locale) {
		this.components = components;
		this.locale = locale;
	}

	public Map<String, Map<String, String>> getTranslation() {
		final Map<String, Map<String, String>> retMap = new HashMap<>();

		final ArrayList<String> componentsToQuery = new ArrayList<String>() {};

		for (final String component : components) {
			final MessagesDTO dto = new MessagesDTO();
			dto.setComponent(component);
			dto.setLocale(locale);

			final CacheService cs = new CacheService(dto);
			final Map<String, String> translations = cs.getCacheOfComponent();
			if (translations == null && !cs.isContainComponent()) {
				componentsToQuery.add(component);
			}
			else {
				retMap.put(component, translations);
			}
		}

		final Map<String, Map<String, String>> transMap = new ComponentsBasedOpt(componentsToQuery, locale).getComponentsMessages();
		for (final  Entry<String, Map<String, String>> entry: transMap.entrySet()) {
			final MessagesDTO dto = new MessagesDTO();
			dto.setComponent(entry.getKey());
			dto.setLocale(locale);
			final CacheService cs = new CacheService(dto);
			cs.addCacheOfComponent(entry.getValue());

			retMap.put(entry.getKey(), entry.getValue());
		}

		return retMap;
	}
}
