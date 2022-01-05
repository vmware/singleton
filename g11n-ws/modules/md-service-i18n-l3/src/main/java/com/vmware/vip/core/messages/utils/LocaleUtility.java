/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.utils;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.util.StringUtils;

public class LocaleUtility {
	private static final String[] DEFAULT_LOCALES = { "en", "en-US", "en_US" };
	private static final Locale DEFAULT_LOCALE = new Locale("en", "");
	private static final ResourceBundle LOCALE_MAP = ResourceBundle
			.getBundle("map");

	// Use ThreadLocal to combine the locale with local thread so that the
	// locale can be used by any code places.
	private static InheritableThreadLocal<Locale> threadLocal = new InheritableThreadLocal<Locale>() {
		@Override
		protected Locale initialValue() {
			return DEFAULT_LOCALE;
		}
	};

	private LocaleUtility() {

	}

	/**
	 * Set the locale to ThreadLocal
	 *
	 * @param locale
	 */
	public static void setLocale(Locale locale) {
		threadLocal.set(locale);
	}

	/**
	 * get the locale from ThreadLocal
	 *
	 * @param locale
	 */
	public static Locale getLocale() {
		Locale locale = threadLocal.get();
		if (locale == null) {
			return DEFAULT_LOCALE;
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
		if (!StringUtils.isEmpty(locale)) {
			for (String ls : LocaleUtility.DEFAULT_LOCALES) {
				if (locale.equals(ls)) {
					isDefault = true;
					break;
				}
			}
		}
		return isDefault;
	}

	
	private static Locale pickupLocale(List<Locale> locales,
            Locale preferredLocale) {
	    Locale pLocale = null;
        Locale sLocale = preferredLocale;
        String key = preferredLocale.toLanguageTag();
        if (LocaleUtility.LOCALE_MAP.containsKey(key)) {
            String value = LocaleUtility.LOCALE_MAP.getString(key);
            sLocale = Locale.forLanguageTag(value);
        }
        for (Locale locale : locales) {
            String localeTag = locale.toLanguageTag();
            if (sLocale.toLanguageTag().equalsIgnoreCase(localeTag)) {
                pLocale = locale;
                break;
            } else if (locale.getLanguage().equalsIgnoreCase(
                    sLocale.getLanguage())) {
                pLocale = new Locale(locale.getLanguage());
                String script = locale.getScript();
                if (!"".equals(script)
                        && script.equalsIgnoreCase(sLocale.getScript())) {
                    pLocale = Locale.forLanguageTag(locale.getLanguage() + "-"
                            + script);
                }
                String country = locale.getCountry();
                if (!"".equals(country)
                        && country.equalsIgnoreCase(sLocale.getCountry())) {
                    pLocale = Locale.forLanguageTag(pLocale.toLanguageTag()
                            + "-" + country);
                    String variant = locale.getVariant();
                    if (!"".equals(variant)
                            && variant.equalsIgnoreCase(sLocale.getVariant())) {
                        pLocale = Locale.forLanguageTag(pLocale.toLanguageTag()
                                + "-" + variant);
                    }
                }
            }
        }
        return pLocale;
	}
	
	
	/**
	 * pickup the matched locale with a map in properties file, if no locale return default locale
	 * 
	 * @param locales
	 * @param preferredLocale
	 * @return
	 */
	public static Locale pickupLocaleFromList(List<Locale> locales,
			Locale preferredLocale) {
	    Locale pLocale = pickupLocale(locales,preferredLocale);
		return pLocale == null ? DEFAULT_LOCALE : pLocale;
	}
	
	/**
     * pickup the matched locale with a map in properties file, if no locale return input locale
     * 
     * @param locales
     * @param preferredLocale
     * @return
     */
    public static Locale pickupLocaleFromListNoDefault(List<Locale> locales,
            Locale preferredLocale) {
        Locale pLocale = pickupLocale(locales,preferredLocale);
        return pLocale == null ? preferredLocale : pLocale;
    }
}
