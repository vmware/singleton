/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtil {
	static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static Properties readPropertiesFile(String file) throws IOException {
		final Properties props = new Properties();
		try (InputStream is = ClassLoader.getSystemResourceAsStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
			props.load(reader);
		}
		return props;
	}

	public static JSONObject readJSONFile(String file) throws ParseException, IOException {
		try ( InputStream is = ClassLoader.getSystemResourceAsStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
			return (JSONObject) new JSONParser().parse(reader);
		} 
	}	

	public static JSONObject readJarJsonFile(String jarPath, String filePath){
		JSONObject jsonObj = null;
		URL url = null;
		String path;
		if (jarPath.startsWith("file:")
				&& jarPath.lastIndexOf(".jar!") > 0) {
			path = "jar:" + jarPath + filePath;
		} else {
			path = "jar:file:" + jarPath + "!/" + filePath;
		}

		try {
			url = new URL(path);

			try (InputStream fis = url.openStream();
					Reader reader = new InputStreamReader(fis, "UTF-8");){

				Object o = new JSONParser().parse(reader);
				if (o != null) {
					jsonObj = (JSONObject) o;
				}
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		} catch (MalformedURLException e1) {
			logger.error(e1.getMessage());
		}

		return jsonObj;
	}

	public static JSONObject readLocalJsonFile(String filePath){
		String basePath = FileUtil.class.getClassLoader()
				.getResource("").getFile();
		JSONObject jsonObj = null;
		File file = new File(basePath+filePath);
		if (file.exists()) {
			try(InputStream fis = new FileInputStream(file);	
					Reader reader = new InputStreamReader(fis, "UTF-8");) {
				Object o = new JSONParser().parse(reader);
				if (o != null) {
					jsonObj = (JSONObject) o;
				}
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}

		return jsonObj;
	}
}
