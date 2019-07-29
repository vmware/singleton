package com.vmware.vip.test.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
}
