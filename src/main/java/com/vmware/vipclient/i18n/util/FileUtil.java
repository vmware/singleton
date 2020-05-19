/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    
    public static JSONObject readJson(Path path)  {
        JSONObject jsonObj = null;
        try (InputStream is = Files.newInputStream(path);
    			Reader reader = new InputStreamReader(is, "UTF-8");){
			jsonObj = (JSONObject) (new JSONParser().parse(reader));
		} catch (Exception e) {
			logger.error("Failed to read json file " + path);
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

                Object o = new JSONParser().parse(reader);
                if (o != null) {
                    jsonObj = (JSONObject) o;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            logger.error(e1.getMessage());
        }

        return jsonObj;
    }
    
    @Deprecated
    public static JSONObject readLocalJsonFile(String filePath) {
        String basePath = FileUtil.class.getClassLoader()
                .getResource("").getFile();
        JSONObject jsonObj = null;
        File file = new File(basePath + filePath);
        if (file.exists()) {
            try (InputStream fis = new FileInputStream(file);
                    Reader reader = new InputStreamReader(fis, "UTF-8");) {
                Object o = new JSONParser().parse(reader);
                if (o != null) {
                    jsonObj = (JSONObject) o;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return jsonObj;
    }
    
    public static Path getPath(Path path) throws URISyntaxException, IOException {
    	URI uri = Thread.currentThread().getContextClassLoader().
				getResource(path.toString()).toURI();

    	if (uri.getScheme().equals("jar")) {
			FileSystem fileSystem = null;
			try {
				fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
			} catch (FileSystemAlreadyExistsException e) {
				fileSystem = FileSystems.getFileSystem(uri);
			}
			path = fileSystem.getPath(path.toString());
		} else {
			path = Paths.get(uri);
		}
    
		return path;
    }

}
