/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.json.JSONException;

public class JSONUtil {

    /**
     * Convert Json string to Map<String, Object>
     * @param json
     * @return Map<String, Object> obj
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJson(String json) {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = getContainerFactory();
        Map<String, Object> result = null;
        try {
            result = (Map<String, Object>) parser.parse(json, containerFactory);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static ContainerFactory getContainerFactory() {
        ContainerFactory containerFactory = new ContainerFactory() {
            public List<Object> creatArrayContainer() {
                return new LinkedList<Object>();
            }

            public Map<String, Object> createObjectContainer() {
                return new LinkedHashMap<String, Object>();
            }
        };
        return containerFactory;
    }

    /**
     * Get the node value of JSON string. e.g. main.locale.day
     * @param jsonObj JSONObject
     * @param keyPath JSON node path
     * @return the node value
     */
    public static Object select(JSONObject jsonObj, String keyPath) {
        if (null == jsonObj || null == keyPath) {
            return null;
        }
        String[] patharr = keyPath.split("\\.");
        JSONObject current = jsonObj;
        Object retvalue = null;
        for (int i = 0; i < patharr.length; i++) {
            String key = patharr[i];
            retvalue = current.get(key);
            if (i < (patharr.length - 1)) {
                current = (JSONObject) retvalue;
            }
        }
        return retvalue;
    }

    /**
     * Convert JSON string to JSONObject
     * @param jsonStr
     * @return JSONObject
     */
    public static JSONObject string2JSON(String jsonStr) {
        JSONObject genreJsonObject = null;
        try {
            genreJsonObject = (JSONObject) JSONValue.parseWithException(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return genreJsonObject;
    }

	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> string2SortMap(String jsonStr) {
		Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.toLowerCase().compareTo(o2.toLowerCase()) == 0) {
					return 1;// Avoid being covered, such as h and H
				}
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		});
		try {
			Map<String, Object> genreJsonObject = (Map<String, Object>) JSONValue.parseWithException(jsonStr);
			sortMap.putAll(genreJsonObject);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sortMap;
	}

}
