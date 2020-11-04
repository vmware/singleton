/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONBundleUtil;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalMessagesOpt implements Opt, MessageOpt {
	
	private Logger logger = LoggerFactory.getLogger(LocalMessagesOpt.class.getName());

	private static final String OFFLINE_RESOURCE_PATH = "{0}/messages_{1}.json";
	private static final String PROPERTIES_SUFFIX = ".properties";
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
		Map<String, String> messages = new HashMap<>();

		try {
			Locale bestMatch = Locale.lookup(Arrays.asList(new Locale.LanguageRange((dto.getLocale()))),
					getSupportedLocales());
			String filePath = FormatUtils.format(OFFLINE_RESOURCE_PATH, dto.getComponent(), bestMatch.toLanguageTag());
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl(), filePath);
			messages = getMessages(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dto.getLocale().equals(ConstantsKeys.SOURCE)) {
			try {
				Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl(), dto.getComponent());
				messages.putAll(getMessages(path));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!messages.isEmpty())
			cacheItem.setCacheItem(messages, null, System.currentTimeMillis(), null);

	}

    private Map<String, String> getMessages(Path path) throws URISyntaxException, IOException {
		Map<String, String> result = new HashMap<>();

		URI uri = Thread.currentThread().getContextClassLoader().
				getResource(path.toString()).toURI();
		if (uri.getScheme().equals("jar")) {
			try(FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
				path = fileSystem.getPath(path.toString());
				if (Files.isRegularFile(path))
					result = JSONBundleUtil.getMessages(path);
				else
					loadProps(result, path);
			}
		} else {
			path = Paths.get(uri);
			if (Files.isRegularFile(path))
				result = JSONBundleUtil.getMessages(path);
			else
				loadProps(result, path);
		}

		return result;
	}

	private void loadProps(Map<String, String> messages, Path offlineResourcePath) throws IOException {
		try (Stream<Path> listOfFiles = Files.walk(offlineResourcePath).filter(p ->
				Files.isRegularFile(p) && p.getFileName().toString().endsWith(PROPERTIES_SUFFIX))) {
			listOfFiles.map(propsFile -> {
				try {
					return readProps(propsFile);
				} catch (IOException e) {
					return new Properties();
				}
			}).forEach(properties -> {
				Set<String> keys = properties.stringPropertyNames();
				for (String key : keys) {
					messages.put(key, properties.getProperty(key));
				}
			});
		}
	}

	private Properties readProps(Path propsFile) throws IOException {
		try (InputStream stream = Files.newInputStream(propsFile)) {
			Properties config = new Properties();
			config.load(stream);
			return config;
		}
	}
    
    private List<Locale> getSupportedLocales() {
		ProductService ps = new ProductService(dto);
		Set<String> supportedLanguages = ps.getSupportedLanguageTags(DataSourceEnum.Bundle);
        logger.debug("supported languages: [{}]", supportedLanguages.toString());
    	List<Locale> supportedLocales = new LinkedList<Locale>();
    	for (String languageTag : supportedLanguages) {
    		supportedLocales.add(Locale.forLanguageTag(languageTag));
    	}
    	return supportedLocales;
    }
}
