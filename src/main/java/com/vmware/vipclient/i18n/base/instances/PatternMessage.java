/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import java.util.Locale;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.messages.service.PatternService;

/**
 * provide api to get pattern data from remote or locale
 */
public class PatternMessage implements Message {

    public PatternMessage() {
        super();
    }

    /**
     * get whole pattern data for formatting
     * 
     * @param locale
     * @return
     */
    public JSONObject getPatternMessage(Locale locale) {
        return new PatternService().getPatterns(locale.toLanguageTag());
    }

    /**
     * get whole pattern data for formatting
     * 
     * @param locale
     * @return
     */
    public JSONObject getPatternMessage(String language, String region) {
        return new PatternService().getPatterns(language, region);
    }
}
