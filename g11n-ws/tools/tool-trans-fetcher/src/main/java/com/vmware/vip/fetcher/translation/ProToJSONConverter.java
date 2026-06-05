/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.translation;

import java.util.Enumeration;
import java.util.Properties;

import org.json.JSONObject;

/**
 * Convert key/value pairs in properties file to JSON format
 */
public class ProToJSONConverter {

	/**
	 * Convert Propeties object to JSON format
	 *
	 * @param pro The properties object which you want to convert to JSON format
	 * @return JSONObject
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public JSONObject getJSONFromProp(Properties pro){
		JSONObject pairs = new JSONObject();
		Enumeration en = pro.keys();
		while(en.hasMoreElements()) {
			Object key = en.nextElement();
			pairs.put((String) key, pro.get(key));
		}
		return pairs;
	}

}
