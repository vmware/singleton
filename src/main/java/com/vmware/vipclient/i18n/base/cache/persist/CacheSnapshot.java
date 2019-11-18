/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache.persist;

import java.io.Serializable;
import java.util.Set;

import com.vmware.vipclient.i18n.base.cache.CacheMode;

public class CacheSnapshot implements Serializable {
    private String      productName   = "";
    private String      version       = "";
    private String      vipServer     = "";
    private CacheMode   cacheMode;
    private String      dropId        = "";
    private long        expiredTime;
    private long        lastClean;
    private String      cacheRootPath = "";
    private Set<String> components;
    private Set<String> locales;

    public CacheSnapshot() {
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVipServer() {
        return vipServer;
    }

    public void setVipServer(String vipServer) {
        this.vipServer = vipServer;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public String getDropId() {
        return dropId;
    }

    public void setDropId(String dropId) {
        this.dropId = dropId;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public long getLastClean() {
        return lastClean;
    }

    public void setLastClean(long lastClean) {
        this.lastClean = lastClean;
    }

    public String getCacheRootPath() {
        return this.cacheRootPath;
    }

    public void setCacheRootPath(String cacheRootPath) {
        this.cacheRootPath = cacheRootPath;
    }

    public Set<String> getComponents() {
        return components;
    }

    public void setComponents(Set<String> components) {
        this.components = components;
    }

    public Set<String> getLocales() {
        return locales;
    }

    public void setLocales(Set<String> locales) {
        this.locales = locales;
    }
}
