/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.azure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vmware.vip.common.http.HTTPRequester;
import com.vmware.vip.common.utils.LocaleUtils;
import com.vmware.vip.messages.data.dao.api.IMTProcessor;
import com.vmware.vip.messages.data.dao.exception.MTException;
import com.vmware.vip.messages.mt.MTConfig;

public class AzureTranslatingProcessor implements IMTProcessor {
	private static Logger logger = LoggerFactory.getLogger(AzureTranslatingProcessor.class);

	public String translateString(String fromLang, String toLang, String source)
			throws MTException {
		List<String> sourceList = new ArrayList<String>();
		sourceList.add(source);
		List<String> translationList = this.translateArray(fromLang, toLang,
				sourceList);
		return translationList.size() == 1 ? translationList.get(0) : "";
	}

	public List<String> translateArray(String fromLang, String toLang,
			List<String> sourceList) throws MTException {
		String key = LocaleUtils.normalizeToLanguageTag(fromLang) + "_" + LocaleUtils.normalizeToLanguageTag(toLang);
		ArrayNode resultArrayNode = null;
		if(MTConfig.isTranslatedFull(key)) {
			throw new MTException("Request failed: daily MT translated wordcount is over!");
		}
		try {
			logger.info("-----Start to request MT translation----------");
			FromModel fromObj = this.getFromObj(
					LocaleUtils.normalizeToLanguageTag(fromLang),
					LocaleUtils.normalizeToLanguageTag(toLang), sourceList);
			ObjectMapper mapper = new ObjectMapper();
			String requestJson = mapper.writeValueAsString(fromObj
					.getSourceObjectList());
			String urlStr = MTConfig.MTSERVER + fromObj.getToLang();
			logger.info("Using MT Server:" + MTConfig.MTSERVER);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Ocp-Apim-Subscription-Key", MTConfig.KEY);
			if(StringUtils.isNotBlank(MTConfig.REGION)) {
                headers.put("Ocp-Apim-Subscription-Region", MTConfig.REGION);
            }
            String response = HTTPRequester.postData(requestJson, urlStr,
					"application/json", "POST", headers);
			if(response == null || response.equalsIgnoreCase("")) {
				throw new MTException("Request failed: respond empty from MT server!");
			}
			resultArrayNode = mapper.readValue(response, ArrayNode.class);
		} catch (IOException e) {		
			logger.error("Failed to request MT translation");
		}
		if(resultArrayNode != null) {
			MTConfig.updateTranslationCache(key, resultArrayNode.size());
		}
		return this.getTargetList(resultArrayNode);
	}

	private FromModel getFromObj(String fromLang, String toLang,
			List<String> sourceList) {
		FromModel from = new FromModel();
		from.setFromLang(fromLang);
		from.setToLang(toLang);
		from.setSourceList(sourceList);
		return from;
	}

	private List<String> getTargetList(ArrayNode arrayNode) {
		List<String> targetList = new ArrayList<String>();
		ObjectMapper mapper = new ObjectMapper();
		for (JsonNode jo : arrayNode) {
			try {
				ToResult to = mapper.readValue(jo.toString(), ToResult.class);
				ArrayNode toList = to.getTranslations();
				for (JsonNode textNode : toList) {
					ToTextModel toText = mapper.readValue(textNode.toString(),
							ToTextModel.class);
					String translation = toText.getText();
					targetList.add(translation);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return targetList;
	}

}
