/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;

public class Task {
    private static Logger  logger              = LoggerFactory.getLogger(Task.class);

    private static boolean connected           = true;
    private static int     min                 = 60 * 1000;
    private static int     intevalPing         = 10 * min;                           // 10 minutes
    private static int     defaultIntevalCache = 24 * 60 * min;                      // 24 hours

    private Task() {

    }

    public static void startTaskOfCacheClean(VIPCfg gc, int interalCleanCache) {
        TimerTask taskPing = new TimerTask() {
            @Override
            public void run() {
                if (!connected && null != gc.getVipServer()
                        && HttpRequester.ping(gc.getVipServer())) {
                    connected = true;
                    logger.info(
                            "Ping the host[" + gc.getVipServer()
                                    + "] is passed and reset connection.");
                }
            }
        };
        TimerTask cleanCacheTask = new TimerTask() {
            @Override
            public void run() {
                Cache translationCache = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
                if (translationCache == null) {
                    return;
                }
                Set<String> set = translationCache.keySet();
                // ping VIP service before clean the cache
                if ((!set.isEmpty()) && HttpRequester.ping(gc.getVipServer())) {
                    translationCache.clear();
                    logger.info("Cache was clean up.");
                    gc.initializeMessageCache();
                } else {
                    logger.info("There's no cache or VIP service is not connected, unable to clean the cache.");
                }
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(taskPing, intevalPing, intevalPing);
        if (interalCleanCache > 0) {
            timer.scheduleAtFixedRate(cleanCacheTask, (long) interalCleanCache * min,
                    (long) interalCleanCache * min);
        } else {
            timer.scheduleAtFixedRate(cleanCacheTask, defaultIntevalCache,
                    defaultIntevalCache);
        }
    }
}
