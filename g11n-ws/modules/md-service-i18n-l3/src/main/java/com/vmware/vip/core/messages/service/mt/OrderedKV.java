/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.mt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class for store key list and value list from a map, the value list will be
 * sent for MT translation.
 * 
 * @author linr
 *
 */
public class OrderedKV {
	List<String> keys = new ArrayList<String>();
	List<String> values = new ArrayList<String>();

	public OrderedKV(Map<String, Object> messages) {
		Set<String> s = messages.keySet();
		Iterator<String> it = s.iterator();
		while (it.hasNext()) {
			String k = it.next();
			keys.add(k);
			String v = (String) messages.get(k);
			values.add(v);
		}
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
}
