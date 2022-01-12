/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.common.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.utils.HTTPRequester;
import com.vmware.vip.common.utils.PropertiesFileUtil;
import com.vmware.vip.fetcher.common.dto.OrderedProperties;

public class OrderedPropertiesUtils {
    public static OrderedProperties loadFromStream(String fileName) {
        OrderedProperties p = new OrderedProperties();
        InputStream inputStream = PropertiesFileUtil.class.getClassLoader().getResourceAsStream(
                fileName);
        try {
            p.load(new InputStreamReader(inputStream, ConstantsUnicode.UTF8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static OrderedProperties loadFromFile(String fileName) {
        OrderedProperties p = new OrderedProperties();

        try {
          
            p.load(new InputStreamReader(new FileInputStream(fileName), ConstantsUnicode.UTF8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static OrderedProperties loadFromURL(String url) {
        OrderedProperties pro = new OrderedProperties();
        try {
            InputStream inputStream = HTTPRequester.createConnection(new URL(url)).getInputStream();
            pro.load(new InputStreamReader(inputStream, ConstantsUnicode.UTF8));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pro;
    }

    public static Object getPropertyValue(Properties p, String key) {
        return p.get(key);
    }

    public static Map getOrderedMapFromProp(OrderedProperties orderedPro) {
        Map map = new LinkedHashMap();
        Enumeration en = orderedPro.keys();
        while (en.hasMoreElements()) {
            Object key = en.nextElement();
            if ("[ERROR]".equals(key)) {
                System.out.println("key:" + key);
                System.out.println("value:" + orderedPro.get(key));
            }
            map.put(key, orderedPro.get(key));
        }
        return map;
    }

    public static Map getOrderedMapFromPropForPseudo(OrderedProperties orderedPro, String pseudoTag) {
        Map map = new LinkedHashMap();
        Enumeration en = orderedPro.keys();
        while (en.hasMoreElements()) {
            Object key = en.nextElement();
            // map.put(key, "@@"+orderedPro.get(key)+"@@");
            map.put(key, pseudoTag + orderedPro.get(key) + pseudoTag);
        }
        return map;
    }

    /**
     * 
     * @param fileName1: absolute path
     * @param fileName2
     * @return
     */
    public static OrderedProperties mergeOrderedProperties(String fileName1, String fileName2) {
        OrderedProperties op1 = loadFromFile(fileName1);
        OrderedProperties op2 = loadFromFile(fileName2);
        op1.putAll(op2);
        return op1;
    }

    public static OrderedProperties mergeOrderedProperties(OrderedProperties op1,
            OrderedProperties op2) {
        op1.putAll(op2);
        return op1;
    }

}
