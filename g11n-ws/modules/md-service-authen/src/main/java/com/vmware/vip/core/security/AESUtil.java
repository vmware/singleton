/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;

/**
 * An encrypted and decryption tool class, the algorithm is AES
 */
public class AESUtil {
private static Logger logger = LoggerFactory.getLogger(AESUtil.class);
    /**
     * The encryption method by AES
     *
     * @param data Data to be encrypted
     * @param key AES key (must be 16 bytes)
     * @return The encrypted string
     */
    public static String encrypt(String data, String key) {
        if (key.length() < 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            Cipher cipher = Cipher.getInstance(ConstantsKeys.AES_ALGORITHM);
            byte[] byteContent = data.getBytes(ConstantsUnicode.UTF8);
            cipher.init(Cipher.ENCRYPT_MODE, genKey(key));
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
           
        }
        return null;

    }

    /**
     * The decryption method by AES
     *
     * @param data Data to be decrypted
     * @param key AES key (must be 16 bytes)
     * @return The decrypted string
     */
    public static String decrypt(String data, String key) {
        if (key.length() < 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        try {
            byte[] decryptFrom = parseHexStr2Byte(data);
            Cipher cipher = Cipher.getInstance(ConstantsKeys.AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, genKey(key));
            byte[] result = cipher.doFinal(decryptFrom);
            return new String(result, ConstantsUnicode.UTF8);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Method to generate the SecretKeySpec
     *
     * @param key A fixed string
     * @return SecretKeySpec object
     */
    private static SecretKeySpec genKey(String key) {
        SecretKeySpec secretKey;
        try {
            secretKey = new SecretKeySpec(key.getBytes(ConstantsUnicode.UTF8),
                    ConstantsKeys.AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, ConstantsKeys.AES_ALGORITHM);
            return seckey;
        } catch (UnsupportedEncodingException e) {
        	logger.error(e.getMessage(), e);
            throw new RuntimeException("genKey fail!", e);
        }
    }

    /**
     * Convert byte array data to hex String
     *
     * @param buf Byte array
     * @return The converted String
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Convert hex String to byte array
     *
     * @param hexStr Hex String
     * @return The converted data
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
