/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.dto;

import java.io.Serializable;

import com.vmware.vipclient.i18n.base.PatternTypeEnum;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class PatternsDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private PatternTypeEnum   patternType;
    private String            locale;

    public PatternsDTO(PatternTypeEnum patternType, String locale) {
        super();
        this.patternType = patternType;
        this.locale = locale;
    }

    /**
     * get the key for cache.
     * 
     * @return
     */
    public String getCacheKey() {
        if (patternType == null) {
            throw new VIPJavaClientException(
                    "patternType has not been initialized");
        }
        if (locale == null) {
            throw new VIPJavaClientException("locale has not been initialized");
        }
        StringBuffer key = new StringBuffer(patternType.name());
        key.append(ConstantsKeys.UNDERLINE);
        key.append(locale);
        return key.toString();
    }
}
