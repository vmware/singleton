/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.bundle;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.vmware.vip.common.utils.UnzipTranslationUtils;
@Profile("bundle")
@Component
public class InitBundleListener implements ApplicationListener<ApplicationReadyEvent> {
	private static Logger logger = LoggerFactory.getLogger(InitBundleListener.class);
	private final String clearStr="translation.bundle.file.clean";

	@Autowired
	private BundleConfig bundleConfig;
	
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {

		boolean cleanflag = Boolean.parseBoolean(System.getProperty(clearStr));
		if(cleanflag) {
			logger.info("start clean and unzip translation to local");
		}else {
			logger.info("start unzip translation to local");
		}
		try {
			String bundleAbsPath = new File(bundleConfig.getBasePathWithSeparator()).toPath().toAbsolutePath().normalize().toString();
			if(!bundleAbsPath.endsWith(File.separator)) {
				bundleAbsPath = bundleAbsPath+File.separator;
			}
		    logger.info("the bundle's base path: {}", bundleAbsPath);
			UnzipTranslationUtils.unzipTranslationToLocal(bundleAbsPath,cleanflag, InitBundleListener.class);	
		} catch (IOException e) {
			
			logger.warn("init bundle exception or no bundle file need to unzip", e);

		}
		
		System.clearProperty(clearStr);
		logger.info("init the bundles end");
		
		
	}

}
