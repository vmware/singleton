/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtil {
    static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    
    public static JSONObject readJson(Path path)  {
        JSONObject jsonObj = null;
        try (InputStream is = Files.newInputStream(path);
    			Reader reader = new InputStreamReader(is, "UTF-8");){
			jsonObj = new JSONObject(new JSONTokener(reader));
		} catch (Exception e) {
			logger.error("Failed to read json file " + path, e);
		}
        
        return jsonObj;
    }
    
    @Deprecated
    public static JSONObject readJarJsonFile(String jarPath, String filePath) {
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
                    Reader reader = new InputStreamReader(fis, "UTF-8");) {
            	jsonObj = new JSONObject(new JSONTokener(reader));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            logger.error(e1.getMessage());
        }

        return jsonObj;
    }
    
    @Deprecated
    public static JSONObject readLocalJsonFile(String filePath) {
        String path = FileUtil.class.getClassLoader()
                .getResource(filePath).getFile();
        JSONObject jsonObj = null;
        File file = new File(path);
        if (file.exists()) {
            try (InputStream fis = new FileInputStream(file);
                    Reader reader = new InputStreamReader(fis, "UTF-8");) {
            	jsonObj = new JSONObject(new JSONTokener(reader));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return jsonObj;
    }

    public static List<URI> getAllResources(Path path) {
        List<URI> uris = new LinkedList<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path.toString());
            while(urls.hasMoreElements()) {
                uris.add(urls.nextElement().toURI());
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return uris;
    }
}
