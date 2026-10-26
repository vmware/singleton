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
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class Main {
	
	public static void main(String[] args) {
		Locale sharedLibLocale = Locale.forLanguageTag("es");
		if (args.length > 0) {
			// set locale
			System.out.println("Set locale to: " + args[0]);
			sharedLibLocale = Locale.forLanguageTag(args[0]);
		}

		// Initialize root application's VIP configuration
		VIPCfg cfg = VIPCfg.getInstance();
		try {
			cfg.initialize("sampleconfig");
		} catch (VIPClientInitException e) {
			System.out.println(e.getMessage());
		}
		cfg.initializeVIPService();
		cfg.createTranslationCache(MessageCache.class);
		cfg.createFormattingCache(FormattingCache.class);

		TranslationMessage tm = (TranslationMessage) I18nFactory.getInstance(cfg).getMessageInstance(TranslationMessage.class);
		String rootMsg = tm.getMessage(Locale.forLanguageTag("fr"), "default", "global_text_username");
		System.out.println("Root application's message  " + rootMsg);

		SharedLibDemo.demoSharedLib(sharedLibLocale, tm);

	}
}
