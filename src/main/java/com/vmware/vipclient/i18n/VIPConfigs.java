/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n;

import java.util.HashMap;
import java.util.Map;

public class VIPConfigs {
    private static final Map<String, VIPCfg> configs = new HashMap<>();
    private static VIPCfg mainCfg;

    public static synchronized VIPCfg getMainCfg() {
        if (mainCfg == null) {
            mainCfg = new VIPCfg();
        }
        return mainCfg;
    }

    public static VIPCfg getCfg(String productName) {
        return configs.get(productName);
    }

    public static void addCfg(VIPCfg cfg) {
        configs.put(cfg.getProductName(), cfg);
    }

    public static boolean contains(String productName) {
        return configs.containsKey(productName);
    }

}
