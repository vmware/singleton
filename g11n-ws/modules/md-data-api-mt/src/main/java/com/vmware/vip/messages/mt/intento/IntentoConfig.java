/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt.intento;

/**
 * A config class to define the settings for requesting Intento MT
 */
public class IntentoConfig {

    public static final int INTERVAL = 4000;

    public static final int RETRY = 10;

    public static String PROVIDER = "";

    public static String API_KEY = "";

    public static String CATEGORY = "";

    public static String TRANSURL = "";

    private IntentoConfig() {
    }

    public static void setProvider(String provider) {
        PROVIDER = provider;
    }

    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }

    public static void setCATEGORY(String category) {
        IntentoConfig.CATEGORY = category;
    }

    public static void setTRANSURL(String transURL) {
        IntentoConfig.TRANSURL = transURL;
    }
}
