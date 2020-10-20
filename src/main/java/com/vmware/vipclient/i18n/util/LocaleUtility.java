/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.util.*;
import java.util.stream.Collectors;

public class LocaleUtility {

    private static Locale defaultLocale;
    private static Locale sourceLocale = Locale.ENGLISH;
    
    // A locale fallback priority queue. For now, it only contains the default locale and the source messages (from "messages_source.json").
    private static List<Locale> fallbackLocales = new LinkedList<Locale>(Arrays.asList(getDefaultLocale(), Locale.forLanguageTag(ConstantsKeys.SOURCE)));
    
    // Use ThreadLocal to combine the locale with local thread so that the
    // locale can be used by any code places.
    private static InheritableThreadLocal<Map<String, Locale>> threadLocal    = new InheritableThreadLocal<Map<String, Locale>>() {
                                                                                  @Override
                                                                                  protected Map<String, Locale> initialValue() {
                                                                                      return new HashMap<>();
                                                                                  }

                                                                                  @Override
                                                                                  protected Map<String, Locale> childValue(
                                                                                          Map<String, Locale> parentValue) {
                                                                                      return parentValue.entrySet()
                                                                                              .stream().collect(
                                                                                                      Collectors.toMap(
                                                                                                              Map.Entry::getKey,
                                                                                                              e -> (new Locale.Builder())
                                                                                                                      .setLocale(
                                                                                                                              e.getValue())
                                                                                                                      .build()));
                                                                                  }
                                                                              };

    /**
     * Set the locale to ThreadLocal
     *
     * @param locale
     */
    public static void setLocale(Locale locale) {
        Map<String, Locale> localeMap = threadLocal.get();
        localeMap.put(ConstantsKeys.LOCALE_L3, locale);
        threadLocal.set(localeMap);
    }

    /**
     * get the locale from ThreadLocal
     *
     * @param locale
     */
    public static Locale getLocale() {
        Map<String, Locale> localeMap = threadLocal.get();
        Locale locale = localeMap.get(ConstantsKeys.LOCALE_L3);
        if (locale == null) {
            return defaultLocale;
        } else {
            return locale;
        }
    }

    /**
     * Set the locale to ThreadLocal
     *
     * @param locale
     */
    public static void setL2Locale(Locale locale) {
        Map<String, Locale> localeMap = threadLocal.get();
        localeMap.put(ConstantsKeys.LOCALE_L2, locale);
        threadLocal.set(localeMap);
    }

    /**
     * get the locale from ThreadLocal
     *
     * @param locale
     */
    public static Locale getL2Locale() {
        Map<String, Locale> localeMap = threadLocal.get();
        Locale locale = localeMap.get(ConstantsKeys.LOCALE_L2);
        if (locale == null) {
            return defaultLocale;
        } else {
            return locale;
        }
    }

    /*
     * Check if a locale is the default
     */
    public static boolean isDefaultLocale(Locale locale) {
        if (locale != null) {
            return LocaleUtility.isDefaultLocale(locale.toLanguageTag());
        } else {
            return false;
        }
    }

    /*
     * Check if a language tag matches the default locale that is from the config file
     */
    public static boolean isDefaultLocale(String languageTag) {
    	languageTag = languageTag.replaceAll("_", "-");
        Locale match = Locale.lookup(Arrays.asList(new Locale.LanguageRange(languageTag)),
        		Arrays.asList(getDefaultLocale()));
        return match != null;
    }

    /*
     * format a locale to the mapped locale, e.g. zh-CN -> zh-Hans
     */
    public static Locale fmtToMappedLocale(String zhLocale) {
        return fmtToMappedLocale(Locale.forLanguageTag(zhLocale.replace("_", "-")));
    }

    /*
     * format a locale to the mapped locale, e.g. zh-CN -> zh-Hans
     */
    public static Locale fmtToMappedLocale(Locale zhLocale) {
        if (zhLocale.toLanguageTag().equalsIgnoreCase("zh-CN")
                || zhLocale.toLanguageTag().equalsIgnoreCase(
                        "zh-Hans-CN")) {
            return Locale.forLanguageTag("zh-Hans");
        } else if (zhLocale.toLanguageTag().equalsIgnoreCase("zh-TW")
                || zhLocale.toLanguageTag().equalsIgnoreCase(
                        "zh-Hant-TW")) {
            return Locale.forLanguageTag("zh-HANT");
        }
        return zhLocale;
    }

    public static boolean isSameLocale(String locale1, String locale2) {
        String loc1 = fmtToMappedLocale(locale1).toLanguageTag();
        String loc2 = fmtToMappedLocale(locale2).toLanguageTag();
        return loc1.equals(loc2);
    }

    /*
     * pick up the matched locale from a locale list
     */
    public static Locale pickupLocaleFromList(Set<Locale> locales,
                                              Locale preferredLocale) {
		Locale localeObject = fmtToMappedLocale(preferredLocale);
		Locale bestMatch = Locale.lookup(Arrays.asList(new Locale.LanguageRange(localeObject.toLanguageTag())),
				locales);

        // For any Chinese locale (zh-*) that is not supported (except for zh-Hans and zh-Hant), use the fallback locale even if "zh" is supported.
        if (preferredLocale.getLanguage().equals("zh")) {
            Locale zhLocale = fmtToMappedLocale(preferredLocale);
            if (!locales.contains(zhLocale)) {
                String langTag = zhLocale.toLanguageTag();
                if (!langTag.equals("zh-Hans") && !langTag.equals("zh-Hant"))
                    return null;
            }

        }
        return bestMatch;
    }

    /**
     * normalize a locale string(e.g. 'zh__#Hans', 'zh_CN_#Hans') to language tag(e.g. 'zh-Hans', 'zh-Hans-CN').
     */
    public static String normalizeToLanguageTag(String localeStr) {
        if (null == localeStr || "".equalsIgnoreCase(localeStr)) {
            return localeStr;
        }
        if (isLanguageTag(localeStr)) {
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
                }

            }
            return language + script + country;
        }
    }

    /**
     * validate that an argument is a well-formed BCP 47 tag
     * 
     * @param languageTag
     * @return true if the format is fine
     */
    public static boolean isLanguageTag(String languageTag) {
        if (null == languageTag || "".equalsIgnoreCase(languageTag)) {
            return false;
        }
        return languageTag.contains("-");
    }

	public static Locale getDefaultLocale() {
		return defaultLocale == null ? getSourceLocale() : defaultLocale;
	}

	public static void setDefaultLocale(Locale defaultLocale) {
		LocaleUtility.defaultLocale = defaultLocale;
	}

	public static Locale getSourceLocale() {
		return sourceLocale; 
	}

	public static void setSourceLocale(Locale sourceLocale) {
		LocaleUtility.sourceLocale = sourceLocale;
	}

	public static List<Locale> getFallbackLocales() {
		return fallbackLocales;
	}

	public static void setFallbackLocales(List<Locale> fallbackLocales) {
		LocaleUtility.fallbackLocales = fallbackLocales;
	}
    
}
