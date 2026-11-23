/*
 * Copyright 2019-2026 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.pattern;

import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.vip.common.constants.ConstantsKeys;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Locale id selection in {@link PatternServiceImpl#buildPatternMap}.
 */
public class PatternServiceImplTest {

    @Test
    public void keepsLocaleIdWhenLanguageContainsValidRegion() throws Exception {
        LocaleDataDTO localeData = new LocaleDataDTO();
        localeData.setLocale("en");
        localeData.setDisplayLocaleID(true);
        List<String> categories = new ArrayList<String>();
        categories.add(ConstantsKeys.PLURALS);

        Map<String, Object> pattern = buildPatternMap("en-US", "US", patternJson("en"), categories, localeData);

        Assert.assertEquals("en-US", pattern.get(ConstantsKeys.LOCALEID));
    }

    @Test
    public void keepsCldrLocaleIdWhenLanguageAndRegionResolveToIt() throws Exception {
        LocaleDataDTO localeData = new LocaleDataDTO();
        localeData.setLocale("es-US");
        localeData.setDisplayLocaleID(true);
        List<String> categories = new ArrayList<String>();
        categories.add(ConstantsKeys.DATES);
        categories.add(ConstantsKeys.DATE_FIELDS);

        Map<String, Object> pattern = buildPatternMap("es", "US", patternJson("es-US"), categories, localeData);

        Assert.assertEquals("es-US", pattern.get(ConstantsKeys.LOCALEID));
    }

    @Test
    public void blanksLocaleIdWhenLanguageAndRegionCombinationIsInvalid() throws Exception {
        LocaleDataDTO localeData = new LocaleDataDTO();
        localeData.setLocale("en");
        localeData.setDisplayLocaleID(false);

        Map<String, Object> pattern = buildPatternMap("fr", "US", patternJson("en"),
                Arrays.asList(ConstantsKeys.DATES, ConstantsKeys.CURRENCIES), localeData);

        Assert.assertEquals("", pattern.get(ConstantsKeys.LOCALEID));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildPatternMap(String language, String region, String patternJson,
                                                 List<String> categories, LocaleDataDTO localeData) throws Exception {
        Method method = PatternServiceImpl.class.getDeclaredMethod("buildPatternMap", String.class, String.class,
                String.class, List.class, String.class, LocaleDataDTO.class);
        method.setAccessible(true);
        return (Map<String, Object>) method.invoke(new PatternServiceImpl(), language, region, patternJson,
                categories, null, localeData);
    }

    private static String patternJson(String localeId) {
        return "{\"localeID\":\"" + localeId + "\",\"categories\":{\"dates\":{},\"plurals\":{},\"currencies\":{},\"supplemental\":{}}}";
    }
}
