package com.vmware.vip.test.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.vmware.vip.test.javaclient.Constants;

public class Config {
	private static Config config = null;
	private static final String CONFIG_ENCODEING = "UTF-8";
	private  Properties properties;
	private String configFile = Constants.CONFIG_FILE_PATH;
	private void readConfigFile()
	{
		FileInputStream configFileInputStream;
		InputStreamReader inputStreamReader;

		try {
			configFileInputStream = new FileInputStream(configFile);
			inputStreamReader = new InputStreamReader(configFileInputStream, CONFIG_ENCODEING);
			properties = new Properties();
			properties.load(inputStreamReader);
		} catch (FileNotFoundException e) {
			System.out.println("config file cannot be found in "+configFile);
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Config file "+configFile+" not support encoding "+ CONFIG_ENCODEING);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	private Config() {
		readConfigFile();
	}
	public static synchronized Config getInstance() {
		if (config == null)
			config = new Config();
		return config;
	}
	public String get(String key) {
		return config.properties.getProperty(key);
	}
	public List<String> getList(String key) {
		String value = get(key);
		List<String> list = new ArrayList<String>();
		if (value!=null && !value.trim().equals("")) {
			for (String item : value.split(Constants.CONF_LIST_SEPERATER)){
				list.add(item.trim());
			}
			return list;
		}
		return null;
	}
}
