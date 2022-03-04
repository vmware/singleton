/*
 * Copyright 2019-2022 VMware, Inc.
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

    /**
     * Iterates over the set of locales to find a locale that best matches the preferredLocale.
     *
     * <p> </p>A "best match" is defined to be the locale that has the longest common language tag with the preferredLocale.
     * For example, the supported locale 'de' will be returned for a non-supported preferredLocale 'de-DE'. </p>
     *
     * <p> To meet the custom usage of Chinese language, locale "zh" is not considered as a "match" for any non-supported Chinese locale (zh-*).
     * That is, even if "zh" locale is supported, <code>null</code> will be returned for Chinese locale 'zh-HK' that is not supported. </p>
     *
     * @param locales the set of locales to find the best match from.
     * @param preferredLocale the locale being matched.
     * @return the best match, if any; <code>null</code> otherwise.
     */
    public static Locale pickupLocaleFromList(Set<Locale> locales,
                                              Locale preferredLocale) {
		Locale localeObject = fmtToMappedLocale(preferredLocale);
		Locale bestMatch = Locale.lookup(Arrays.asList(new Locale.LanguageRange(localeObject.toLanguageTag())),
				locales);

        // handle Chinese locale matching
        if (bestMatch != null && bestMatch.getLanguage().equals("zh")) {
            if (!locales.contains(localeObject)) {
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

    public static Set<Locale> langTagtoLocaleSet (Set<String> languageTags) {
        Set<Locale> locales = new HashSet<>();
        if (languageTags != null) {
            for (String languageTag : languageTags) {
                locales.add(Locale.forLanguageTag(languageTag));
            }
        }
        return locales;
    }

}
