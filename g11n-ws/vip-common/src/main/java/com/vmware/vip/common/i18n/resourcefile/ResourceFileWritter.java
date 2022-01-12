/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.resourcefile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * The class represents a Writter for writing content to resource file
 * 
 */
public class ResourceFileWritter {

	private static Logger logger = LoggerFactory.getLogger(ResourceFileWritter.class);

	/**
	 * This function will perform: 1. Parse the remote's responding result, the
	 * result would be like:
	 * {"locales":"en, ja, zh_CN","components":"default","bundles":"[{ \"en\":
	 * {\"cancel\":\"Abbrechen\"}, \"ja\": {\"cancel\":\"Abbrechen\"},
	 * \"zh_CN\": {\"cancel\":\"Abbrechen\"}, \"component\":\"default\"
	 * }]","version":"1.0.0","productName":"devCenter"} 2. Convert the packaged
	 * JSON content with multiple component messages to multiple JSO resource
	 * files 3. Write the translation to path
	 * src-generated/main/resources/l10n/bundles/";
	 *
	 * @param remoteRusult
	 *            the string to parse and write
	 * @throws VIPResourceOperationException
	 *             if file opertion gets problem
	 */
	public static void writeStrToMultiJSONFiles(String remoteRusult)
			throws VIPResourceOperationException {
		MultiComponentsDTO baseTranslationDTO = new MultiComponentsDTO();
		try {
			baseTranslationDTO = MultiComponentsDTO
					.getMultiComponentsDTO(remoteRusult);
		} catch (VIPAPIException e1) {
			e1.printStackTrace();
		}
		List bundles = baseTranslationDTO.getBundles();
		Iterator<?> it = bundles.iterator();
		SingleComponentDTO singleComponentDTO = new SingleComponentDTO();
		singleComponentDTO.setProductName(baseTranslationDTO.getProductName());
		singleComponentDTO.setVersion(baseTranslationDTO.getVersion());
		while (it.hasNext()) {
			try {
				JSONObject bundleObj = (JSONObject) JSONValue
						.parseWithException(it.next().toString());
				String component = (String) bundleObj
						.get(ConstantsKeys.COMPONENT);
				singleComponentDTO.setComponent(component);
				List<String> locales = baseTranslationDTO.getLocales();
				for (String locale : locales) {
					String tLocale = StringUtils.trim(locale);
					singleComponentDTO.setLocale(tLocale);
					singleComponentDTO.setMessages((JSONObject) bundleObj
							.get(tLocale));
					String jsonFilePathDir = ResourceFilePathGetter
							.getLocalizedJSONFilesDir(singleComponentDTO);
					ResourceFileWritter.writeJSONObjectToJSONFile(
							jsonFilePathDir
									+ ConstantsChar.BACKSLASH
									+ ResourceFilePathGetter
											.getLocalizedJSONFileName(tLocale),
							singleComponentDTO);
				}
			} catch (ParseException e) {
				throw new VIPResourceOperationException("Parse '"
						+ it.next().toString() + "' failed.");
			}
		}
	}

	/**
	 * Write the key/value pairs to the JSON file from a JSON object
	 *
	 * @param jsonFileName
	 *            a JSON file name
	 * @param component
	 *            the name of component
	 * @param locale
	 *            the locale string
	 * @param pairs
	 *            the JSON object contains keys and values
	 * @return VIPResourceOperationException
	 */
	@SuppressWarnings("unchecked")
	public static void writeJSONObjectToJSONFile(String jsonFileName,
			String component, String locale, JSONObject pairs)
			throws VIPResourceOperationException {
		logger.info("Write JSON content to file: " + jsonFileName);
		JSONObject json = new JSONObject();
		json.put(ConstantsKeys.COMPONENT, component);
		json.put(ConstantsKeys.lOCALE, locale);
		json.put(ConstantsKeys.MESSAGES, pairs);
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		FileOutputStream outputStream = null;
		try {
			File f = new File(jsonFileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			outputStream = new FileOutputStream(f);
			write = new OutputStreamWriter(outputStream, ConstantsUnicode.UTF8);
			writer = new BufferedWriter(write);
			writer.write(json.toJSONString());
		} catch (IOException e) {
			throw new VIPResourceOperationException("Write file '"
					+ jsonFileName + "' failed.");
		} finally {
			IOUtil.closeWriter(writer);
			IOUtil.closeWriter(write);
			IOUtil.closeOutputStream(outputStream);
		}
	}
	
	/*
	 * override method
	 */
	public static void writeJSONObjectToJSONFile(String jsonFileName,
			String component, String locale, Map<String, String> messages)
			throws VIPResourceOperationException {
		logger.info("Write JSON content to file: " + jsonFileName);
		SingleComponentDTO dto = new SingleComponentDTO();
		dto.setComponent(component);
		dto.setLocale(locale);
		dto.setMessages(messages);
		ResourceFileWritter.writeJSONObjectToJSONFile(jsonFileName, dto);
	}

	/**
	 * Write a JSON object to the JSON file by parsing data in
	 * SingleComponentDTO instance
	 *
	 * @param jsonFileName
	 *            a JSON file name to be written
	 * @param singleComponentDTO
	 *            the SingleComponentDTO's instance
	 * @return VIPResourceOperationException
	 */
	public static void writeJSONObjectToJSONFile(String jsonFileName,
			SingleComponentDTO singleComponentDTO)
			throws VIPResourceOperationException {
		logger.info("Write JSON content to file: " + jsonFileName);
		Map<String, Object> json = new HashMap<String, Object>();

		json.put(ConstantsKeys.COMPONENT, singleComponentDTO.getComponent());
		json.put(ConstantsKeys.lOCALE, singleComponentDTO.getLocale());
		json.put(ConstantsKeys.MESSAGES, singleComponentDTO.getMessages());
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
			// [bug 1827676] replace("\\u", "\\\\u")) happens on
			// SourceServiceImpl.synchronizeSourceToBundle
			writer.write(new ObjectMapper().writerWithDefaultPrettyPrinter()
					.writeValueAsString(json));
		} catch (IOException e) {
			throw new VIPResourceOperationException("Write file '"
					+ jsonFileName + "' failed.");
		} finally {
			IOUtil.closeWriter(writer);
			IOUtil.closeWriter(write);
			IOUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * Write a MultiComponentsDTO to a JSON file
	 *
	 * @param jsonFileName
	 *            a JSON file name to be written
	 * @param multiComponentsDTO
	 *            the MultiComponentsDTO instance
	 * @return VIPResourceOperationException
	 */
	public static void writeMultiComponentsDTOToJSONFile(String jsonFileName,
			MultiComponentsDTO multiComponentsDTO)
			throws VIPResourceOperationException {
		logger.info("Write JSON content to file: " + jsonFileName);
		FileWriter jsonFileWriter = null;
		try {
			File file = new File(PathUtil.filterPathForSecurity(jsonFileName));
			if (!file.exists()) {
				file.createNewFile();
			}
			jsonFileWriter = new FileWriter(
					PathUtil.filterPathForSecurity(jsonFileName));
			jsonFileWriter.write(multiComponentsDTO.toJSONString());
			jsonFileWriter.flush();
		} catch (IOException e) {
			throw new VIPResourceOperationException("Write file '"
					+ jsonFileName + "' failed.");
		} finally {
			IOUtil.closeWriter(jsonFileWriter);
		}
	}
}
