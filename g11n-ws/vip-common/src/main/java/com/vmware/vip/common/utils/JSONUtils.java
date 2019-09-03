/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.source.util.IOUtil;

/**
 * This class is used for handling JSON object, string, map, etc.
 */
public class JSONUtils {

	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

	/**
	 * Parse a JSON string to a JSON object.
	 *
	 * @param jsonStr
	 *            JSON string
	 * @return JSONObject
	 */
	public static JSONObject string2JSON(String jsonStr) {
		JSONObject genreJsonObject = null;
		try {
			genreJsonObject = JSONObject.parseObject(jsonStr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return genreJsonObject;
	}

	/**
	 * Parse a JSON string to a map.
	 *
	 * @param jsonStr
	 *            JSON string
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map string2Map(String jsonStr) {
		Map genreJsonObject = null;
		try {
			genreJsonObject = JSON.parseObject(jsonStr, HashMap.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return genreJsonObject;
	}

	/**
	 * Get an ordered map with adding pseudo tag for each element.
	 *
	 * @param map
	 *            a map without order
	 * @param pseudoTag
	 *            a tag string, e.g @@
	 * @return LinkedHashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getOrderedMapForPseudo(Map map, String pseudoTag) {
		Map newMap = new LinkedHashMap();
		if (map == null || map.size() == 0)
			return newMap;
		Set set = map.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Object key = it.next();
			String value = (String) map.get(key);
			if (value.startsWith("@") && value.endsWith("@")) {
				newMap.put(key, value);
			} else {
				newMap.put(key, pseudoTag + value + pseudoTag);
			}
		}
		return newMap;
	}

	/**
	 * Get an JSON object from map with adding pseudo tag for each element.
	 *
	 * @param map
	 *            a map
	 * @param pseudoTag
	 *            a tag string, e.g @@
	 * @return JSON object
	 */
	public static JSONObject getJSONObjectForPseudo(Map<String, Object> map, String pseudoTag) {
		JSONObject jsonObj = null;
		if (map != null && map.size() > 0) {
			jsonObj = new JSONObject();
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String key = it.next();
				jsonObj.put(key, pseudoTag + map.get(key) + pseudoTag);
			}
		}
		return jsonObj;
	}

	/**
	 * Convert Json string to Map<String, Object>
	 * 
	 * @param json
	 * @return Map<String, Object> obj
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJson(String json) {

		Map<String, Object> result = null;
		if (!StringUtils.isEmpty(json)) {
			try {
				result  = JSON.parseObject(json, HashMap.class);;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * Get Map<String, Object> from a json file.
	 * 
	 * @param filePath
	 * @return Map<String, Object> obj
	 */
	public static Map<String, Object> getMapFromJsonFile(String filePath) {
		Map<String, Object> json=null;
		try {
			File file = new File(filePath);
			
			JSON.parseObject(new FileInputStream(file), HashMap.class);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return json;
	}

	/**
	 * Write a Map<String, Object> to a json file.
	 * 
	 * @param filePath
	 * @param map
	 */
	public static void writeMapToJsonFile(String filePath, Map<String, Object> map) {
		
		try (FileWriter writer = new FileWriter(filePath)){
			writer.write(JSON.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.MapSortField));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} 
	}

	
	/**
	 * Read a JSON file in a jar
	 * 
	 * @param jarPath
	 *            the path of jar
	 * @param filePath
	 *            the path of file in jar
	 * @return JSON file content
	 */
	public static String readJarJsonFile(String jarPath, String filePath) {
		String json = "", path;
		InputStream is = null;
		URL url = null;
		try {
			if (jarPath.startsWith("file:") && jarPath.lastIndexOf(".jar!") > 0) {// run in a jar
				path = "jar:" + jarPath + filePath;
			} else { // run in a jar of jar
				path = "jar:file:" + jarPath + "!/" + filePath;
			}
			url = new URL(path);
			is = url.openStream();
			json = IOUtils.toString(is, ConstantsUnicode.UTF8);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtil.closeInputStream(is);
		}
		return json;
	}

}
