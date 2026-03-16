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
import com.vmware.vipclient.sample.shared.Shared;

public class SharedLibDemo {
    private static TranslationMessage tmSharedLib;

    static {
        String shareLibProdName = "SharedProduct";
        VIPCfg sharedLibCfg = VIPCfg.getSubInstance(shareLibProdName);
        try {
            sharedLibCfg.initialize("sharedlibrary");
        } catch (VIPClientInitException e) {
            e.printStackTrace();
        }
        sharedLibCfg.createTranslationCache(MessageCache.class);
        sharedLibCfg.createFormattingCache(FormattingCache.class);
        tmSharedLib = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class, sharedLibCfg);
    }

    public static void demoSharedLib(Locale locale, TranslationMessage tm) {
        useInternalSharedLibConfig(locale);
        overrideSharedLibConfig(locale);
        getSharedLibMsgUsingRootAppConfig(locale, tm);
        getSharedLibMsgUsingSharedLibConfig(locale);
    }

    private static void useInternalSharedLibConfig(Locale locale) {
        Shared shared = new Shared(locale);
        System.out.println("Shared library's message; sharedlibrary.properties is inside the shared lib: " + shared.someMethod());
    }

    private static void overrideSharedLibConfig(Locale locale) {
        Shared shared = new Shared(tmSharedLib, locale);
        System.out.println("Shared library's message; use sharedlibrary.properties in main app: " + shared.someMethod());
    }

    private static void getSharedLibMsgUsingRootAppConfig(Locale locale, TranslationMessage tm) {
        String sharedMsg = tm.getMessage(locale, "shared", "shared.library.key1");
        System.out.println("Shared library's message; root app's config is used:  " + sharedMsg);
    }

    private static void getSharedLibMsgUsingSharedLibConfig(Locale locale) {
        String sharedMsg = tmSharedLib.getMessage(locale, "shared", "shared.library.key1");
        System.out.println("Shared library's message; tmSharedLib.getMessage is called from root application:  " + sharedMsg);
    }
}
