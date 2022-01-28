/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.transportal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.http.HTTPRequester;
import com.vmware.vip.common.utils.LocaleUtils;
import com.vmware.vip.messages.data.dao.api.IMTProcessor;
import com.vmware.vip.messages.data.dao.exception.MTException;
import com.vmware.vip.messages.mt.MTConfig;

@Repository
public class PortalTranslatingProcessor implements IMTProcessor {
	private static Logger logger = LoggerFactory.getLogger(PortalTranslatingProcessor.class);

	public String translateString(String fromLang, String toLang, String source) throws MTException {
		List<String> sourceList = new ArrayList<String>();
		sourceList.add(source);
		List<String> translationList = this.translateArray(fromLang, toLang,
				sourceList);
		return translationList.size() == 1 ? translationList.get(0) : "";
	}

	public List<String> translateArray(String fromLang, String toLang,
			List<String> sourceList) throws MTException {
		ToResult t = null;
		if(MTConfig.isTranslatedFull(LocaleUtils.normalizeToLanguageTag(toLang))) {
			return sourceList;
		}
		try {
			logger.info("-----Start to send translation----------");
			FromModel fromObj = this.getFromObj(
					LocaleUtils.normalizeToLanguageTag(fromLang),
					LocaleUtils.normalizeToLanguageTag(toLang), sourceList);
			ObjectMapper mapper = new ObjectMapper();
			String requestJson = mapper.writeValueAsString(fromObj);
			logger.info("Using MT Server:");
			logger.info(MTConfig.MTSERVER);
			String response = HTTPRequester.postData(requestJson, MTConfig.MTSERVER,
					"application/json", "POST", null);
			t = mapper.readValue(response, ToResult.class);
		} catch (IOException e) {			
			logger.info("Failed to send translation for source collection");
			throw new MTException("Failed to send translation for source collection");
		}
		MTConfig.TRANSLATED_CACHE.put(LocaleUtils.normalizeToLanguageTag(fromLang) + "_" + LocaleUtils.normalizeToLanguageTag(toLang), t.getTargetList().size());
		return t.getTargetList();
	}

	private FromModel getFromObj(String f, String to, List<String> sourceList) {
		FromModel from = new FromModel();
		from.setFromLang(f);
		from.setToLang(to);
		from.setSourceList(sourceList);
		return from;
	}

}
