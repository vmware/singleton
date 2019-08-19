/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.i18n.dto.BaseDTO;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.bundle.BundleConfig;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.exception.BundleException;

/**
 * This java class is used to get locale list from file in or out of jar.
 */
@Component
public class ProductDao implements IProductDao {

	@Autowired
	private BundleConfig bundleConfig;

	public List<String> getComponentList(String productName, String version)
			throws BundleException {
		List<String> componentList = new ArrayList<String>();

		String basePath = bundleConfig.getBasePathWithSeparator();

		String filePath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter.getProductVersionConcatName(new BaseDTO(productName, version));
		String bundlePath = basePath + filePath;

		File file = new File(bundlePath);
		if (file.exists()) {
			File[] libfiles = file.listFiles();
			for (File file2 : libfiles) {
				if(file2.isDirectory()) {
					componentList.add(file2.getName());
				}
			}

			if (componentList.size() == 0) {
				throw new BundleException("Component list is empty.");
			}
		} else {
			throw new BundleException("Can't find resource from " + productName + "\\" + version);
		}
		return componentList;
	}

	public List<String> getLocaleList(String productName, String version) throws BundleException {
		List<String> supportedLocaleList = new ArrayList<String>();

		String basePath = bundleConfig.getBasePathWithSeparator();

		String filePath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter.getProductVersionConcatName(new BaseDTO(productName, version));
		String bundlePath = basePath + filePath;

		File file = new File(bundlePath);
		if (file.exists() && file.isDirectory()) {
			File[] componentFolders = file.listFiles();
			for (File folder : componentFolders) {
				if (folder.isDirectory()) {
					String[] translationFileNames = folder.list();
					for (int i = 0; i < translationFileNames.length; i++) {
						addLocaleToList(supportedLocaleList, translationFileNames[i]);
					}
				}
			}
		} else {
			throw new BundleException("The file is not existing: " + bundlePath);
		}
		if (supportedLocaleList.size() == 0) {
			throw new BundleException("The locae list is empty.");
		}
		return supportedLocaleList;
	}

	/**
	 * Get locale by cutting locale string from the file name and then add it to
	 * locale list.
	 *
	 * @param supportedLocaleList
	 * @param fileName
	 */
	private void addLocaleToList(List<String> supportedLocaleList, String fileName) throws BundleException {
		String locale = ResourceFilePathGetter.getLocaleByFileName(fileName);
		if (!StringUtils.isEmpty(locale)) {
			if (!supportedLocaleList.contains(locale)) {
				supportedLocaleList.add(locale);
			}
		} else {
			throw new BundleException("Empty locale when get it from file: " + fileName);
		}
	}

	@Override
	public String getVersionInfo(String productName, String version)
			throws DataException {
		String basepath = bundleConfig.getBasePathWithSeparator();
		String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
				+ File.separator + version + File.separator
				+ ConstantsFile.VERSION_FILE;
		String result = null;
		String jsonfile = basepath + subpath;
		if (new File(jsonfile).exists()) {
			result = new LocalJSONReader().readLocalJSONFile(jsonfile);
		}
		if (result == null) {
			throw new BundleException("File is not existing: " + jsonfile);
		}

		return result;

	}

	/**
	 * get all products and versions as map with the product as key and with its versions as value
	 *
	 * @return
	 * @throws BundleException
	 */
	public Map<String, String[]> getProductsAndVersions() throws BundleException {
		Map<String, String[]> productsAndVersions = new HashMap<>();
		String basePath = bundleConfig.getBasePathWithSeparator() + ConstantsFile.L10N_BUNDLES_PATH;
		File ff = new File("");
		String aa = ff.getAbsolutePath();
		File file = new File(basePath);
		if (file.exists() && file.isDirectory()) {
			File[] productFolders = file.listFiles();
			for (File folder : productFolders) {
				if (folder.isDirectory()) {
					String productName = folder.getName();
					String[] versions = folder.list();
					productsAndVersions.put(productName, versions);
				}
			}
		} else {
			throw new BundleException("The file is not existing: " + basePath);
		}
		return productsAndVersions;
	}
}
