/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.proxyAllTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

public class BaseTestClass {
    protected Logger                logger;
    VIPCfg                          vipCfg            = VIPCfg.getInstance();

    public BaseTestClass() {
        clearDataSource();
    }

    @Rule
    public final TestRule           watchman          = new TestWatcher() {
                                                          @Override
                                                          public Statement apply(Statement base,
                                                                  Description description) {
                                                              logger = LoggerFactory.getLogger(
                                                                      description.getTestClass().getSimpleName());
                                                              return super.apply(base, description);
                                                          }

                                                          @Override
                                                          protected void failed(Throwable e, Description description) {
                                                              logger.error(description.getMethodName() + " Failed.", e);
                                                          }

                                                          @Override
                                                          protected void starting(Description description) {
                                                              logger.info(
                                                                      "Starting test: " + description.getMethodName());
                                                          }
                                                      };

    @ClassRule
    public static WireMockClassRule wireMockClassRule = new WireMockClassRule(
            WireMockConfiguration.options().port(8099).usingFilesUnderClasspath("mockserver"));

    @Rule
    public WireMockClassRule        instanceRule      = wireMockClassRule;

    // @BeforeClass
    public void ProxyToRealServer() {
        stubFor(proxyAllTo("https://").atPriority(1));
        instanceRule.snapshotRecord();
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    static void printObject(Object obj) {
        System.out.println("-----------------------------------Start Printing Object");

        for (Field field : obj.getClass().getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())
                        || Modifier.isPrivate(field.getModifiers())
                        || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                System.out.println("Field '" + field.getName() + "': ");
                System.out.println("\t" + field.get(obj));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Method method : obj.getClass().getDeclaredMethods()) {
            try {
                if (Modifier.isStatic(method.getModifiers())
                        || Modifier.isTransient(method.getModifiers())
                        || Modifier.isPrivate(method.getModifiers())
                        || method.getModifiers() == 0
                        || method.getParameterCount() > 0) {
                    continue;
                }
                System.out.print("method '" + method.getName() + "': ");
                System.out.println(method.invoke(obj));
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    protected void clearCache(String cacheName) {
        Cache cache = TranslationCacheManager.getCache(cacheName);
        if (null != cache) {
            cache.clear();
        }
    }

    protected void clearTranslationCache() {
        clearCache(VIPCfg.CACHE_L3);
    }

    protected void clearDataSource(){
        vipCfg.getMsgOriginsQueue().clear();
    }

}
