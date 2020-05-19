/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.util.FileUtil;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);
    private static final String BUNDLE_PREFIX = "messages_";
    
    @Override
    public Map<String, String> getLanguages(String displayLanguage) {
   
    	Map<String, String> supportedLocales = new HashMap<String, String>();
    	Locale inLocale = Locale.forLanguageTag(displayLanguage); 
		try {
			
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl());
			path = FileUtil.getPath(path);
			
			try (Stream<Path> listOfFiles = Files.walk(path).filter(p -> Files.isRegularFile(p))) {
            	listOfFiles.map(file -> {
					String fileName = file.getFileName().toString();
					return fileName.substring(BUNDLE_PREFIX.length(), fileName.indexOf('.'));
				}).forEach(s->supportedLocales.put(s, Locale.forLanguageTag(s).getDisplayName(inLocale)));
            }
					
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
    	return supportedLocales;
    }

}
