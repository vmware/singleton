/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VIPCfgFactory {
    /**
     * A lazy-loaded singleton VIPCfg using the "initialization-on-demand holder" design pattern
     */
    private static class VIPCfgHolder {
        static final VIPCfg mainCfg = new VIPCfg();
    }

    public static VIPCfg getCfg() {
        return VIPCfgHolder.mainCfg;
    }

    /**
     * A lazy-loaded singleton Map<String, VIPCfg> configs using the "initialization-on-demand holder" design pattern
     */
    private static class ConfigsHolder {
        static final Map<String, VIPCfg> configs = new ConcurrentHashMap<>();
    }

    public static VIPCfg getCfg(String productName) {
        if (!contains(productName)) {
            synchronized (ConfigsHolder.configs) {
                if (!contains(productName)) {
                    VIPCfg cfg = new VIPCfg();
                    cfg.setProductName(productName);
                }
            }
        }
        return ConfigsHolder.configs.get(productName);
    }

    protected static void addCfg(VIPCfg cfg) {
        ConfigsHolder.configs.put(cfg.getProductName(), cfg);
    }

    private static boolean contains(String productName) {
        return ConfigsHolder.configs.containsKey(productName);
    }

}
