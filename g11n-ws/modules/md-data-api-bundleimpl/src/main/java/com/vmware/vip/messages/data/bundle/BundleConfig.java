/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.bundle;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("bundle")
@Configuration
public class BundleConfig {
	
	@Value("${translation.bundle.file.basepath}")
	private String bundleFileBasePath;

	
	
	
	
	
	
	public String getBasePathWithSeparator() {
		
		int len = bundleFileBasePath.trim().length();
		
		if(bundleFileBasePath.trim().substring(len-1).equals(File.separator)) {
			return bundleFileBasePath.trim(); 
		}
		
		return bundleFileBasePath.trim()+File.separator;
	}


    
}
