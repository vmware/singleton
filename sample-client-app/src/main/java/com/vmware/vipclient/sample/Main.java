/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vipclient.sample;

import java.util.Date;
import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class Main {
	
	public static void main(String[] args) {
		Locale thislocale = Locale.ENGLISH;
		if (args.length > 0) {
			// set locale
			System.out.println("Set locale to: " + args[0]);
			thislocale = Locale.forLanguageTag(args[0]);
		}
		LocaleUtility.setLocale(thislocale);

		// Initialize
		VIPCfg cfg = VIPCfg.getInstance();
		try {
			//cfg.initialize("sampleconfig");
			cfg.loadConfig("sampleconfig");
		} catch (VIPClientInitException e) {
			System.out.println(e.getMessage());
		}
		cfg.initializeVIPService();
		cfg.createTranslationCache(MessageCache.class);
		cfg.createFormattingCache(FormattingCache.class);
		I18nFactory.getInstance(cfg);

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
		boolean bReady = Translation.isTranslationReady("default", key, thislocale);
		System.out.println(bReady);

		// Number format
		double num = 201703.5416926;
		System.out.println(">>>>>> Number format: " + num);
		String resultOfNumber = Format.formatNumber(num);
		System.out.println(resultOfNumber);

		// Percent format
		double numOfPercent = 12.3456;
		System.out.println(">>>>>> Percent format: " + numOfPercent);
		String resultOfPercent = Format.formatPercent(numOfPercent);
		System.out.println(resultOfPercent);

		// Currency format
		double numOfCurrency = 201703.5416926;
		System.out.println(">>>>>> Currency format: " + numOfCurrency);
		String resultOfCurrency = Format.formatCurrency(numOfCurrency);
		System.out.println(resultOfCurrency);

		String code = "EUR";
		System.out.println(">>>>>> Currency format with code \"" + code + "\": " + numOfCurrency);
		String resultOfCurrency2 = Format.formatCurrency(numOfCurrency, code);
		System.out.println(resultOfCurrency2);

		// Date format
		Date date = new Date(1511156364801l);
		String pattern = "long";
		String tz = "GMT-8";
		System.out.println(">>>>>> Date format with pattern \"" + pattern + "\": " + date);
		System.out.println(Format.formatDate(date, pattern));
		System.out.println(">>>>>> Date format with pattern \"" + pattern + "\" and timezone \"" + tz + "\": " + date);
		System.out.println(Format.formatDate(date, pattern, tz));
	}
}
