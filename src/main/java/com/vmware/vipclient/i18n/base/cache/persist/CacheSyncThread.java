/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache.persist;

import java.util.Set;

import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class CacheSyncThread extends Thread {
    private Set<String> components;
    private Set<String> locales;
    private Loader      loader;

    public CacheSyncThread(Set<String> components, Set<String> locales,
            Loader loader) {
        this.components = components;
        this.locales = locales;
        this.loader = loader;
    }

    @Override
    public void run() {
        CacheSnapshot cs = TranslationCacheManager.getInstance()
                .getCacheSnapshot();
        for (String component : components) {
            for (String locale : locales) {
                MessagesDTO dto = new MessagesDTO();
                dto.setProductID(cs.getProductName());
                dto.setVersion(cs.getVersion());
                dto.setComponent(component);
                dto.setLocale(locale);
                String r = this.fetch(dto);
                this.loader.updateOrInsert(dto.encryption(dto.getCompositStrAsCacheKey()), r);
            }
        }
    }

    private String fetch(MessagesDTO dto) {
        MessageCacheItem cacheItem = new MessageCacheItem();
        new ComponentBasedOpt(dto).getComponentMessages(cacheItem);
		return cacheItem.getCachedData().toString();
    }

}
