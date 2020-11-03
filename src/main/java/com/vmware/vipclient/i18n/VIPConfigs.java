package com.vmware.vipclient.i18n;

import java.util.HashMap;
import java.util.Map;

public class VIPConfigs {
    private static final Map<String, VIPCfg> configs = new HashMap<>();
    private static VIPCfg mainCfg;

    public static VIPCfg getMainCfg() {
        return mainCfg;
    }

    public static synchronized VIPCfg getMainCfgInstance() {
        if (getMainCfg() == null) {
            mainCfg = new VIPCfg();
        }
        return VIPConfigs.getMainCfg();
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
