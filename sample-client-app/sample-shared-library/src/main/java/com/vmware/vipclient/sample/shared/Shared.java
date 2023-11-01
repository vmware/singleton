/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample.shared;

import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class Shared {
    TranslationMessage tm = null;
    Locale locale = null;
    static String component = "shared";

    public Shared(Locale locale) {
        this.locale = locale;
        if (this.tm == null) {
            VIPCfg cfg = VIPCfg.getInstance();
            try {
                cfg.initialize("sharedlib");
            } catch (VIPClientInitException e) {
                System.out.println(e.getMessage());
            }
            cfg.initializeVIPService();
            cfg.createTranslationCache(MessageCache.class);
            cfg.createFormattingCache(FormattingCache.class);

            tm = (TranslationMessage) I18nFactory.getInstance(cfg).getMessageInstance(TranslationMessage.class);
        }
    }
    public Shared(TranslationMessage tm , Locale locale) {
        this.tm = tm;
        this.locale = locale;
    }

    public String someMethod () {
        System.out.println(">>>>>> Shared.someMethod");
        String msg = tm.getMessage(locale, component, "shared.library.key1");
        return msg;
    }

    public static void main(String args[]) {
        Locale locale = Locale.forLanguageTag("es");
        Shared shared = new Shared(locale);

        shared.someMethod();
    }

}
