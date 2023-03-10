/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.common.Constants;
import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.i18n.pattern.service.impl.PatternServiceImpl;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.*;

@SuppressWarnings("restriction")
public class CommonUtil {

    /**
     * Use for string not null validation
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * Parse locale and match cldr locale path: e.g. zh-Hans-CN => zh-Hans, zh-CN => zh-Hans-CN =>
     * zh-Hans, if no matching item, return zh
     *
     * @param locale          translation locale
     * @param allLocales      cldr all locales
     * @param likelySubtagMap data from likeliSubtags.json
     * @return
     */
    public static String getPathLocale(String locale, Map<String, String> allLocales,
                                       Map<String, Object> likelySubtagMap) {
        String cldrLocale = allLocales.get(locale.toLowerCase());
        if (CommonUtil.isEmpty(cldrLocale) && locale.contains("-")) {
            String[] localeList = locale.split("-");
            int size = localeList.length;
            switch (size) {
                case 3:
                    cldrLocale = allLocales.get((localeList[0] + "-" + localeList[1]).toLowerCase());// e.g. zh-Hans
                    if (CommonUtil.isEmpty(cldrLocale)) {
                        cldrLocale = allLocales.get((localeList[0] + "-" + localeList[2]).toLowerCase());// zh-CN
                    }
                    break;
                case 2:// e.g. zh-Hans or zh-CN => zh-Hans-CN
                    String likelySubStr = (String) likelySubtagMap.get("und-" + localeList[1].toLowerCase());
                    if (CommonUtil.isEmpty(likelySubStr) || !localeList[0].equals(likelySubStr.split("-")[0])) {
                        break;
                    }
                    if (!CommonUtil.isEmpty(likelySubStr)) {
                        cldrLocale = allLocales.get(likelySubStr.toLowerCase());
                        if (CommonUtil.isEmpty(cldrLocale)) {
                            String[] likelySubStrArr = likelySubStr.split("-");
                            cldrLocale = allLocales.get((likelySubStrArr[0] + "-" + likelySubStrArr[1]).toLowerCase());
                        }
                    }
                    if (CommonUtil.isEmpty(cldrLocale)) {
                        cldrLocale = allLocales.get(localeList[0]);
                    }
                    break;
                default:
                    break;
            }
        }
        return cldrLocale;
    }

    /**
     * validate that an argument is a well-formed BCP 47 tag
     *
     * @param languageTag
     * @return true if the format is fine
     */
    public static boolean isLanguageTag(String languageTag) {
        return Locale.forLanguageTag(languageTag) != null;
    }

    /**
     * normalize a locale string(e.g. 'zh__#Hans', 'zh_CN_#Hans') to language tag(e.g. 'zh-Hans', 'zh-Hans-CN').
     */
    public static String normalizeToLanguageTag(String localeStr) {
        if (isLanguageTag(localeStr) || null == localeStr || "".equalsIgnoreCase(localeStr)) {
            return localeStr;
        } else {
            String language = "", country = "", script = "";
            String[] os = localeStr.split("_");
            for (int i = 0; i < os.length; i++) {
                switch (i) {
                    case 0:
                        language = os[0];
                        continue;
                    case 1:
                        country = "".equalsIgnoreCase(os[1]) ? "" : "-" + os[1];
                        continue;
                    case 2:
                        script = "".equalsIgnoreCase(os[2]) ? "" : "-" + os[2].replace("#", "");
                        continue;
                    default:
                        break;
                }
            }
            return language + script + country;
        }
    }

    /**
     * Get locale by specific language and region
     *
     * @param language translation language
     * @param region   translation region
     * @return LocaleDataDTO
     */
    public static LocaleDataDTO getLocale(String language, String region) {
        LocaleDataDTO resultData = new LocaleDataDTO();
        language = language.replace("_", "-");
        String locale = language + "-" + region;
        String cldrLocale = getCLDRLocale(locale, localePathMap, localeAliasesMap);
        if (!isEmpty(cldrLocale)) {
            resultData.setLocale(cldrLocale);
            return resultData;
        }

        String[] languageDataList = language.split("-");
        int size = languageDataList.length;
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                if (region.toLowerCase().equals(languageDataList[i].toLowerCase())) {
                    locale = language;
                    cldrLocale = getCLDRLocale(locale, localePathMap, localeAliasesMap);
                    break;
                }
            }

            /*VIP-1915 [VIPServer]Server returns wrong pattern data for language 'es-MX' and region 'US'
            * Judge whether language contains script or not according to languageData.json,if not,
            * splice language and region*/
            if (isEmpty(cldrLocale)) {
                String segmentedLanguage = languageDataList[0];
                String languageScript = languageDataList[1];
                // VIP-1944 Language name with language-script-country format can't be handled correctly in get pattern API.
                if (size > 2) {
                    locale = segmentedLanguage + "-" + languageScript + "-" + region;
                    cldrLocale = getCLDRLocale(locale, PatternServiceImpl.localePathMap, PatternServiceImpl.localeAliasesMap);
                }

                if (isEmpty(cldrLocale) && !isEmpty(languageDataMap.get(segmentedLanguage))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> languageData = (Map<String, Object>) languageDataMap.get(segmentedLanguage);
                    if (!isEmpty(languageData.get(Constants._SCRIPTS))) {
                        @SuppressWarnings("unchecked")
                        List<String> scripts = (List<String>) languageData.get(Constants._SCRIPTS);
                        boolean existenceOfScript = false;
                        for (String script : scripts) {
                            if (languageScript.toLowerCase().equals(script.toLowerCase())) {
                                existenceOfScript = true;
                            }
                        }

                        if (!existenceOfScript) {
                            locale = segmentedLanguage + "-" + region;
                            cldrLocale = getCLDRLocale(locale, PatternServiceImpl.localePathMap, PatternServiceImpl.localeAliasesMap);
                        }
                    }
                }
            }
        }

        if (isEmpty(cldrLocale)) {
            language = regionMap.get(region.toUpperCase());
            if (!CommonUtil.isEmpty(language)) {
                locale = language + "-" + region;
                resultData.setDisplayLocaleID(false);
            }
        }

        resultData.setLocale(getCLDRLocale(locale, localePathMap, localeAliasesMap));
        return resultData;
    }

    /**
     * Query whether locale exists, if not, query defaultContent.json
     *
     * @param locale
     * @param availableLocales
     * @param localeAliases
     * @return
     */
    public static String getCLDRLocale(String locale, Map<String, String> availableLocales,
                                       Map<String, Object> localeAliases) {
        String matchLocale = getMatchingLocale(localeAliases, locale);
        if (!CommonUtil.isEmpty(matchLocale)) {
            locale = matchLocale;
        }
        String cldrLocale = availableLocales.get(locale.toLowerCase());
        if (CommonUtil.isEmpty(cldrLocale)) {
            cldrLocale = PatternUtil.getMatchingLocaleFromLib(locale);
        }
        return cldrLocale;
    }

    /**
     * Query aliases.json for a matching locale
     *
     * @param languageAliases
     * @param locale
     * @return
     */
    public static String getMatchingLocale(Map<String, Object> languageAliases, String locale) {
        for (Map.Entry<String, Object> item : languageAliases.entrySet()) {
            if (locale.toLowerCase().equals(item.getKey().toLowerCase())) {
                @SuppressWarnings("unchecked")
				Map<String, String> data = (Map<String, String>) item.getValue();
                locale = data.get(Constants.REPLACEMENT);
                return locale;
            }
        }
        return "";
    }

    /**
     * Query plurals.json
     *
     * @param language
     * @return
     */
    public static Map<String, Object> getMatchingPluralByLanguage(String language) {
        for (Map.Entry<String, Object> item : pluralsMap.entrySet()) {
            if (language.toLowerCase().equals(item.getKey().toLowerCase())) {
                @SuppressWarnings("unchecked")
				Map<String, Object> data = (Map<String, Object>) item.getValue();
                return data;
            }
        }
        return null;
    }
}
