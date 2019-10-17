/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SharedComponentTest extends BaseTestClass {
    TranslationMessage translation;
    MessagesDTO dto;
    TranslationMessage translation1;
    MessagesDTO dto1;


    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.initialize("vipconfig");
        gc.initializeVIPService();
        if (gc.getCacheManager() != null) gc.getCacheManager().clearCache();
        gc.createTranslationCache(MessageCache.class);
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        dto = new MessagesDTO();

        VIPCfg subCfg = VIPCfg.getSubInstance("sub-key");
        subCfg.initialize("vipconfig4sharedcomponent");
        subCfg.initializeVIPService();
        subCfg.createTranslationCache(MessageCache.class);
        subCfg.createFormattingCache(FormattingCache.class);
        translation1 = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class, subCfg);
        translation1.setCfg(subCfg);
        dto1 = new MessagesDTO();
    }

    @Test
    public void testGetPseudoTranslation_Collected() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String source = "Host";
        String expected = "#@Host#@";

        String pseudoTrans1 = translation.getString(zhLocale, comp, key, source, "");

        Locale zhLocale2 = new Locale("de", "");
        String comp2 = "JSP";
        String key2 = "table.head";
        String source2 = "VM";
        String pseudoTrans2 = translation1.getString(zhLocale2, comp2, key2, source2, "");

        logger.debug("pseudoTrans1: " + pseudoTrans1);

    }
}
