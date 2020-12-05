/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONBundleUtil;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		InputStream is = getInputStream();
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(is, "UTF-8"));
			Map<String, String> messages = (JSONObject) jsonObject.get("messages");
			cacheItem.setCacheItem(messages, null, System.currentTimeMillis(), null);
		} catch (Exception e) {
			logger.error("Failed to get offline messages for product: " + dto.getProductID() + " " + dto.getVersion() +
					", component: " + dto.getComponent() + ", locale: " + dto.getLocale() + ", exception: " + e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}
		}
	}

	private InputStream getInputStream() {
		String locale = LocaleUtility.fmtToMappedLocale(dto.getLocale()).toLanguageTag();
		while (true) {
			String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), locale);
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl(), filePath);
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.toString());
			if (is != null)
				return is;
			/*
			 * If valid URI is not found, find the next best matching locale available in the file system
			 * This could happen if:
			 * a. the matching resource bundle had been corrupted or removed from the file system since last check.
			 * b. the requested locale hadn't been matched against the list of supported locales. This happens if
			 * supported locales cache hasn't been initialized or if previous attempts to populate the cache had failed.
			 */
			int index = locale.lastIndexOf("-");
			if (index <= 0)
				break;
			locale = locale.substring(0, index);
		}
		throw new VIPJavaClientException("Failed to get resource bundle for locale: " + locale);
	}
}
