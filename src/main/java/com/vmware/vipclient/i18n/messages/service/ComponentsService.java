/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentsBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ComponentsService {

	Logger logger = LoggerFactory.getLogger(ComponentsService.class);
	private final List<String> components;
	private final List<String> locales;

	/**
	 * @param components
	 * @param locales
	 */
	public ComponentsService(List<String> components, List<String> locales) {
		this.components = components;
		this.locales = locales;
	}

	public Map<String, Map<String, Map<String, String>>> getTranslation() {
		final Map<String, Map<String, Map<String, String>>> retMap = new HashMap<>();

		final Set<String> componentsToQuery = new HashSet<>();
		final Set<String> localesToQuery = new HashSet<>();

		for (String locale : locales) {
			final MessagesDTO dto = new MessagesDTO();
			dto.setLocale(locale);
			Map<String, Map<String, String>> localeMap = new HashMap<>();
			for (final String component : components) {
				dto.setComponent(component);

				final CacheService cs = new CacheService(dto);
				final Map<String, String> translations = cs.getCacheOfComponent();
				if (translations == null && !cs.isContainComponent()) {
					componentsToQuery.add(component);
					localesToQuery.add(locale);
				}
				else {
					localeMap.put(component, translations);
				}
			}
			retMap.put(locale, localeMap);
		}

		final JSONArray bundles = new ComponentsBasedOpt(componentsToQuery, localesToQuery)
				.getComponentsMessages();
		for (final Object entry : bundles) {
			JSONObject obj = (JSONObject)entry;
			String locale = (String) obj.get(ConstantsKeys.LOCALE);
			String comp = (String) obj.get(ConstantsKeys.COMPONENT);
			JSONObject messages = (JSONObject) obj.get(ConstantsKeys.MESSAGES);

			// update cache
			final MessagesDTO dto = new MessagesDTO();
			dto.setComponent(comp);
			dto.setLocale(locale);
			final CacheService cs = new CacheService(dto);
			cs.addCacheOfComponent(messages);

			// construct return data
			Map<String, Map<String, String>> localeMap = retMap.get(locale);
			localeMap.put(comp, messages);
			retMap.put(locale, localeMap);
		}

		return retMap;
	}
}
