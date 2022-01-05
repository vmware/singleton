/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.vmware.i18n.common.Constants;

/**
 * This class is used for handling Properties file.
 */
public class PropertiesFileUtil {

    /**
     * Load a properties file from resource, e.g. the file locate in a Jar file.
     *
     * @param fileName the properties file name
     * @return Properties object which contain the file's elements
     */
    public static Properties loadFromStream(String fileName) {
        Properties p = new Properties();
        InputStream inputStream = PropertiesFileUtil.class.getClassLoader()
                .getResourceAsStream(fileName);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream, Constants.UTF8);
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
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
            inputStream = new FileInputStream(fileName);
            reader = new InputStreamReader(inputStream, Constants.UTF8);
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeReader(reader);
            IOUtil.closeInputStream(inputStream);
        }
        return p;
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
