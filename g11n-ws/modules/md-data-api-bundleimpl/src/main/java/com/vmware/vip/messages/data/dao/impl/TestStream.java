package com.vmware.vip.messages.data.dao.impl;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.vmware.vip.messages.data.bundle.BundleConfig;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.exception.BundleException;
@Profile("bundle")
@Component
public class TestStream {
	private static Logger logger = LoggerFactory.getLogger(TestStream.class);
	@Autowired
	private BundleConfig bundleConfig;
	
	@Autowired
	private ProductDao productDao;
	
	public List<File> get2JsonFiles(String productName, String version, List<String> components,
			List<String> locales) throws DataException{
		
		if (components == null ) {
			components = productDao.getComponentList(productName, version);
		}
		if (locales == null) {
			locales = productDao.getLocaleList(productName, version);
		}
		
		
		for (String component : components) {
			for (String locale : locales) {
			}
		}
		
		return null;
	}

}
