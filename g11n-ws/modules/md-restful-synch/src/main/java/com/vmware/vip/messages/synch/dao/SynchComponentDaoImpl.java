/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.synch.model.SyncI18nMsg;


/**
 * This java class is used to handle translation bundle file or translation
 * cache for single component
 */
@Repository
public class SynchComponentDaoImpl implements SynchComponentDao {
	
	private static Logger logger = LoggerFactory.getLogger(SynchComponentDaoImpl.class);
	
	@Value("${translation.bundle.file.basepath}")
	private String bundleFileBasePath;
	
	@Override
	public SyncI18nMsg get(String productName, String version, String component, String locale) throws DataException{
		// TODO Auto-generated method stub
		
		String jsonStr = get2JsonStr(productName, version, component, locale);
		
		if(jsonStr == null) {
			logger.warn("File is not existing");
			throw new DataException("File is not existing: ");
		}
		 
		SyncI18nMsg result = JSON.parseObject(jsonStr, SyncI18nMsg.class);
		if(result != null) {
			result.setProduct(productName);
			result.setVersion(version);
			result.setComponent(component);
			result.setLocale(locale);
		}
		return result;
	}

	
	public String get2JsonStr(String productName, String version, String component, String locale){
		// TODO Auto-generated method stub

			String basepath = bundleFileBasePath+File.separator;
			String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
					+ File.separator + version + File.separator + component + File.separator
					+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
			String result = null;
			String jsonfile = basepath + subpath;
			if (new File(jsonfile).exists()) {
				result = new LocalJSONReader().readLocalJSONFile(jsonfile);
			}

			
			return result;
	
	}

	

	public File update(String productName, String version, String component, String locale, TreeMap<String, String> map) throws DataException {
		
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
		}
		String basepath = bundleFileBasePath+File.separator;
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
			writeJSONObjectToJSONFile(jsonfile,component, locale, map);
		} catch (Exception e) {
			throw new DataException(ConstantsKeys.FATA_ERROR + "Failed to write content to file: " + jsonfile + ".", e);
		}
		
		
		return targetFile;
	
	}

	// TODO
	public boolean delete(String productName, String version, String component,
			String locale){
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
		}
		String basepath = bundleFileBasePath+File.separator;
		String subpath = ConstantsFile.L10N_BUNDLES_PATH + productName
				+ File.separator + version + File.separator + component
				+ File.separator
				+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);

		String jsonfile = basepath + subpath;
		File targetFile = new File(jsonfile);
		if(targetFile.exists()) {
			 return targetFile.delete();
		}else {
			return false;
		}
	}
	
	
	
	private  void writeJSONObjectToJSONFile(String jsonFileName,String component, String locale, TreeMap<String, String> messages )
			throws VIPResourceOperationException {
		logger.info("Write JSON content to file: " + jsonFileName);
		Map<String, Object> json = new HashMap<String, Object>();
		json.put(ConstantsKeys.COMPONENT, component);
		json.put(ConstantsKeys.lOCALE, locale);
		json.put(ConstantsKeys.MESSAGES, messages);
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		FileOutputStream outputStream = null;
		try {
			File f = new File(PathUtil.filterPathForSecurity(jsonFileName));
			if (!f.exists()) {
				f.createNewFile();
			}
			outputStream = new FileOutputStream(f);
			write = new OutputStreamWriter(outputStream, ConstantsUnicode.UTF8);
			writer = new BufferedWriter(write);
			writer.write(JSONObject.toJSONString(json, SerializerFeature.PrettyFormat));
		} catch (IOException e) {
			throw new VIPResourceOperationException("Write file '"+ jsonFileName + "' failed.");
		} finally {
			IOUtil.closeWriter(writer);
			IOUtil.closeWriter(write);
			IOUtil.closeOutputStream(outputStream);
		}
	}

}
