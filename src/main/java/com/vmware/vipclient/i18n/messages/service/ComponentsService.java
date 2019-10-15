/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentsBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class ComponentsService {

	Logger logger = LoggerFactory.getLogger(ComponentsService.class);
	private final List<String> components;
	private final List<Locale> locales;

	/**
	 * @param components
	 * @param locales
	 */
	public ComponentsService(List<String> components, List<Locale> locales) {
		this.components = components;
		this.locales = locales;
	}

	public Map<Locale, Map<String, Map<String, String>>> getTranslation() {
		final Map<Locale, Map<String, Map<String, String>>> retMap = new HashMap<>();

		final ArrayList<String> componentsToQuery = new ArrayList<String>() {};
		final ArrayList<Locale> localesToQuery = new ArrayList<Locale>() {
		};

		for (Locale locale : locales) {
			final MessagesDTO dto = new MessagesDTO();
			dto.setLocale(locale.toLanguageTag());
			Map<String, Map<String, String>> localeMap = new HashMap<String, Map<String, String>>() {
			};
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

		final JSONObject transMap = new ComponentsBasedOpt(componentsToQuery, localesToQuery)
				.getComponentsMessages();
		for (final Object entry : transMap.entrySet()) {
			JSONObject obj = (JSONObject)entry;
			String localestr = (String) obj.get("locale");
			Locale locale = Locale.forLanguageTag(localestr);
			String comp = (String) obj.get("component");
			Map<String, String> messages = (Map<String, String>) obj.get("messages");

			// update cache
			final MessagesDTO dto = new MessagesDTO();
			dto.setComponent(comp);
			dto.setLocale(localestr);
			final CacheService cs = new CacheService(dto);
			cs.addCacheOfComponent(messages);

			// construct return data
			Map<String, Map<String, String>> localeMap = retMap.get(locale);
			if(null == localeMap) {
				localeMap = new HashMap<String, Map<String, String>>() {};
			}
			localeMap.put(comp, messages);
			retMap.put(locale, localeMap);
		}

		return retMap;
	}
}
