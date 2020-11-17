/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.vipclient.i18n.VIPCfg;
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
import java.nio.file.*;
import java.util.*;

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
		try {
			String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), dto.getLocale());
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
			cacheItem.setCacheItem(messages, null, System.currentTimeMillis(), null);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			// Do not update cacheItem
		}
    }
}
