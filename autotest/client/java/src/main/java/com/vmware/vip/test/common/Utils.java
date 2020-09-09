/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.json.JSONObject;

public class Utils {
	public static Properties loadProperties(String propertyFile) {
		Properties prop = null;
		InputStream fInputStream = null;
		try {
			fInputStream = new FileInputStream(propertyFile);
			prop = new Properties();
			prop.load(fInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}

	public static JSONObject readJSONObjFromFile(String filePath) {
		JSONObject obj = null;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(filePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			String content = "";
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				content = content + line;
			}
			bufferedReader.close();
			obj = new JSONObject(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static String removeFileExtension(String filename) {
		if (filename.contains(".")) {
			return filename.substring(0, filename.lastIndexOf("."));
		}
		return filename;
	}
	
	public static String getFileName(String filePath) {
		String fileSeparater = System.getProperty("file.separator");
		return filePath.substring(filePath.lastIndexOf(fileSeparater)+1, filePath.length());
	}

	public static <T> T getRandomItem(List<T> list) {
		Random random = new Random();
		int index = random.nextInt(list.size()-1);
		return list.get(index);
	}
}
