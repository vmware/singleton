/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.source;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.SourceOpt;

public class ResourceBundleSrcOpt implements SourceOpt {
	
    private ResourceBundle rb;

    public ResourceBundleSrcOpt(String bundle, Locale locale) {
        this.rb = ResourceBundle.getBundle(bundle, locale);
    }
    
    @Override
    public void getComponentMessages(MessageCacheItem cacheItem) {
    	Enumeration<String> keys = rb.getKeys();
    	while (keys.hasMoreElements()) {
    		String key = keys.nextElement();
    		cacheItem.addCacheData(key, rb.getString(key));
    	}
    }

    @Override
    public String getMessage(String key) {
        return rb.getString(key);
    }
	
}
