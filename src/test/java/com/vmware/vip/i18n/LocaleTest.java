/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.LocaleMessage;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class LocaleTest extends BaseTestClass {
    LocaleMessage localeI18n;

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.initialize("vipconfig");
        gc.initializeVIPService();
        gc.createFormattingCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        localeI18n = (LocaleMessage)i18n.getMessageInstance(LocaleMessage.class);
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
                "zh-Hans", "zh-Hant","zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant"};


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
                "zh-Hans-CN", "zh-Hant-TW","zh_CN_#Hans", "zh_TW_#Hant" };
        String[] expectedLocales = { "de", "es", "fr", "ja", "ko", "en-US", "zh-CN", "zh-TW",
                "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant",
                "zh-Hans-CN", "zh-Hant-TW","zh-Hans-CN", "zh-Hant-TW" };
        for (int i = 0; i < testLocaleStrs.length; i++) {
            String normalizedLanguageTag = LocaleUtility.normalizeToLanguageTag(testLocaleStrs[i]);
            Assert.assertEquals(expectedLocales[i], normalizedLanguageTag);
        }
    }

    @Test
    public void testGetRegionList() throws ParseException{
        List<String> list = new ArrayList<String>();
        list.add("zh_Hant");
        list.add("ja");
        list.add("de");
        Map<String, Map<String, String>> result = localeI18n.getRegionList(list);
        Assert.assertNotNull(result);
        localeI18n.getRegionList(list);//get data from cache
    }

    @Test
    public void testGetDisplayNamesByLanguage() throws ParseException {
        Map<String, String> resp = localeI18n.getDisplayLanguagesList("zh_Hans");
        Assert.assertNotNull(resp);
        localeI18n.getDisplayLanguagesList("zh_Hans");//get data from cache
    }

    @Test
    public void testThreadLocale() throws InterruptedException {
        Locale locale_zhCN = new Locale("zh", "CN");
        Locale locale_zhTW = new Locale("zh", "TW");
        Locale locale_koKR = new Locale("ko", "KR");
        Locale locale_deDE = new Locale("de", "DE");

        LocaleUtility.setLocale(LocaleUtility.defaultLocale);
        // cp. check the default locale isn't zh-Hans.
        Assert.assertNotEquals("Error! Default locale is: " + locale, locale_zhCN, LocaleUtility.getLocale());

        // Set locale in current thread
        LocaleUtility.setLocale(locale_zhCN);

        // cp1. check the locale is saved successfully.
        Assert.assertEquals("Error! Locale isn't set successfully.", locale_zhCN, LocaleUtility.getLocale());

        // Create a new sub-thread, and read its initial locale
        sobj.v = 11;
        tPool.submit(subThreadOne());

        // cp2. check the locale of sub-thread is same as parent thread
        lock.lock();
        try {
            while (sobj.v != 0)
                con.await();
            Assert.assertEquals("Didn't inherit successfully", LocaleUtility.getLocale(), locale);

            //  Change locale in sub-thread,
            locale = locale_zhTW;
            sobj.v = 12;
            con.signal();
        } finally {
            lock.unlock();
        }

        //cp3. check parent locale doesn't change
        lock.lock();
        try {
            while (sobj.v != 0)
                con.await();
            Assert.assertEquals("Child interfere parent!", locale_zhCN, LocaleUtility.getLocale());

            // Change locale in parent thread,
            LocaleUtility.setLocale(locale_koKR);
            sobj.v = 11;
            con.signal();
        } finally {
            lock.unlock();
        }

        //cp4.  check sub-thread locale doesn't change
        lock.lock();
        try {
            while (sobj.v != 0)
                con.await();
            Assert.assertEquals("Parent interfere child!", locale_zhTW, locale);
        } finally {
            lock.unlock();
        }

        // Launch another sub-thread, change locale in this sub-thread
        sobj.v = 22;
        locale = locale_deDE;
        tPool.submit(subThreadTwo());

        // cp5. Check first sub-thread locale doesn't change
        lock.lock();
        try {
            while (sobj.v != 0)
                con.await();
            Assert.assertEquals("Child interfere child!", locale_zhTW, locale);
        } finally {
            lock.unlock();
        }
        tPool.shutdown();
    }

    private static final ExecutorService tPool = Executors.newFixedThreadPool(2);
    private Locale locale = null;
    private Lock lock = new ReentrantLock(true);
    private Condition con = lock.newCondition();
    private final SyncObj sobj = new SyncObj();

    private Runnable subThreadOne() {
        return new Runnable() {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        while (!(sobj.v >= 10 && sobj.v < 20))
                            con.await();
                        if (sobj.v == 11) {
                            locale = LocaleUtility.getLocale();
                        } else if (sobj.v == 12) {
                            LocaleUtility.setLocale(locale);
                        }
                        sobj.v = 0;
                        con.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
    }

    private Runnable subThreadTwo() {
        return new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    while (!(sobj.v >= 20 && sobj.v < 30))
                        con.await();
                    if (sobj.v == 21) {
                        locale = LocaleUtility.getLocale();
                    } else if (sobj.v == 22) {
                        LocaleUtility.setLocale(locale);
                    }
                    sobj.v = 11;
                    con.signalAll();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        };
    }

    class SyncObj {
        int v = 1;
    }

}
