/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.pattern.action.PatternAction;
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
		PatternAction pa = PatternAction.getInstance();
		String json = pa.getPattern(locale, cateStr);
		Map<String, Object> patternMap = (Map<String, Object>) JSONUtil.getMapFromJson(json);
		Map<String, Object> catesMap = (Map<String, Object>) patternMap.get("categories");
		String[] catesArr = cateStr.split(",");
		for (String cate : catesArr) {
			Assert.assertNotNull(catesMap.get(cate));
		}
	}
    
    @SuppressWarnings("unchecked")
	@Test
	public void testtimezoneListAPI() {
    	String locale = "fr";
    	PatternAction pa = PatternAction.getInstance();
    	TimeZoneName json = pa.getTimeZoneName(locale, true);
    }
}
