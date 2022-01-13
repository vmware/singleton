/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import com.vmware.vipclient.i18n.VIPCfg;

public abstract class CacheItem {
    private String etag;
    private long timestamp;
    private Long maxAgeMillis = 86400000l;

    public String getEtag() {
        return etag;
    }

    protected void setEtag(String etag) {
        this.etag = etag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMaxAgeMillis() {
        return maxAgeMillis;
    }

    protected void setMaxAgeMillis(Long maxAgeMillis) {
        if (maxAgeMillis != null)
            this.maxAgeMillis = maxAgeMillis;
    }

    public boolean isExpired() {
        // If offline mode only, cache never expires.
        if (VIPCfg.getInstance().getVipServer() == null) {
            return false;
        }
        // If maxAgeFromConfig is present, it means it is using the old way
        // of caching expiration, so do not expire individual CacheItem object
        if (VIPCfg.getInstance().getCacheExpiredTime() != 0) {
            return false;
        }

        synchronized (this) {
            return System.currentTimeMillis() - this.getTimestamp() >= maxAgeMillis;
        }
    }
}
