/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.VIPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VIPCfgFactory {
    /**
     * A lazy-loaded singleton VIPCfg using the "initialization-on-demand holder" design pattern
     */
    private static class GlobalCfgHolder {
        static final VIPCfgWrapper globalCfg = new VIPCfgWrapper(new VIPCfg());
    }

    private static Logger logger = LoggerFactory.getLogger(VIPCfgFactory.class);

    public static VIPCfgWrapper getGlobalCfg() {
        if (GlobalCfgHolder.globalCfg.getProductName() == null) {
            logger.error("Global config hasn't been initialized.");
            return null;
        }
        return GlobalCfgHolder.globalCfg;
    }

    /**
     * @deprecated This method was added for backwards compatibility with deprecated {@link VIPCfg#getInstance() getInstance}.
     * Use the {@link #getGlobalCfg() getGlobalCfg} method instead.
     */
    public static VIPCfgWrapper getCfg() {
        return GlobalCfgHolder.globalCfg;
    }

    /**
     * A lazy-loaded singleton Map<String, VIPCfg> configs using the "initialization-on-demand holder" design pattern
     */
    private static class ConfigsHolder {
        static final Map<String, VIPCfgWrapper> configs = new ConcurrentHashMap<>();
    }


    public static VIPCfgWrapper initialize(String cfgFile, boolean isGlobalCfg) throws VIPClientInitException {
        ResourceBundle prop = ResourceBundle.getBundle(cfgFile);
        if (prop == null) {
            throw new VIPClientInitException("Can't initialize VIPCfg. Config file is null.");
        } else if (!prop.containsKey("productName")) {
            throw new VIPClientInitException("Can't initialize VIPCfg. Product name is not defined.");
        }
        String productName = prop.getString("productName");
        VIPCfgWrapper cfgWrapper = getCfg(productName, isGlobalCfg);
        cfgWrapper.getVipCfg().initialize(cfgFile);
        return cfgWrapper;
    }

    public static VIPCfgWrapper getCfg(String productName, boolean isGlobalCfg) {
        if (!contains(productName)) {
            synchronized (ConfigsHolder.configs) {
                if (!contains(productName)) {
                    VIPCfgWrapper cfgWrapper = isGlobalCfg ? GlobalCfgHolder.globalCfg : new VIPCfgWrapper(new VIPCfg());
                    cfgWrapper.setProductName(productName);
                    addCfg(cfgWrapper);
                }
            }
        }
        return ConfigsHolder.configs.get(productName);
    }

    public static VIPCfgWrapper getCfg(String productName) {
        return getCfg(productName, false);
    }

    /**
     * @deprecated Use {@link #getCfg(String) getCfg} instead.
     * This method was added for backwards compatibility with deprecated {@link VIPCfg#getInstance()} and {@link VIPCfg#setProductName(String)} methods.
     */
    static void addCfg(String productName, VIPCfg cfg) {
        if (!contains(productName)) {
            synchronized (ConfigsHolder.configs) {
                if (!contains(productName)) {
                    VIPCfgWrapper cfgWrapper = new VIPCfgWrapper(cfg);
                    cfgWrapper.setProductName(productName);
                    addCfg(cfgWrapper);
                }
            }
        }
    }

    private static void addCfg(VIPCfgWrapper vipCfgWrapper) {
        ConfigsHolder.configs.put(vipCfgWrapper.getProductName(), vipCfgWrapper);
    }

    private static boolean contains(String productName) {
        return ConfigsHolder.configs.containsKey(productName);
    }

    public static class VIPCfgWrapper implements VIPConfig {
        private String productName;
        private VIPCfg vipCfg;

        private VIPCfgWrapper(VIPCfg vipCfg) {
            this.vipCfg = vipCfg;
        }
        public String getProductName() {
            return productName;
        }

        private void setProductName(String productName) {
            this.productName = productName;
        }

        public VIPCfg getVipCfg() {
            return vipCfg;
        }
    }
}
