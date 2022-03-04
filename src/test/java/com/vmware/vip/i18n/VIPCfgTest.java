/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class VIPCfgTest extends BaseTestClass {
    TranslationMessage translation;
    MessagesDTO        dto;

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        if (gc.getCacheManager() != null)
            gc.getCacheManager().clearCache();
        gc.createTranslationCache(MessageCache.class);
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        dto = new MessagesDTO();
    }

    @Test
    public void testPseudo() {
        String component = "JAVA";
        Locale locale1 = new Locale("en", "US");
        String key = "LeadTest";
        String source = "source";
        VIPCfg.getInstance().setPseudo(true);
        String message1 = translation.getString(locale1, component, key, source, "It's a comment");
        Assert.assertThat(message1, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("@@source@@"));

        Locale locale2 = new Locale("zh", "CN");
        String message2 = translation.getString(locale2, component, key, source, "It's a comment");
        Assert.assertTrue(message2.indexOf("@[{0}] Test alert@") > -1 || message2.indexOf("#@[{0}] Test alert#@") > -1);

        Locale locale3 = new Locale("de", "DE");
        VIPCfg.getInstance().setPseudo(false);
        String message3 = translation.getString(locale3, component, key, "[{0}] Test alert", "It's a comment");
        Assert.assertThat(message3, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("[{0}] Testwarnung"));
    }

    @Test
    public void testMT() {
        String component = "JAVA";
        Locale locale1 = new Locale("en", "US");
        String key = "key.mt";
        String source = "It's a testing source";
        VIPCfg.getInstance().setMachineTranslation(true);
        String message1 = translation.getString(locale1, component, key, source, "It's a comment");
        Assert.assertThat(message1, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(source));

        Locale locale2 = new Locale("fr", "FR");
        String message2 = translation.getString(locale2, component, key, source, "It's a comment");
        // Assert.assertThat(message2, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("C'est une source de
        // test"));

        Locale locale3 = new Locale("de", "DE");
        VIPCfg.getInstance().setMachineTranslation(false);
        String message3 = translation.getString(locale3, component, key, "Test source", "It's a comment");
        // Assert.assertThat(message3, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Test source"));
    }

    //@Test
    public void testMT2() {
        String component = "default";
        Locale locale1 = new Locale("en", "US");
        String key = "key.mt";
        String source = "It's a testing source";
        String url = VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL();
        VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL("https://1.2.3.4:8090");
        VIPCfg.getInstance().setMachineTranslation(true);
        String message1 = translation.getString(locale1, component, key, source, "It's a comment");
        Assert.assertThat(message1, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(source));

        Locale locale2 = new Locale("fr", "FR");
        String message2 = translation.getString(locale2, component, key, source, "It's a comment");
        Assert.assertThat(message2, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(source));
        VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL(url);

    }

    @Test
    public void testMT3() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.setProductName("Sample");
        gc.setVersion("1.0.0");
        String component = "default";
        Locale locale1 = new Locale("ru");
        VIPCfg.getInstance().setMachineTranslation(true);
        Map message1 = translation.getMessages(locale1, component);
        // Assert.assertTrue(message1.size()>0);
        // String mt = (String)message1.get("global_text_username");
        // Assert.assertThat(mt, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Имя пользователя"));
        String key = "key.mt";
        String source = "It's a testing source";
        String message2 = translation.getString(locale1, component, key, source, "It's a comment");
        // Assert.assertThat(message2, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Это тестовый
        // источник"));
    }
    
    @Test
    @Deprecated
    public void testMT3_() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.setProductName("Sample");
        gc.setVersion("1.0.0");
        String component = "default";
        Locale locale1 = new Locale("ru");
        VIPCfg.getInstance().setMachineTranslation(true);
        Map message1 = translation.getStrings(locale1, component);
        // Assert.assertTrue(message1.size()>0);
        // String mt = (String)message1.get("global_text_username");
        // Assert.assertThat(mt, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Имя пользователя"));
        String key = "key.mt";
        String source = "It's a testing source";
        String message2 = translation.getString(locale1, component, key, source, "It's a comment");
        // Assert.assertThat(message2, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Это тестовый
        // источник"));
    }

    @Test
    @Deprecated
    public void testCollectSource() {
        vipCfg.setPseudo(false);

        String component = "JAVA";
        Locale locale1 = new Locale("en", "US");
        String key = "key.mt";
        String source = "It's a testing source";
        VIPCfg.getInstance().setCollectSource(true);
        String message1 = translation.getString(locale1, component, key, source, "It's a comment");
        Assert.assertThat(message1, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(source));

        Locale locale2 = new Locale("de", "DE");
        String message2 = translation.getString(locale2, component, "LeadTest", source, "It's a comment");
        Assert.assertThat(message2, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase(source));
        String message22 = translation.getString(locale2, component, "LeadTest", "[{0}] Test alert", "It's a comment");
        Assert.assertThat(message22, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("[{0}] Testwarnung"));

        Locale locale3 = new Locale("de", "DE");
        VIPCfg.getInstance().setCollectSource(false);
        String message3 = translation.getString(locale3, component, key, "Test source", "It's a comment");
        Assert.assertThat(message3, org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase("Test source"));
    }

}
