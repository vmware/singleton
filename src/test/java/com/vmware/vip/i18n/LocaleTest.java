/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.LocaleMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LocaleTest extends BaseTestClass {
    LocaleMessage localeI18n;
    VIPCfg gc;
    
    @Before
    public void init() {
        gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        localeI18n = (LocaleMessage) i18n.getMessageInstance(LocaleMessage.class);
    }

    @Test
    public void testPickupLocaleFromList() {
        Locale[] supportedLocales = { Locale.forLanguageTag("de"),
                Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
                Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
                Locale.forLanguageTag("zh-Hans"),
                Locale.forLanguageTag("zh-Hant")

        };
        Locale[] testLocales = { Locale.forLanguageTag("de"),
                Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
                Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
                Locale.forLanguageTag("zh"), Locale.forLanguageTag("zh-CN"),
                Locale.forLanguageTag("zh-TW"),
                Locale.forLanguageTag("zh-HANS-CN"),
                Locale.forLanguageTag("zh-HANT-TW"),
                Locale.forLanguageTag("zh-HANS"),
                Locale.forLanguageTag("zh-HANT") };

        String[] expectedLocales = { "de", "es", "fr", "ja", "ko", "zh",
                "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant" };

        for (int i = 0; i < testLocales.length; i++) {
            String matchedLanguageTag = LocaleUtility.pickupLocaleFromList(
                    Arrays.asList(supportedLocales), testLocales[i])
                    .toLanguageTag();

            logger.debug(matchedLanguageTag + "-----" + expectedLocales[i]);
            Assert.assertEquals(expectedLocales[i], matchedLanguageTag);
        }
    }

    @Test
    public void normalizeToLanguageTag() {
        String[] testLocaleStrs = { "de", "es", "fr", "ja", "ko", "en-US", "zh-CN", "zh-TW",
                "zh-Hans", "zh-Hant", "zh__#Hans", "zh__#Hant",
                "zh-Hans-CN", "zh-Hant-TW", "zh_CN_#Hans", "zh_TW_#Hant" };
        String[] expectedLocales = { "de", "es", "fr", "ja", "ko", "en-US", "zh-CN", "zh-TW",
                "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant",
                "zh-Hans-CN", "zh-Hant-TW", "zh-Hans-CN", "zh-Hant-TW" };
        for (int i = 0; i < testLocaleStrs.length; i++) {
            String normalizedLanguageTag = LocaleUtility.normalizeToLanguageTag(testLocaleStrs[i]);
            Assert.assertEquals(expectedLocales[i], normalizedLanguageTag);
        }
    }

    @Test
    public void testGetRegionList() throws ParseException {
        List<String> list = new ArrayList<String>();
        list.add("en-US");
        list.add("zh_CN");
        //list.add("ja");
        list.add("de-DE");
        list.add("aa");
        Map<String, Map<String, String>> result = localeI18n.getRegionList(list);
        Assert.assertNotNull(result);
        localeI18n.getRegionList(list);// get data from cache
    }

    @Test
    public void testGetDisplayNamesByLanguage() throws ParseException {
        Map<String, String> enResp = localeI18n.getDisplayLanguagesList("en");
        Assert.assertNotNull(enResp);
        Map<String, String> zhResp = localeI18n.getDisplayLanguagesList("zh_Hans");
        Assert.assertNotNull(zhResp);
        localeI18n.getDisplayLanguagesList("zh_Hans");// get data from cache
        //test invalid locale, fallback to default locale
        Map<String, String> aaResp = localeI18n.getDisplayLanguagesList("aa");
        Assert.assertNotNull(aaResp);
    }
    
    @Test
    public void testGetSupportedLocalesOfflineBundles() throws ParseException {
    	//Enable offline mode
    	String offlineResourcesBaseUrlOrig = gc.getOfflineResourcesBaseUrl();
    	gc.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = gc.getMsgOriginsQueue();
    	gc.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
    	
    	// There is no service response mock for "fil" display language, so service request will fail.
    	// List of supported locales shall be dertermined from available offline bundle files.
        Map<String, String> resp = localeI18n.getDisplayLanguagesList("fil");
        Assert.assertTrue(resp.containsKey("fil"));
        
        CacheService cs = new CacheService(new MessagesDTO());
    	List<Locale> supportedLocales = cs.getSupportedLocalesFromCache();
        Assert.assertNotNull(supportedLocales);
        Assert.assertTrue(supportedLocales.contains(Locale.forLanguageTag("fil")));
        Assert.assertEquals("Filipino", supportedLocales.get(
        		supportedLocales.indexOf(Locale.forLanguageTag("fil"))).getDisplayName());
        localeI18n.getDisplayLanguagesList("invalid_locale");// get data from cache
        
        // Disable offline mode off for next tests.
        gc.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	gc.setMsgOriginsQueue(msgOriginsQueueOrig);
    }

    @Test
    public void testThreadLocale() throws InterruptedException {
        Locale localeZhCN = new Locale("zh", "CN");
        Locale localeZhTW = new Locale("zh", "TW");
        Locale localeKoKR = new Locale("ko", "KR");
        Locale localeDeDE = new Locale("de", "DE");

        LocaleUtility.setLocale(LocaleUtility.getDefaultLocale());
        // cp. check the default locale isn't zh-Hans.
        Assert.assertNotEquals("Error! Default locale is: " + locale, localeZhCN, LocaleUtility.getLocale());

        // Set locale in current thread
        LocaleUtility.setLocale(localeZhCN);

        // cp1. check the locale is saved successfully.
        Assert.assertEquals("Error! Locale isn't set successfully.", localeZhCN, LocaleUtility.getLocale());

        // Create a new sub-thread, and read its initial locale
        t = 11;
        new Thread(subThreadOne).start();

        // cp2. check the locale of sub-thread is same as parent thread
        lock.lock();
        try {
            while (t != 0)
                con.await();
            Assert.assertEquals("Didn't inherit successfully", LocaleUtility.getLocale(), locale);

            // Change locale in sub-thread,
            locale = localeZhTW;
            t = 12;
            con.signal();
        } finally {
            lock.unlock();
        }

        // cp3. check parent locale doesn't change
        lock.lock();
        try {
            while (t != 0)
                con.await();
            Assert.assertEquals("Child interfere parent!", localeZhCN, LocaleUtility.getLocale());

            // Change locale in parent thread,
            LocaleUtility.setLocale(localeKoKR);
            t = 11;
            con.signal();
        } finally {
            lock.unlock();
        }

        // cp4. check sub-thread locale doesn't change
        lock.lock();
        try {
            while (t != 0)
                con.await();
            Assert.assertEquals("Parent interfere child!", localeZhTW, locale);
        } finally {
            lock.unlock();
        }

        // Launch another sub-thread, change locale in this sub-thread
        t = 22;
        locale = localeDeDE;
        new Thread(subThreadTwo).start();

        // cp5. Check first sub-thread locale doesn't change
        lock.lock();
        try {
            while (t != 0)
                con.await();
            Assert.assertEquals("Child interfere child!", localeZhTW, locale);
        } finally {
            lock.unlock();
        }
    }

    private Locale    locale       = null;
    private Lock      lock         = new ReentrantLock(true);
    private Condition con          = lock.newCondition();
    private int       t            = 1;

    private Runnable  subThreadOne = () -> {
                                       while (true) {
                                           lock.lock();
                                           try {
                                               while (!(t >= 10 && t < 20))
                                                   con.await();
                                               if (t == 11) {
                                                   locale = LocaleUtility.getLocale();
                                               } else if (t == 12) {
                                                   LocaleUtility.setLocale(locale);
                                               }
                                               t = 0;
                                               con.signal();
                                           } catch (InterruptedException e) {
                                               e.printStackTrace();
                                           } finally {
                                               lock.unlock();
                                           }
                                       }

                                   };

    private Runnable  subThreadTwo = () -> {
                                       lock.lock();
                                       try {
                                           while (!(t >= 20 && t < 30))
                                               con.await();
                                           if (t == 21) {
                                               locale = LocaleUtility.getLocale();
                                           } else if (t == 22) {
                                               LocaleUtility.setLocale(locale);
                                           }
                                           t = 11;
                                           con.signalAll();
                                       } catch (InterruptedException e) {
                                           e.printStackTrace();
                                       } finally {
                                           lock.unlock();
                                       }
                                   };
}
