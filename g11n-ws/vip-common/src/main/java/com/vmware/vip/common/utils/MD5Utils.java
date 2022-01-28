/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * MD5 encryption tools
 *
 */
public class MD5Utils {
	private static Logger logger = LoggerFactory.getLogger(MD5Utils.class);
    /**
     * MD5 32bit Encrypt Methods. 
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string, such as "821f03288846297c2cf43c34766a38f7"
     *
     */ 
	public static String getHex32Str(String readyEncryptStr) {
		String hex32Str = "";
		if (!StringUtils.isEmpty(readyEncryptStr)) {
			try {
				MessageDigest md = MessageDigest.getInstance(ConstantsKeys.MD5);
				md.update(readyEncryptStr.getBytes());
				byte[] b = md.digest();
				StringBuilder su = new StringBuilder();
				for (int offset = 0, bLen = b.length; offset < bLen; offset++) {
					String haxHex = Integer.toHexString(b[offset] & 0xFF);
					if (haxHex.length() < 2) {
						su.append("0");
					}
					su.append(haxHex);
				}
				hex32Str = su.toString();
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return hex32Str;
	}

    /**
     * MD5 16bit Encrypt Methods.
     *
     * @param readyEncryptStr ready encrypt string
     * @return String encrypt result string, such as "8846297c2cf43c34"
     *
     */ 
	public static String getHex16Str(String readyEncryptStr) {
		return StringUtils.isEmpty(readyEncryptStr) ? null : getHex32Str(readyEncryptStr).substring(8, 24); 
	}

	public static void main(String[] args) {
		System.out.println(getHex32Str("book"));
		System.out.println(getHex16Str("book"));
	}
}
