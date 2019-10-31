/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	private final SortedSet<String> components;
	private final SortedSet<String> locales;

	/**
	 * @param components2
	 * @param locales2
	 */
	public ComponentsService(Set<String> components2, Set<String> locales2) {
		this.components = new TreeSet<>(components2);
		this.locales = new TreeSet<>(locales2);
	}

	public Map<String, Map<String, Map<String, String>>> getTranslation() {
		final Map<String, Map<String, Map<String, String>>> retMap = new HashMap<>();

		final SortedSet<String> componentsToQuery = new TreeSet<>();
		final SortedSet<String> localesToQuery = new TreeSet<>();

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
		Map<String, String> localeMap = makeLocaleMap(localesToQuery, localesFromServer);

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

	private Map<String, String> makeLocaleMap(SortedSet<String> localesToQuery, List<String> localesFromServer) {
		if (localesToQuery.size() != localesFromServer.size()) {
			throw new VIPJavaClientException(ConstantsMsg.SERVER_CONTENT_ERROR);
		}

		HashMap<String, String> map = new HashMap<>();
		Iterator<String> iterOriginal = localesToQuery.iterator();
		Iterator<String> iterServer = localesFromServer.iterator();
		while (iterOriginal.hasNext()) {
			map.put(iterServer.next(), iterOriginal.next());
		}

		return map;
	}
}
