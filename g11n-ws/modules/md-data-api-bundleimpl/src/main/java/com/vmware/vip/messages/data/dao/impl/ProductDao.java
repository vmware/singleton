/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile("bundle")
@Component
public class ProductDao implements IProductDao {

	@Autowired
	private BundleConfig bundleConfig;

	public List<String> getComponentList(String productName, String version)
			throws BundleException {
		List<String> componentList = new ArrayList<>();

		String basePath = bundleConfig.getBasePathWithSeparator();

		String filePath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter.getProductVersionConcatName(new BaseDTO(productName, version));
		String bundlePath = basePath + filePath;

		File file = new File(bundlePath);
		if (file.exists() && file.isDirectory()) {
			File[] libfiles = file.listFiles();
			for (File file2 : libfiles) {
				if(file2.isDirectory()) {
					componentList.add(file2.getName());
				}
			}
		} else {
			throw new BundleException("Can't find resource from " + productName + "\\" + version);
		}
		return componentList;
	}

	public List<String> getLocaleList(String productName, String version) throws BundleException {
		List<String> supportedLocaleList = new ArrayList<>();

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
     * Get one product corresponding versions
     *
     * @return a map with the product names as key and with version list as value
     * @throws BundleException
     */
    @Override
    public List<String> getVersionList(String productName) throws DataException {
   
        String basePath = bundleConfig.getBasePathWithSeparator() + ConstantsFile.L10N_BUNDLES_PATH + productName
                + File.separator;
        File fileBase = new File(basePath);
        if (fileBase.exists() && fileBase.isDirectory()) {
            String[] versionNames = fileBase.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    // TODO Auto-generated method stub
                    if(dir.isDirectory()) {
                        return true;
                    }else {
                        return false;
                    }
                   
                }
                
            });
           return Arrays.asList(versionNames);
        }else {
            throw new BundleException("The base l10n dir is not existing, the missed dir is: " + basePath);
        }
    }

    /**
     * Get the content of the Allow Product List by bundle.json file name
     */
    @Override
    public String getAllowProductListContent(String contentFilePath) throws DataException {
        if (new File(contentFilePath).exists()) {
         return new LocalJSONReader().readLocalJSONFile(contentFilePath);
        }else {
            return null;
        }
        
    }
}
