/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VIPCfgFactory {
    private static Logger logger = LoggerFactory.getLogger(VIPCfgFactory.class);

    public static final VIPCfg globalCfg = new VIPCfg();

    private static class ConfigsHolder {
        static final Map<String, VIPCfg> configs = new ConcurrentHashMap<>();
    }

    @Deprecated
    protected static void changeProductName(VIPCfg cfg, String oldName) {
        if (oldName == null) {
            synchronized (ConfigsHolder.configs) {
                ConfigsHolder.configs.put(cfg.getProductName(), cfg);
            }
        } else {
            synchronized (ConfigsHolder.configs) {
                ConfigsHolder.configs.remove(oldName);
                ConfigsHolder.configs.put(cfg.getProductName(), cfg);
            }
        }
    }

    public static VIPCfg getCfg(String productName, boolean... useGlobalCfg) {
        boolean useGlobal = useGlobalCfg.length > 0 ? useGlobalCfg[0] : false;
        VIPCfg vipCfg = ConfigsHolder.configs.get(productName);
        if (vipCfg == null) {
            vipCfg = useGlobal ? globalCfg : new VIPCfg();
            vipCfg.setProductName(productName);
            ConfigsHolder.configs.put(productName, vipCfg);
        } else if (useGlobal && !vipCfg.equals(globalCfg)) {
            logger.debug("Can't use the global VIPCfg instance. Another VIPCfg for product: " + productName + " already exists.");
        }
        return ConfigsHolder.configs.get(productName);
    }

    public static VIPCfg initialize(String cfgFile, boolean... useGlobalCfg) throws VIPClientInitException {
        boolean useGlobal = useGlobalCfg.length > 0 ? useGlobalCfg[0] : false;
        ResourceBundle prop = ResourceBundle.getBundle(cfgFile);
        if (prop == null) {
            throw new VIPClientInitException("Can't initialize VIPCfg. Config file is null.");
        } else if (!prop.containsKey("productName")) {
            throw new VIPClientInitException("Can't initialize VIPCfg. Product name is not defined.");
        }
        String productName = prop.getString("productName");
        VIPCfg vipCfg = getCfg(productName, useGlobal);
        vipCfg.init(cfgFile);
        return vipCfg;
    }

}
