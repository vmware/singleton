/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.i18n.common.Constants;

/**
 * This Class represents a Reader to read local JSON file
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
            result = FileUtils.readFileToString(new File(path), Constants.UTF8);
        } catch (Exception e) {
			logger.error(e.getMessage());
        }
        return result;
    }

    /**
     * Read a JSON file in a jar
     * @param jarPath the path of jar
     * @param filePath the path of file in jar
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
            json = IOUtils.toString(is, Constants.UTF8);
        } catch (Exception e) {
			logger.error(e.getMessage());
        } finally {
            IOUtil.closeInputStream(is);
        }
        return json;
    }
}
