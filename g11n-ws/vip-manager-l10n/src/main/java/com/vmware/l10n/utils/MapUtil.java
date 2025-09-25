/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import org.springframework.util.StringUtils;

public class MapUtil {

	/*
	 * update the messages map with key/value with source format, e.g. "aa.bb"
	 * will be updated as "aa.bb.#HTML" if the new key contains source format
	 * '.#HTML'.
	 */
	public static void updateKeyValue(Map<String, Object> messages, String key,
			Object value) {
		String updatedTrimKey = "";
		if (StringUtils.isEmpty(key)) {
			return;
		} else {
			updatedTrimKey = getTrimKey(key);
		}
		Iterator<String> it = messages.keySet().iterator();
		boolean isNew = true;
		while (it.hasNext()) {
			String originalKey = it.next();
			String originalTrimKey = getTrimKey(originalKey);
			if (updatedTrimKey.equals(originalTrimKey)) {
				if (originalKey.equals(key)) { // Source Format not changed.
					messages.put(key, value);
				} else { // Source Format changed.
					messages.remove(originalKey);
					messages.put(key, value);
				}
				isNew = false;
				break;
			}
		}
		if (isNew) {
			// new key
			messages.put(key, value);
		}
	}
	
	public static void updateKeyValue(JSONObject messages, String key,
			Object value) {
		String updatedTrimKey = "";
		if (StringUtils.isEmpty(key)) {
			return;
		} else {
			updatedTrimKey = getTrimKey(key);
		}
		Iterator<String> it = messages.keySet().iterator();
		boolean isNew = true;
		while (it.hasNext()) {
			String originalKey = it.next();
			String originalTrimKey = getTrimKey(originalKey);
			if (updatedTrimKey.equals(originalTrimKey)) {
				if (originalKey.equals(key)) { // Source Format not changed.
					messages.put(key, value);
				} else { // Source Format changed.
					messages.remove(originalKey);
					messages.put(key, value);
				}
				isNew = false;
				break;
			}
		}
		if (isNew) {
			// new key
			messages.put(key, value);
		}
	}

	/*
	 * get the trim key, e.g. get "a.b.c" from "a.b.c.#HTML".
	 */
	private static String getTrimKey(String key) {
		String updatedTrimKey = "";
		int index = key.lastIndexOf(".#");
		if (index > 0) {
			updatedTrimKey = key.substring(0, index);
		} else {
			updatedTrimKey = key;
		}
		return updatedTrimKey;
	}

}
