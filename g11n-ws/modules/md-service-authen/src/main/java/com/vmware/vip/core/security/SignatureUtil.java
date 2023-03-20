/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsUnicode;

/**
 * Generate signature for vIP-Server securit, the signature will be responsed to product client,
 * and product client verify it
 */
public class SignatureUtil {
   private static Logger logger = LoggerFactory.getLogger(SignatureUtil.class);
    /**
     * A helper util for generate signature
     *
     * @param paramMap The parameters for generation signature
     * @return signature string
     */
    public static String sign(LinkedHashMap<String, String> paramMap) {
        if (null == paramMap) {
            throw new RuntimeException("signature parameter can't null");
        }
        StringBuilder paramStr = new StringBuilder();
        for (String key : paramMap.keySet()) {
            paramStr.append(key);
            paramStr.append(paramMap.get(key));
        }
        try {
            String signatureStr = EncryptUtil.SHA512(paramStr.toString().getBytes(
                    ConstantsUnicode.UTF8));
            return signatureStr.toUpperCase();
        } catch (UnsupportedEncodingException e) {
        	logger.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Encapsulation sessionId and token into map and sign it to generate signature
     *
     * @param sessionId After product client certification through,the vIP-Server returned to
     *        product client's parameters 'sessionId'
     * @param token After product client certification through,the vIP-Server returned to
     *        product client's parameters 'token'
     * @return signature string
     */
    public static String sign(String sessionId, String token) {
        if (StringUtils.isEmpty(sessionId)) {
            throw new RuntimeException("sessionId parameter can't null");
        }
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token parameter can't null");
        }
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<String, String>();
        paramMap.put("sessionId", sessionId);
        paramMap.put("token", token);
        return sign(paramMap);
    }
}
