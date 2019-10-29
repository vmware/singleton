/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
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

		final LinkedHashSet<String> componentsToQuery = new LinkedHashSet<>();
		final LinkedHashSet<String> localesToQuery = new LinkedHashSet<>();

		final MessagesDTO dto = new MessagesDTO();

		// Get data from cache first. If cache doens't have, query from server.
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
		ComponentsBasedOpt opt = new ComponentsBasedOpt(componentsToQuery, localesToQuery);
		final JSONObject response = opt.queryFromServer();
		JSONArray bundles = (JSONArray) opt.getDataPart(response).get(ConstantsKeys.BUNDLES);
		JSONArray localesFromServer = (JSONArray) opt.getDataPart(response).get(ConstantsKeys.LOCALES);
		Map<String, String> localeMap = makeLocaleMap(locales, localesFromServer);

		// combine data from server into the map to return.
		Iterator<?> iter = bundles.iterator();
		while (iter.hasNext()) {
			final JSONObject bundle = (JSONObject) iter.next();
			String locale = localeMap.get(bundle.get(ConstantsKeys.LOCALE));
			String comp = (String) bundle.get(ConstantsKeys.COMPONENT);
			JSONObject messages = (JSONObject) bundle.get(ConstantsKeys.MESSAGES);

			// update cache.
			dto.setComponent(comp);
			dto.setLocale(locale);
			new CacheService(dto).addCacheOfComponent(messages);

			// update map to return.
			retMap.get(locale).put(comp, messages);
		}

		return retMap;
	}

	private Map<String, String> makeLocaleMap(List<String> originalLocales, List<String> localesFromServer) {
		if(originalLocales.size() != localesFromServer.size()) {
			throw new VIPJavaClientException(ConstantsMsg.SERVER_CONTENT_ERROR);
		}

		HashMap<String, String> map = new HashMap<>();
		Iterator<String> iterOriginal = originalLocales.iterator();
		Iterator<String> iterServer = localesFromServer.iterator();
		while (iterOriginal.hasNext()) {
			map.put(iterServer.next(), iterOriginal.next());
		}

		return map;
	}
}
