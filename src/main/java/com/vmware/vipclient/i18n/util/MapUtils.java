/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * Map operation related tools
 *
 */
public class MapUtils {

    /**
     * get entry of the value is minimum in map.
     * 
     * @param map
     *            the original map
     * @return the entry of the value is minimum.
     */
    public static Entry<String, Integer> getMinValueItem(Map<String, Integer> map) {
        Entry<String, Integer> minValueEntry = null;
        if (null == map || map.size() == 0) {
            return minValueEntry;
        }
        minValueEntry = map.entrySet().iterator().next();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (minValueEntry.getValue() > entry.getValue()) {
                minValueEntry = entry;
            }
        }
        return minValueEntry;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("A", 99);
        map.put("B", 67);
        map.put("C", 67);
        map.put("D", 87);
        System.out.println(MapUtils.getMinValueItem(map));
    }
}
