/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import com.vmware.vip.common.constants.ConstantsUnicode;
import org.apache.commons.lang3.StringUtils;

/**
 * This class is used for locale and language tag conversion
 */
public class LocaleUtils {

	public static final String[] defaultLocales = { "en" };

	/**
	 * validate that an argument is a well-formed BCP 47 tag
	 * 
	 * @param languageTag
	 * @return true if the format is fine
	 */
	public static boolean isLanguageTag(String languageTag) {
		try {
			new Locale.Builder().setLanguageTag(languageTag).build();
			return true;
		} catch (IllformedLocaleException e) {
			return false;
		}
	}

	/**
	 * Normalize a language tag as locale string, e.g. ja-JP will be normalized as ja_JP
	 * 
	 * @param languageTag
	 * @return normalized locale string
	 */
	public static String normalizeLocaleStr(String languageTag) {
		if (isLanguageTag(languageTag)) {
			Locale.Builder builder = new Locale.Builder();
			builder.setLanguageTag(languageTag);
			Locale locale = builder.build();
			if(languageTag.equalsIgnoreCase("zh-Hans") || languageTag.equalsIgnoreCase("zh-Hant")){
			    return locale.toString().replaceFirst("_", "").replace("#", ""); 
			}else{
			    return locale.toString().replace("#", "");
			}
		}
		return languageTag;
	}

	/**
	 * Normalize a language tag array as a string array, e.g. {"zh_CN", "ja-JP"}
	 * will be normalized as {"zh_CN", "ja_JP"}
	 * 
	 * @param languageTags
	 * @return normalized locale array
	 */
	public static String[] normalizeLocaleStr(String[] languageTags) {
		List<String> normalizeLocales = new ArrayList<String>();
		for (String languageTag : languageTags) {
			normalizeLocales.add(LocaleUtils.normalizeLocaleStr(languageTag
					.trim()));
		}
		return normalizeLocales.toArray(new String[normalizeLocales.size()]);
	}

	/*
	 * Judge if a locale object is English locale.
	 */
	public static boolean isDefaultLocale(Locale locale) {
		if (locale != null) {
			return LocaleUtils.isDefaultLocale(locale.toLanguageTag());
		} else {
			return false;
		}
	}

	/*
	 * Judge if a locale string is English locale string.
	 */
	public static boolean isDefaultLocale(String locale) {
		boolean isDefault = false;
		if (!StringUtils.isEmpty(locale)) {
			for (String ls : LocaleUtils.defaultLocales) {
				if (locale.equals(ls)) {
					isDefault = true;
					break;
				}
			}
		}
		return isDefault;
	}

    /**
     * normalize a locale string(e.g. 'zh__#Hans', 'zh_CN_#Hans') to language tag(e.g. 'zh-Hans', 'zh-Hans-CN').
     */
    public static String normalizeToLanguageTag(String localeStr) {
        if(null ==localeStr || "".equalsIgnoreCase(localeStr)){
            return localeStr;
        }
		String localeTag = localeStr.replace("_", "-");
		if(localeTag.indexOf(ConstantsUnicode.ALT)>0)//handle languages like en-US-alt-short
			localeTag = localeTag.substring(0, localeTag.indexOf(ConstantsUnicode.ALT));
        if (isLanguageTag(localeTag)) {
            return Locale.forLanguageTag(localeTag).toLanguageTag();
        } else {
            String language = "", country = "", script = "";
            String[] os = localeStr.split("-");
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
                }

            }
            return language + script + country;
        }
    }

}
