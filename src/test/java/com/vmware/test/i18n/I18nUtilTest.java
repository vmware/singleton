/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test.i18n;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.utils.CLDRUtils;

public class I18nUtilTest {

	final String LOCALE = "fr";
	final String testURL = "https://github.com/unicode-cldr/cldr-core/archive/32.0.0.zip";
	final String[] catesArr = { "dates", "numbers", "plurals", "measurements"};
/**
	@Before
	public void network() throws Exception {
		URL urlObj = new URL(testURL);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		conn.setConnectTimeout(30 * 1000);
		int status = conn.getResponseCode();
		Assert.assertEquals(200, status);
	}
**/
	@Test
	public void testDataExtract() {
		Map<String, Object> patternMap = new LinkedHashMap<String, Object>();
		Map<String, Object> catesMap = new HashMap<String, Object>();
		patternMap.put("localeID", LOCALE);
		catesMap.put("dates", CLDRUtils.dateDataExtract(LOCALE));
		catesMap.put("numbers", CLDRUtils.numberDataExtract(LOCALE));
		catesMap.put("plurals", CLDRUtils.getPluralsData(LOCALE));
		catesMap.put("measurements", CLDRUtils.getMeasurementsData(LOCALE));
		patternMap.put("categories", catesMap);
		for (String key : catesArr) {
			Assert.assertNotNull(catesMap.get(key));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLocalesExtract() {
		
		String languages="zh,en,zh-Hant,zh-Hans-HK";
		
		try {
			String[] languageArray=languages.split(",");
			for(int i=0;i<languageArray.length;i++) {
				String regions = PatternUtil.getRegionFromLib(languageArray[i]);
				Map<String, Object> genreJsonObject = null;
				JSONObject jsonObject = new JSONObject(regions);
				genreJsonObject = jsonObject.toMap();
				Map<String, Object> territoriesObject = (Map<String, Object>) genreJsonObject.get("territories");
				// genreJsonObject = (Map<String, Object>) new ObjectMapper().readValue(regions, HashMap.class);
				// Map<String, Object> territoriesObject = (Map<String, Object>) new ObjectMapper().readValue(genreJsonObject.get("territories").toString(), HashMap.class);
				if (languageArray[i].equals("zh")) {
					Assert.assertEquals("zh", genreJsonObject.get("language"));
					Assert.assertNotNull(territoriesObject.get("TW"));
					Assert.assertEquals("台湾", territoriesObject.get("TW"));

					Assert.assertEquals("阿森松岛", territoriesObject.get("AC"));
					Assert.assertEquals("南极洲", territoriesObject.get("AQ"));
					Assert.assertEquals("布韦岛", territoriesObject.get("BV"));
					Assert.assertEquals("克利珀顿岛", territoriesObject.get("CP"));

					Assert.assertEquals("南乔治亚和南桑威奇群岛", territoriesObject.get("GS"));
					Assert.assertEquals("赫德岛和麦克唐纳群岛", territoriesObject.get("HM"));
					Assert.assertEquals("马尔代夫", territoriesObject.get("MV"));
					Assert.assertEquals("特里斯坦-达库尼亚群岛", territoriesObject.get("TA"));

					Assert.assertEquals("法属南部领地", territoriesObject.get("TF"));
					Assert.assertEquals("未知地区", territoriesObject.get("ZZ"));
					Assert.assertEquals("", genreJsonObject.get("defaultRegionCode"));
				}
				if (languageArray[i].equals("zh-Hant")) {
					Assert.assertEquals("zh", genreJsonObject.get("language"));
					Assert.assertNotNull(territoriesObject.get("TW"));
					Assert.assertEquals("台灣", territoriesObject.get("TW"));
					Assert.assertEquals("義大利", territoriesObject.get("IT"));
					Assert.assertEquals("TW", genreJsonObject.get("defaultRegionCode"));

				}
				if (languageArray[i].equals("en")) {
					Assert.assertEquals("en", genreJsonObject.get("language"));
					Assert.assertNotNull(territoriesObject.get("TW"));
					Assert.assertEquals("Taiwan", territoriesObject.get("TW"));
					Assert.assertEquals("North Korea", territoriesObject.get("KP"));
					Assert.assertEquals("US", genreJsonObject.get("defaultRegionCode"));
				}
				if (languageArray[i].equals("zh-Hans-HK")) {
					Assert.assertEquals("HK", genreJsonObject.get("defaultRegionCode"));
				}

			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
