/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.utils.JSONUtil;
import com.vmware.i18n.utils.timezone.TimeZoneName;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Unit testing of PatternUtil
 */

public class PatternUtilTest {

    /**
     * Query defaultContent.json to determine whether there is a matching locale,
     * and if so, get the processed result and run
     */
    @Test
    public void getMatchingLocaleFromLib() {
        String locale = PatternUtil.getMatchingLocaleFromLib("en_US");
        Assert.assertEquals("en", locale);
    }
    
    @SuppressWarnings("unchecked")
	@Test
	public void testGetPatternAPI() {
    	String locale = "fr";
		String cateStr= "dates,numbers,plurals";
		String json = PatternUtil.getPatternFromLib(locale, cateStr);
		Map<String, Object> patternMap = (Map<String, Object>) JSONUtil.getMapFromJson(json);
		Map<String, Object> catesMap = (Map<String, Object>) patternMap.get("categories");
		String[] catesArr = cateStr.split(",");
		for (String cate : catesArr) {
			Assert.assertNotNull(catesMap.get(cate));
		}
	}
    
    @SuppressWarnings("unused")
	@Test
	public void testtimezoneListAPI() {
    	String locale = "fr";
    	TimeZoneName json = PatternUtil.getTimeZoneName(locale, true);
		Assert.assertEquals("fr", json.getLanguage());
		Assert.assertNotNull(json.getTimeZoneNames());
    }

	@Test
	public void testGetLanguageFromLib() {
		String locale = "en";
		String json = PatternUtil.getLanguageFromLib(locale);
		Map<String, Object> languagesMap = (Map<String, Object>) JSONUtil.getMapFromJson(json);
		Assert.assertEquals("en", languagesMap.get("displayLanguage"));
		Assert.assertNotNull(languagesMap.get("languages"));
	}

	@Test
	public void testGetContextTransformFromLib() {
		String locale = "en";
		String json = PatternUtil.getContextTransformFromLib(locale);
		Map<String, Object> contextTransformsMap = (Map<String, Object>) JSONUtil.getMapFromJson(json);
		Assert.assertEquals("en", contextTransformsMap.get("languages"));
		Assert.assertNotNull(contextTransformsMap.get("contextTransforms"));
	}
}
