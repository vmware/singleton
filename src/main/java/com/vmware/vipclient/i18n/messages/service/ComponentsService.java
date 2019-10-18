/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		final MessagesDTO dto = new MessagesDTO();
		for (String locale : locales) {
			dto.setLocale(locale);
			Map<String, Map<String, String>> localeMap = new HashMap<>();
			for (final String component : components) {
				dto.setComponent(component);

				// Get existing data from cache.
				final CacheService cs = new CacheService(dto);
				final Map<String, String> translations = cs.getCacheOfComponent();

				// If cache doesn't have data, query from server.
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

		// Nothing to query, return.
		if (componentsToQuery.isEmpty() || localesToQuery.isEmpty()) {
			return retMap;
		}

		// Query from server.
		@SuppressWarnings("unchecked")
		final ArrayList<JSONObject> bundles = new ComponentsBasedOpt(componentsToQuery, localesToQuery)
		.getComponentsMessages();

		// combine returned data into the map to return.
		bundles.forEach(bundle -> {
			String locale = (String) bundle.get(ConstantsKeys.LOCALE);
			String comp = (String) bundle.get(ConstantsKeys.COMPONENT);
			JSONObject messages = (JSONObject) bundle.get(ConstantsKeys.MESSAGES);

			// update cache.
			dto.setComponent(comp);
			dto.setLocale(locale);
			new CacheService(dto).addCacheOfComponent(messages);

			// update map to return.
			Map<String, Map<String, String>> localeMap = retMap.get(locale);
			localeMap.put(comp, messages);
			//			retMap.put(locale, localeMap);
		});

		return retMap;
	}
}
