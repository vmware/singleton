/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class Translation {

	public static void demo(Locale locale) {
		// Get translation
		String key = "global_text_username";
		String source = "User name";

		System.out.println(">>>>>> Get translation by key: \"" + key + "\" and source: \"" + source + "\"");
		String trans1 = Translation.getTranslation(key, source);
		System.out.println(trans1);

		System.out.println(">>>>>> Get translation by key: \"" + key + "\"");
		String trans2 = Translation.getTranslation2(key);
		System.out.println(trans2);
		
		System.out.println(">>>>>> Check translation status of key: \"" + key + "\"");
		boolean bReady = Translation.isTranslationReady("default", key, locale);
		System.out.println(bReady);
	}
	
	/**
	 * get one string's translation
	 * 
	 * @param key
	 * @param source
	 * @param args
	 * @return
	 * @deprecated
	 */
	public static String getTranslation(String key, String source,
			Object... args) {
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		String component = "default";
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		String translation = t.getString(locale, component, key, source, "",
				args);
		return translation;
	}

	/**
	 * get one component's translations
	 * 
	 * @param locale
	 * @param component
	 * @return
	 * @deprecated
	 */
	public static Map getTranslations(Locale locale, String component) {
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		return t.getStrings(locale, component);
	}

	/**
	 * get one string's translation. If client can't get the translation from 
	 * VIP server, client will return English string from local bundle file.
	 * 
	 * @param key
	 * @param args
	 * @return
	 * @deprecated
	 */
	public static String getTranslation2(String key, Object... args) {
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		String component = "default";
		String bundle = "messages";
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		return t.getString2(component, bundle, locale, key, args);
	}
	
	
	/**
	 * check if one component's translations are available
	 * 
	 * @param component
	 * @param locale
	 * @return
	 */
	public static boolean isTranslationReady(String component, Locale locale) {
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		return t.isAvailable(component, locale);
	}

	/**
	 * check if one string's translation is available
	 * 
	 * @param component
	 * @param key
	 * @param locale
	 * @return
	 */
	public static boolean isTranslationReady(String component, String key,
			Locale locale) {
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		return t.isAvailable(component, key, locale);
	}

	/**
	 * post multiple strings to remote VIP service
	 * 
	 * @param locale
	 * @param component
	 * @param sources
	 * @return
	 */
	public static boolean postStrings(Locale locale, String component,
			List<JSONObject> sources) {
		TranslationMessage t = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		return t.postStrings(locale, component, sources);
	}
}
