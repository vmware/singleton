/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;


public class FileUtil {
	static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static Properties readPropertiesFile(String filePath) throws IOException {
		URI uri = findFile(filePath);
		if(null == uri) {
			throw new FileNotFoundException("Can't find file: "+filePath);
		}
		
		final Properties props = new Properties();		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8));) {
			props.load(reader);
		}
		return props;
	}

	public static JSONObject readJSONFile(String filePath) throws ParseException, IOException {
		URI uri = findFile(filePath);
		if(null == uri) {
			throw new FileNotFoundException("Can't find file: "+filePath);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8));) {
			return (JSONObject) new JSONParser().parse(reader);
		} 
	}
	
	public static URI findFile(String filePath) {
		// File exists, return directly.
		File fileObj = new File(filePath);
		if (fileObj.exists()) {
			return fileObj.toPath().toUri();
		}
		
		// a path of file in jar
		if (filePath.contains("jar:") && filePath.contains(".jar!")) {
			try {
				return new URI(filePath);
			} catch (URISyntaxException e) {
				throw new VIPJavaClientException("Invalid path: "+ filePath, e);
			}
		}

		// File doesn't exist. Let java to find it
		URL url = ClassLoader.getSystemResource(filePath);
		try {
			return url != null ? url.toURI() : null;
		} catch (URISyntaxException e) {}
		
		return null;
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
