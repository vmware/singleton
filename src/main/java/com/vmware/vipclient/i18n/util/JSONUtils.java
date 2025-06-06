/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtils {
    static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJson(String json) {
        Map<String, Object> result = null;
        if (json != null && !"".equals(json)) {
            try {
            	JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.toMap();
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public static Map<String, String> map2SortMap(Map<String, String> jsonMap) {
        if (jsonMap == null || jsonMap.size() == 0) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        sortMap.putAll(jsonMap);
        return sortMap;
    }

    public static boolean isEmpty(String json) {
        return json == null || "".equals(json) || "{}".equals(json);
    }
}
