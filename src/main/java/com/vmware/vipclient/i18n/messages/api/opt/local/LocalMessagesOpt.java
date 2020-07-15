/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONBundleUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocalMessagesOpt implements Opt, MessageOpt {
	
	private Logger logger = LoggerFactory.getLogger(LocalMessagesOpt.class.getName());

	private static final String OFFLINE_RESOURCE_PATH = "{0}/messages_{1}.json";
    private MessagesDTO dto;

    public LocalMessagesOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    @Deprecated
    public JSONObject getComponentMessages() {
        return JSONBundleUtil.getMessages(dto.getLocale(), dto.getProductID(),
                dto.getVersion(), dto.getComponent());
    }
    
    @Override
    public void getComponentMessages(MessageCacheItem cacheItem) {
        Locale bestMatch = Locale.lookup(Arrays.asList(new Locale.LanguageRange((dto.getLocale()))),
        		getSupportedLocales());
		try {
			String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), bestMatch.toLanguageTag());
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl(), filePath);
			
			URI uri = Thread.currentThread().getContextClassLoader().
					getResource(path.toString()).toURI();
			
			Map<String, String> messages = null;
	    	if (uri.getScheme().equals("jar")) {
				try(FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
					path = fileSystem.getPath(path.toString());
					messages = JSONBundleUtil.getMessages(path);
				}
			} else {
				path = Paths.get(uri);
				messages = JSONBundleUtil.getMessages(path);
			}
				
	    	cacheItem.addCachedData(messages);
	    	cacheItem.setTimestamp(System.currentTimeMillis());
		} catch (Exception e) {
			logger.debug(e.getMessage());
			// Do not update cacheItem
		}
    }
    
    private List<Locale> getSupportedLocales() {
    	Map<String, String> supportedLanguages = DataSourceEnum.Bundle.createLocaleOpt()
    			.getSupportedLanguages(dto.getLocale());
    	List<Locale> supportedLocales = new LinkedList<Locale>();
    	for (String languageTag : supportedLanguages.keySet()) {
    		supportedLocales.add(Locale.forLanguageTag(languageTag));
    	}
    	return supportedLocales;
    }
}
