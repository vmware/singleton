/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class LocaleUtility {
    protected static final String[]                            defaultLocales = { "en", "en-US", "en_US" };

    public static final Locale                                 defaultLocale  = Locale.US;

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
     * Judge if a locale object is English locale.
     */
    public static boolean isDefaultLocale(Locale locale) {
        if (locale != null) {
            return LocaleUtility.isDefaultLocale(locale.toLanguageTag());
        } else {
            return false;
        }
    }

    /*
     * Judge if a locale string is English locale string.
     */
    public static boolean isDefaultLocale(String locale) {
        boolean isDefault = false;
        if (locale != null && !locale.trim().equals("")) {
            for (String ls : LocaleUtility.defaultLocales) {
                if (locale.equals(ls)) {
                    isDefault = true;
                    break;
                }
            }
        }
        return isDefault;
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

    /*
     * pick up the matched locale from a locale list
     */
    public static Locale pickupLocaleFromList(List<Locale> locales,
            Locale preferredLocale) {
        Locale langLocale = null;
        preferredLocale = fmtToMappedLocale(preferredLocale);

        // Use the first locale from the browser's list of preferred languages
        // for the matching, so that it can keep the same way of getting locale
        // with other VMware products, like vSphere Web Client, etc.
        for (Locale configuredLocale : locales) {
            // Language is matched
            configuredLocale = fmtToMappedLocale(configuredLocale);
            if (configuredLocale.getLanguage().equals(
                    preferredLocale.getLanguage())) {
                String configuredScript = configuredLocale.getScript();
                String preferredScript = preferredLocale.getScript();
                // Country is matched
                if (((preferredScript.equalsIgnoreCase("")) && (configuredScript
                        .equalsIgnoreCase("")))
                        || ((!preferredScript.equalsIgnoreCase("")) && (preferredScript
                                .equalsIgnoreCase(configuredScript)))) {
                    return configuredLocale;
                }
                langLocale = langLocale == null ? configuredLocale : langLocale;
            }
        }

        // With Chinese locale which is not configured/supported in web.xml, it
        // will return 'en_US' as default to meet the usage custom of Chinese,
        // e.g. for 'zh-HK' from client(browser) which is not
        // configured/supported yet, it will return 'en_US';
        // Other locale, like 'de-DE' 'ja-JP' etc.,
        // it will return 'de' 'ja'(main/parent language).
        if (langLocale != null
                && (!langLocale.getLanguage().equalsIgnoreCase("zh"))) {
            return new Locale(langLocale.getLanguage());
        }
        return preferredLocale;
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
}
