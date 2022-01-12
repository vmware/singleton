/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * An encrypt util, the algorithm is SHA-512
 *
 */
public class EncryptUtil {
	private static Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
	private EncryptUtil() {}
    /**
     * Encrypt for a byte array to a String, and encode the String by Base64
     *
     * @param plainText A byte array
     * @return The encrypted String
     */
    public static String SHA512(byte[] plainText) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(ConstantsKeys.SHA_512_ALGORITHM);
            return Base64.encodeBase64String(messageDigest.digest(plainText));
        } catch (NoSuchAlgorithmException e) {
        	logger.error(e.getMessage(), e);
        }
        return "";
    }

}
