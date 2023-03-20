/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt.intento;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * A config class to accept the configuration settings from application properties
 */
@Configuration
public class IntentoConfigContent {
    @Value("${mt.intento.provider:####}")
    private String provider;

    @Value("${mt.intento.apikey:######}")
    private String apiKey;

    @Value("${mt.intento.category:######}")
    private String category;

    @Value("${mt.intento.transURL:######}")
    private String transURL;

    @PostConstruct
    public void initConfig() {
        IntentoConfig.setProvider(this.getProvider());
        IntentoConfig.setApiKey(this.getAPIKey());
        IntentoConfig.setCATEGORY(this.getCategory());
        IntentoConfig.setTRANSURL(this.getTransURL());
    }

    public String getProvider() {
        return provider;
    }

    public String getAPIKey() {
        return apiKey;
    }

    public String getCategory() {
        return category;
    }

    public String getTransURL() {
        return transURL;
    }

}
