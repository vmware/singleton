package com.vmware.vipclient.i18n.base;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;

public enum L10nTypeEnum {
    L3 (VIPCfg.CACHE_L3, MessageCache.class),
    L2 (VIPCfg.CACHE_L2, FormattingCache.class);

    private final String label;
    private final Class cacheClass;

    L10nTypeEnum(String label, Class cacheClass) {
        this.label = label;
        this.cacheClass = cacheClass;
    }

    public static L10nTypeEnum getL10nType(Class cacheClass) {
        for (L10nTypeEnum l10nType: values()) {
            if (l10nType.cacheClass.equals(cacheClass)) {
                return l10nType;
            }
        }
        throw new IllegalArgumentException(cacheClass.toString());
    }

    public String getLabel() {
        return label;
    }

}

