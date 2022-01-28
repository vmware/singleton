/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This class is used for handling Properties file.
 * 
 */

public class PropertiesFileUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesFileUtil.class);

    /**
     * Load a properties file from resource, e.g. the file locate in a Jar file.
     *
     * @param fileName the properties file name
     * @return Properties object which contain the file's elements
     */
    public static Properties loadFromStream(String fileName) {
        Properties p = new Properties();
        InputStream inputStream = PropertiesFileUtil.class.getClassLoader().getResourceAsStream(
                fileName);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream, ConstantsUnicode.UTF8);
            p.load(reader);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtil.closeReader(reader);
            IOUtil.closeInputStream(inputStream);
        }
        return p;
    }

    /**
     * Load a properties file from system-dependent file.
     *
     * @param fileName the system-dependent file name
     * @return Properties object which contain the file's elements
     */
    public static Properties loadFromFile(String fileName) {
        Properties p = new Properties();
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = new FileInputStream(PathUtil.filterPathForSecurity(fileName));
            reader = new InputStreamReader(inputStream, ConstantsUnicode.UTF8);
            p.load(reader);
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        } finally {
            IOUtil.closeReader(reader);
            IOUtil.closeInputStream(inputStream);
        }
        return p;
    }

    /**
     * Load a properties file from a URL.
     *
     * @param url the location of the properties file
     * @return Properties object which contain the file's elements
     */
    public static Properties loadFromURL(String url) {
        Properties pro = new Properties();
        InputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = HTTPRequester.createConnection(new URL(url)).getInputStream();
            reader = new InputStreamReader(inputStream, ConstantsUnicode.UTF8);
            pro.load(reader);
        } catch (MalformedURLException e) {
        	logger.error(e.getMessage(), e);
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        } finally {
            IOUtil.closeReader(reader);
            IOUtil.closeInputStream(inputStream);
        }
        return pro;
    }

    /**
     * Get a value from properties object.
     *
     * @param p the properties object contains the value
     * @param key used for getting the value
     * @return Object as the value
     */
    public static Object getPropertyValue(Properties p, String key) {
        return p.get(key);
    }

}
