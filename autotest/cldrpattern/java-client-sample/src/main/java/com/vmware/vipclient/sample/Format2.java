/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample;

import java.util.Date;
import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;


public class Format2 {
	
	public void initVIPServer() throws Exception {
		VIPCfg cfg = VIPCfg.getInstance();
		try {
			cfg.initialize("sampleconfig");
		} catch (VIPClientInitException e) {
			System.out.println(e.getMessage());
		}
    	
		cfg.initializeVIPService();
		cfg.createTranslationCache(MessageCache.class);
		cfg.createFormattingCache(FormattingCache.class);
		I18nFactory.getInstance(cfg);
	}
	
	public static String number() {
		double num = 201703.5416926;
		Locale locale = Locale.ENGLISH;
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatNumber(num, locale);		
	}
	
	public static String numberofPercent() {
		double num = 0.23456;
		Locale locale = Locale.JAPANESE;
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatPercent(num, locale);
	}
	
	public static String numberofCurrency() {
		double num = 201703.5416926;;
		Locale locale = Locale.SIMPLIFIED_CHINESE;
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatCurrency(num, locale);
	}
	
	public static String dateTime() {
		Date date = new Date(1511156364801l);
		String pattern = "long";
		Locale locale = Locale.SIMPLIFIED_CHINESE;
		DateFormatting nf = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);
		return nf.formatDate(date, pattern, locale);
	}
	
	public static String plural() {
		Date date = new Date(1511156364801l);
		String pattern = "long";
		Locale locale = Locale.FRENCH;
		TranslationMessage nf = (TranslationMessage)I18nFactory.getInstance().getMessageInstance(TranslationMessage.class);
		
		String component = "default";
		String pluralKey = "plural.files";
		Object[] testArg1 = {100000, "MyDisk"};
		return nf.getMessage(locale, component, pluralKey, testArg1);

	}
	
}
