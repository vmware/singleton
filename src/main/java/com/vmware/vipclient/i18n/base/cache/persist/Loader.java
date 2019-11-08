/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache.persist;

import java.util.Map;

public interface Loader {

    public Map<String, String> load(String key);

    public boolean updateOrInsert(String key, String content);

    public boolean delete(String key);

    public boolean isExisting(String key);

    public boolean clear();

    public boolean refreshCacheSnapshot(CacheSnapshot cacheSnapshot);
}
