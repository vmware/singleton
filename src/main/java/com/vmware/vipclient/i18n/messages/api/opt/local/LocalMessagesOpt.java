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
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
			URI uri = getURI();
			Map<String, String> messages = null;
	    	if (uri.getScheme().equals("jar")) {
				synchronized (LocalFileSystem.getInstance()) {
					try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
						Path path = fileSystem.getPath(Paths.get(uri).toString());
						messages = JSONBundleUtil.getMessages(path);
					}
				}
			} else {
				messages = JSONBundleUtil.getMessages(Paths.get(uri));
			}
			cacheItem.setCacheItem(messages, null, System.currentTimeMillis(), null);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
    }

	private URI getURI() throws URISyntaxException {
		String locale = LocaleUtility.fmtToMappedLocale(dto.getLocale()).toLanguageTag();
		URL url = null;
		while (url == null) {
			String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), locale);
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl(), filePath);
			url = Thread.currentThread().getContextClassLoader().getResource(path.toString());
			if (url != null)
				break;

			/*
			 * If url is not found, it could be one of the following cases:
			 * a. the matching resource bundle had been corrupted or removed from the file system since last check.
			 * b. the requested locale hasn't been matched against the list of supported locales. This happens if
			 * supported locales cache hasn't been initialized or if previous attempts to populate the cache had failed.
			 *
			 * In any of the cases above, find the next best matching locale available in the file system.
			 */
			int index = locale.lastIndexOf("-");
			if (index <= 0)
				break;
			locale = locale.substring(0, index);
		}

		return url.toURI();
	}
}
