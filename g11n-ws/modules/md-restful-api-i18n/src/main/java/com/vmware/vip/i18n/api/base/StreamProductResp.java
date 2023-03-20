/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.nio.ByteBuffer;
import java.util.List;

public class StreamProductResp {
    private static String fallbackStateStr = "{\r\n  \"response\": {\r\n    \"code\": 604,\r\n    \"message\": \"The content of response have been version fallback\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";

    private static String partSuccStateStr = "{\r\n  \"response\": {\r\n    \"code\": 207,\r\n    \"message\": \"Part of the translation is available\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";

    private static String succStateStr = "{\r\n  \"response\": {\r\n    \"code\": 200,\r\n    \"message\": \"OK\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": {\r\n";

    private static byte[] endStr = " ],\r\n    \"url\": \"\",\r\n    \"id\": 0\r\n  }\r\n}".getBytes();

    private String productName;
    private String version;
    private String dataOrigin = "";
    private String pseudo = "false";
    private boolean machineTranslation = false;
    private List<String> locales;
    private List<String> components;

    public StreamProductResp(String productName, String version,
                             List<String> locales, List<String> components, String pseudo, boolean machineTranslation) {
        this.productName = productName;
        this.version = version;
        if (dataOrigin != null) {
            this.pseudo = pseudo;
        }
        this.machineTranslation = machineTranslation;
        this.locales = locales;
        this.components = components;
    }

    public ByteBuffer getRespStartBytes(int respNumb) {
        return ByteBuffer.wrap(getParamStr(respNumb).getBytes());
    }

    public ByteBuffer getEndBytes() {
        return ByteBuffer.wrap(endStr);
    }

    private String getParamStr(int respNumb) {
        StringBuilder paramBuilder = new StringBuilder();

        switch (respNumb) {
            case 200:
                paramBuilder.append(succStateStr);
                break;

            case 207:
                paramBuilder.append(partSuccStateStr);
                break;

            default:
                paramBuilder.append(fallbackStateStr);
                break;
        }

        paramBuilder.append("    \"productName\": \"" + this.productName + "\",\r\n");
        paramBuilder.append("    \"version\": \"" + this.version + "\",\r\n");
        paramBuilder.append("    \"dataOrigin\": \"" + this.dataOrigin + "\",\r\n");
        paramBuilder.append("    \"pseudo\": " + this.pseudo + ",\r\n");
        paramBuilder.append("    \"machineTranslation\": " + String.valueOf(this.machineTranslation) + ",\r\n");
        getLocalesStr(paramBuilder);
        getComponentsStr(paramBuilder);
        paramBuilder.append("    \"bundles\": [\r\n");
        return paramBuilder.toString();
    }

    private void getLocalesStr(StringBuilder paramBuilder) {
        paramBuilder.append("    \"locales\": [ ");
        int index = 0;

        for (String localStr : this.locales) {
            if (index != 0) {
                paramBuilder.append(",");

            }
            index++;
            paramBuilder.append("\"");
            paramBuilder.append(localStr);
            paramBuilder.append("\"");


        }
        paramBuilder.append(" ],\r\n");

    }

    private void getComponentsStr(StringBuilder paramBuilder) {
        paramBuilder.append("    \"components\": [ ");
        int index = 0;
        for (String compStr : this.components) {
            if (index != 0) {
                paramBuilder.append(",");

            }
            index++;
            paramBuilder.append("\"");
            paramBuilder.append(compStr);
            paramBuilder.append("\"");
        }
        paramBuilder.append(" ],\r\n");
    }


}
