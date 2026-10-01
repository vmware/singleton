/*
 * Copyright 2026 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overrides the class of the same name in singleton-i18n-patterns-core 0.5.18, which cannot read
 * its CLDR JSON files when it is loaded from a Spring Boot 4 nested jar ("nested:" code source).
 * Classes in BOOT-INF/classes take precedence over BOOT-INF/lib, so this copy is used at runtime.
 * Remove it once a patterns-core release includes the "nested:" handling.
 */
public class LocalJSONReader {

	private static Logger logger = LoggerFactory.getLogger(LocalJSONReader.class);

	/**
	 * Read a local JSON file from specific path.
	 *
	 * @param path the location of the JSON file to be read
	 * @return one string as from the file's content
	 */
	public static String readLocalJSONFile(String path) {
		String result = "";
		try {
			result = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * Read a JSON file in a jar
	 *
	 * @param jarPath  the path of jar
	 * @param filePath the path of file in jar
	 * @return JSON file content
	 */
	public static String readJarJsonFile(String jarPath, String filePath) {
		String json = "";
		try (InputStream is = new URL(toJarUrl(jarPath, filePath)).openStream()) {
			json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}

	static String toJarUrl(String jarPath, String filePath) {
		if (jarPath.startsWith("file:") && jarPath.lastIndexOf(".jar!") > 0) {
			// classic loader: file:/app.jar!/BOOT-INF/lib/x.jar!/
			return "jar:" + jarPath + filePath;
		} else if (jarPath.startsWith("nested:")) {
			// Spring Boot 4 loader: nested:/app.jar/!BOOT-INF/lib/x.jar!/
			return "jar:" + (jarPath.endsWith("!/") ? jarPath : jarPath + "!/") + filePath;
		}
		// plain jar on the file system
		return "jar:file:" + jarPath + "!/" + filePath;
	}
}
