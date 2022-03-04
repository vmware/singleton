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
    public static void demoSharedLib(Locale locale) {
        //VIPCfg cfg = VIPCfg.getInstance();

        String shareLibProdName = "SharedProduct";
        VIPCfg sharedLibCfg = VIPCfg.getSubInstance(shareLibProdName);
        try {
            sharedLibCfg.initialize("sharedlibrary");
        } catch (VIPClientInitException e) {
            e.printStackTrace();
        }
        sharedLibCfg.createTranslationCache(MessageCache.class);
        sharedLibCfg.createFormattingCache(FormattingCache.class);

        TranslationMessage tmSharedLib = (TranslationMessage) I18nFactory.getInstance().getMessageInstance(TranslationMessage.class, sharedLibCfg);

        Shared shared = new Shared(tmSharedLib, locale);
        System.out.println("Shared library's message; fetch triggered from inside the shared library: " + shared.someMethod());

        String sharedMsg = tmSharedLib.getMessage(locale, "shared", "shared.library.key1");
        System.out.println("Shared library's message; fetch is triggered from root application:  " + sharedMsg);

    }
}
