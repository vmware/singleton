/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class.getName());
    
    @Override
    public Map<String, String> getDisplayNamesFromCLDR(String language) {
   
    	Map<String, String> supportedLocales = new HashMap<String, String>();
    	
    	String offlineResourcesBaseUrl = VIPCfg.getInstance().getOfflineResourcesBaseUrl();
		try {
			Path path = Paths.get(Thread.currentThread().getContextClassLoader().
					getResource(Paths.get(offlineResourcesBaseUrl).toString()).toURI());
			
			try (Stream<Path> listOfFiles = Files.walk(path).filter(Files::isRegularFile)) {
				listOfFiles.map(file -> file.getFileName().toString().substring(9, file.getFileName().toString().indexOf(".")))
						.forEach(s->supportedLocales.put(s, s));
			}		
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
    	return supportedLocales;
    }

}
