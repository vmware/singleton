/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);
    private static final String BUNDLE_PREFIX = "messages_";
    
    @Override
    public Map<String, String> getSupportedLanguages(String displayLanguage) {
   
    	Map<String, String> supportedLocales = new HashMap<String, String>();
		try {
			
			Path path = Paths.get(VIPCfg.getInstance().getOfflineResourcesBaseUrl());
			
			URI uri = Thread.currentThread().getContextClassLoader().
					getResource(path.toString()).toURI();

	    	if (uri.getScheme().equals("jar")) {
	    		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
	    			path = fileSystem.getPath(path.toString());
	    			getSupportedLocales(path, supportedLocales, displayLanguage);
	    		}
			} else {
				path = Paths.get(uri);
				getSupportedLocales(path, supportedLocales, displayLanguage);
			}
	    	
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
    	return supportedLocales;
    }
    
    private void getSupportedLocales(Path path, Map<String, String> supportedLocales, String displayLanguage) throws IOException {
    	Locale inLocale = Locale.forLanguageTag(displayLanguage); 
    	try (Stream<Path> listOfFiles = Files.walk(path).filter(p -> Files.isRegularFile(p))) {
        	listOfFiles.map(file -> {
				String fileName = file.getFileName().toString();
				return fileName.substring(BUNDLE_PREFIX.length(), fileName.indexOf('.'));
			}).forEach(s->supportedLocales.put(s, Locale.forLanguageTag(s).getDisplayName(inLocale)));
        }
    }

}
