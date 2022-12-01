/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vipclient.sample;

import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class Main {
	
	public static void main(String[] args) {
		Locale locale = Locale.ENGLISH;
		if (args.length > 0) {
			// set locale
			System.out.println("Set locale to: " + args[0]);
			locale = Locale.forLanguageTag(args[0]);
		}
		LocaleUtility.setLocale(locale);

		// Initialize
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

//		TranslationDemo.demo(locale);
//		Translation.demo(locale);
		
		LocaleUtility.setDefaultLocale(Locale.ENGLISH);
		Format.demo(locale);
		
	}
}
