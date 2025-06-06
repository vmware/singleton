/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.*;
import com.vmware.vipclient.i18n.base.instances.LocaleMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
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
                Locale.forLanguageTag("fr-CA"),
                Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
                Locale.forLanguageTag("zh"),
                Locale.forLanguageTag("zh-Hans"),
                Locale.forLanguageTag("zh-Hant")

        };
        Locale[] testLocales = { Locale.forLanguageTag("de"),
                Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
                Locale.forLanguageTag("fr-CA"), Locale.forLanguageTag("fr-FR"),
                Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
                Locale.forLanguageTag("zh"),
                Locale.forLanguageTag("zh-CN"),
                Locale.forLanguageTag("zh-TW"),
                Locale.forLanguageTag("zh-HANS-CN"),
                Locale.forLanguageTag("zh-HANT-TW"),
                Locale.forLanguageTag("zh-HANS"),
                Locale.forLanguageTag("zh-HANT") };

        String[] expectedLocales = { "de", "es", "fr", "fr-CA", "fr", "ja", "ko", "zh",
                "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant" };

        for (int i = 0; i < testLocales.length; i++) {
            String matchedLanguageTag = LocaleUtility.pickupLocaleFromList(
                    new HashSet<>(Arrays.asList(supportedLocales)), testLocales[i])
                    .toLanguageTag();

            logger.debug(matchedLanguageTag + "-----" + expectedLocales[i]);
            Assert.assertEquals(expectedLocales[i], matchedLanguageTag);
        }
    }

    @Test
    public void testPickupLocaleFromListNotFound() {
        Locale[] supportedLocales = { Locale.forLanguageTag("de"),
                Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
                Locale.forLanguageTag("fr-CA"),
                Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
                Locale.forLanguageTag("zh-Hans"),
                Locale.forLanguageTag("zh-Hant")

        };
        Assert.assertNull(LocaleUtility.pickupLocaleFromList(new HashSet<>(Arrays.asList(supportedLocales)), Locale.forLanguageTag("fil")));
    }

    /**
     *  For any Chinese locale (zh-*) that is not supported,
     *  return null so that fallback locale will be used even if "zh" is supported.
     *  For any non-Chinese locale, return the best match (e.g. 'de' if 'de-DE' is not supported).
     */
    @Test
    public void testPickupLocaleFromListZh() {
        Locale[] supportedLocales = {
                Locale.forLanguageTag("zh"),
                Locale.forLanguageTag("zh-Hans")

        };
        Assert.assertNull(LocaleUtility.pickupLocaleFromList(new HashSet<>(Arrays.asList(supportedLocales)), Locale.forLanguageTag("zh-HK")));
        Assert.assertEquals("zh", LocaleUtility.pickupLocaleFromList(new HashSet<>(Arrays.asList(supportedLocales)),
                Locale.forLanguageTag("zh")).toLanguageTag());
        Assert.assertEquals("zh-Hans", LocaleUtility.pickupLocaleFromList(new HashSet<>(Arrays.asList(supportedLocales)),
                Locale.forLanguageTag("zh-CN")).toLanguageTag());
        Assert.assertNull(LocaleUtility.pickupLocaleFromList(new HashSet<>(Arrays.asList(supportedLocales)),
                Locale.forLanguageTag("zh-TW")));
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
    public void testGetRegionList() throws JSONException {
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
    public void testGetDisplayNamesByLanguage() throws JSONException {
        Map<String, String> enResp = localeI18n.getDisplayLanguagesList("en");
        Assert.assertNotNull(enResp);
        Map<String, String> zhResp = localeI18n.getDisplayLanguagesList("zh_Hans");
        Assert.assertNotNull(zhResp);
        localeI18n.getDisplayLanguagesList("zh_Hans");// get data from cache
        //test invalid locale, fallback to default locale
        Map<String, String> aaResp = localeI18n.getDisplayLanguagesList("aa");
        Assert.assertNotNull(aaResp);
    }

//    @Test
//    public void testThreadLocale() throws InterruptedException {
//        Locale localeZhCN = new Locale("zh", "CN");
//        Locale localeZhTW = new Locale("zh", "TW");
//        Locale localeKoKR = new Locale("ko", "KR");
//        Locale localeDeDE = new Locale("de", "DE");
//
//        LocaleUtility.setLocale(LocaleUtility.getDefaultLocale());
//        // cp. check the default locale isn't zh-Hans.
//        Assert.assertNotEquals("Error! Default locale is: " + locale, localeZhCN, LocaleUtility.getLocale());
//
//        // Set locale in current thread
//        LocaleUtility.setLocale(localeZhCN);
//
//        // cp1. check the locale is saved successfully.
//        Assert.assertEquals("Error! Locale isn't set successfully.", localeZhCN, LocaleUtility.getLocale());
//
//        // Create a new sub-thread, and read its initial locale
//        t = 11;
//        new Thread(subThreadOne).start();
//
//        // cp2. check the locale of sub-thread is same as parent thread
//        lock.lock();
//        try {
//            while (t != 0)
//                con.await();
//            Assert.assertEquals("Didn't inherit successfully", LocaleUtility.getLocale(), locale);
//
//            // Change locale in sub-thread,
//            locale = localeZhTW;
//            t = 12;
//            con.signal();
//        } finally {
//            lock.unlock();
//        }
//
//        // cp3. check parent locale doesn't change
//        lock.lock();
//        try {
//            while (t != 0)
//                con.await();
//            Assert.assertEquals("Child interfere parent!", localeZhCN, LocaleUtility.getLocale());
//
//            // Change locale in parent thread,
//            LocaleUtility.setLocale(localeKoKR);
//            t = 11;
//            con.signal();
//        } finally {
//            lock.unlock();
//        }
//
//        // cp4. check sub-thread locale doesn't change
//        lock.lock();
//        try {
//            while (t != 0)
//                con.await();
//            Assert.assertEquals("Parent interfere child!", localeZhTW, locale);
//        } finally {
//            lock.unlock();
//        }
//
//        // Launch another sub-thread, change locale in this sub-thread
//        t = 22;
//        locale = localeDeDE;
//        new Thread(subThreadTwo).start();
//
//        // cp5. Check first sub-thread locale doesn't change
//        lock.lock();
//        try {
//            while (t != 0)
//                con.await();
//            Assert.assertEquals("Child interfere child!", localeZhTW, locale);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    private Locale    locale       = null;
//    private Lock      lock         = new ReentrantLock(true);
//    private Condition con          = lock.newCondition();
//    private int       t            = 1;
//
//    private Runnable  subThreadOne = () -> {
//                                       while (true) {
//                                           lock.lock();
//                                           try {
//                                               while (!(t >= 10 && t < 20))
//                                                   con.await();
//                                               if (t == 11) {
//                                                   locale = LocaleUtility.getLocale();
//                                               } else if (t == 12) {
//                                                   LocaleUtility.setLocale(locale);
//                                               }
//                                               t = 0;
//                                               con.signal();
//                                           } catch (InterruptedException e) {
//                                        	   Thread.currentThread().interrupt();
//                                           } finally {
//                                               lock.unlock();
//                                           }
//                                       }
//
//                                   };
//
//    private Runnable  subThreadTwo = () -> {
//                                       lock.lock();
//                                       try {
//                                           while (!(t >= 20 && t < 30))
//                                               con.await();
//                                           if (t == 21) {
//                                               locale = LocaleUtility.getLocale();
//                                           } else if (t == 22) {
//                                               LocaleUtility.setLocale(locale);
//                                           }
//                                           t = 11;
//                                           con.signalAll();
//                                       } catch (InterruptedException e) {
//                                    	   Thread.currentThread().interrupt();
//                                       } finally {
//                                           lock.unlock();
//                                       }
//                                   };
}
