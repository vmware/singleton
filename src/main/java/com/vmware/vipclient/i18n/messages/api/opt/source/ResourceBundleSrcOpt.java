/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.source;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.SourceOpt;

/**
 * 
 * SourceOpt implementation that gets source messages from a ResourceBundle .properties file
 * 
 */
public class ResourceBundleSrcOpt implements SourceOpt {
	
    private ResourceBundle rb;
    private Locale locale;

    public ResourceBundleSrcOpt(String bundle, Locale locale) {
        this.rb = ResourceBundle.getBundle(bundle, locale);
        this.locale = locale;
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
    	try {
    		return rb.getString(key);
    	} catch (MissingResourceException e) { 
    		
    	}
        return key;
    }

    @Override
	public Locale getLocale() {
		return locale;
	}
	
}
