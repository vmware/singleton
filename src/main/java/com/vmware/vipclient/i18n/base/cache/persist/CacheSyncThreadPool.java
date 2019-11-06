/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache.persist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vmware.vipclient.i18n.VIPCfg;

public class CacheSyncThreadPool implements Runnable {
    private int maxThreads     = 30;
    private int minThreads     = 10;
    private int defaultThreads = 5;

    public CacheSnapshot getCacheSnapshot() {
        return VIPCfg.getInstance().getCacheManager().getCacheSnapshot();
    }

    @Override
    public void run() {
        CacheSnapshot cs = VIPCfg.getInstance().getCacheManager()
                .getCacheSnapshot();
        Set<String> s = cs.getComponents();
        List<CacheSyncThread> list = new ArrayList<CacheSyncThread>();
        int curentThreads = defaultThreads;
        if (s.size() > 1000) {
            curentThreads = maxThreads;
        } else if (s.size() > 100) {
            curentThreads = minThreads;
        }
        Loader loader = VIPCfg.getInstance().getCacheManager()
                .getLoaderInstance(DiskCacheLoader.class);
        for (String key : s) {
            int c = 0;
            Set<String> components = new HashSet<String>();
            for (int j = 0; j < curentThreads; j++) {
                components.add(key);
            }
            CacheSyncThread t = new CacheSyncThread(components,
                    cs.getLocales(), loader);
            this.runningThreads.add(t);
            t.start();
        }
        while (!isComplete()) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        loader.refreshCacheSnapshot(this.getCacheSnapshot());
    }

    private List<CacheSyncThread> runningThreads = new ArrayList<CacheSyncThread>();

    public boolean isComplete() {
        boolean f = false;
        for (CacheSyncThread t : runningThreads) {
            if (t.isAlive() || t.isInterrupted()) {
                break;
            }
        }
        return f;
    }

}
