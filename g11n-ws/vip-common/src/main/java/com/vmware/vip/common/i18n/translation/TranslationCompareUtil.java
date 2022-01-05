/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.translation;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 *
 * @author shihu
 *
 */
public class TranslationCompareUtil {
	private TranslationCompareUtil() {
	}

	/**
	 * compare the two component messages
	 * 
	 * @param msg1
	 * @param msg2
	 * @return
	 */
	public static Map<String, String> compareComponentMessage(
			Map<String, Object> m1, Map<String, Object> m2) {
		Map<String, String> p = new HashMap<String, String>();
		for (Entry<String, Object> entry : m1.entrySet()) {
			String k1 = entry.getKey();
			String v1 = entry.getValue() == null ? "" : (String) entry
					.getValue();
			String v2 = m2.get(k1) == null ? "" : (String) m2.get(k1);
			if (v1.equals(v2)) {
				p.put(k1, "1");
			} else {
				p.put(k1, "0");
			}
		}
		return p;
	}

}
