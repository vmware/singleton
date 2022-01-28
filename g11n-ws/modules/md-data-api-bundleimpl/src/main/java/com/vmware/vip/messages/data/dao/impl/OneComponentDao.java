/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.utils.SortJSONUtils;
import com.vmware.vip.messages.data.bundle.BundleConfig;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;
import com.vmware.vip.messages.data.exception.BundleException;

/**
 * This java class is used to handle translation bundle file or translation
 * cache for single component
 */
@Profile("bundle")
@Repository
public class OneComponentDao implements IOneComponentDao {
	
	private static Logger logger = LoggerFactory.getLogger(OneComponentDao.class);
	@Autowired
	private BundleConfig bundleConfig;
	
	
	
	@Override
	public ResultI18Message get(String productName, String version, String component, String locale) throws DataException{
	
		
		String jsonStr = get2JsonStr(productName, version, component, locale);
		
		 ObjectMapper mapper = new ObjectMapper(); 
		 
		 ResultI18Message result = null;
		try {
			result = mapper.readValue(jsonStr, ResultI18Message.class);
		} catch (JsonParseException e) {
			String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
			logger.error(errorLog, e);
			throw new BundleException(e.getMessage(), e);

		} catch (JsonMappingException e) {
			String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
			logger.error(errorLog, e);
			throw new BundleException(ConstantsMsg.FIFE_NOT_FOUND, e);
		} catch (IOException e) {
			String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
			logger.error(errorLog, e);
			throw new BundleException(ConstantsMsg.FIFE_NOT_FOUND, e);

		}
		
		if(result != null) {
			result.setProduct(productName);
			result.setVersion(version);
			result.setComponent(component);
			result.setLocale(locale);
		}else {
			throw new BundleException(ConstantsMsg.FIFE_NOT_FOUND);
		}


		return result;
	}

	@Override
	public String get2JsonStr(String productName, String version, String component, String locale) throws DataException{
	
			String basepath = bundleConfig.getBasePathWithSeparator();
			
			String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
				     + File.separator + version + File.separator + component + File.separator
					+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
			String result = null;
			String jsonfile = basepath + subpath;
			if (new File(jsonfile).exists()) {
				result = new LocalJSONReader().readLocalJSONFile(jsonfile);
			}

			if(result ==null) {
				throw new BundleException(ConstantsMsg.FIFE_NOT_FOUND+": " + jsonfile);
			}

			
			
			return result;
	
	}

	@Override
	public boolean add(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException{
	
		return false;
	}

	
	public boolean update(String productName, String version, String component,
			String locale, Map<String, String> map) throws DataException {
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
		}
		String basepath = bundleConfig.getBasePathWithSeparator();
		
		String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
				+ File.separator + version + File.separator + component
				+ File.separator
				+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
		
		String jsonfile = basepath + subpath;
		File targetFile = new File(jsonfile);
		try {
			if (!targetFile.exists()) {
				FileUtils.write(targetFile, "", ConstantsUnicode.UTF8, true);
			}
			SortJSONUtils.writeJSONObjectToJSONFile(jsonfile,
					component, locale, map);
		} catch (Exception e) {
			throw new BundleException(ConstantsKeys.FATA_ERROR + "Failed to write content to file: " + jsonfile + ".", e);
		}
		return true;
	}


	public boolean delete(String productName, String version, String component,
			String locale) throws DataException {
		return false;
	}

}
