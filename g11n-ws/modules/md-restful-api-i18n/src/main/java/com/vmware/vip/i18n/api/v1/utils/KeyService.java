/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.AuthenKeyDTO;

/**
 * Generate Key for security, when product client request vIP-server API must carry this key,
 * vIP-server will verify it
 */
public class KeyService {
    private KeyService() {}
    private static Logger LOGGER = LoggerFactory.getLogger(KeyService.class);

    /**
     * Generate key for product client access vIP-Server API
     *
     * @param keyDTO Encapsulate parameters
     * @return key object
     */
    public static AuthenKeyDTO generateKey(AuthenKeyDTO keyDTO) {
        StringBuilder data = new StringBuilder();
        data.append(keyDTO.getProductName().toLowerCase());
        data.append(ConstantsKeys.VIP);
        data.append(keyDTO.getVersion());
        data.append(ConstantsKeys.VIP);
        data.append(keyDTO.getUserID());
        String decryptData = AESUtil.encrypt(data.toString(), ConstantsKeys.AES_KEY);
        if (StringUtils.isEmpty(decryptData)) {
            return null;
        }
        keyDTO.setKey(decryptData);
        LOGGER.info("The key is {}", decryptData);
        return keyDTO;
    }

    /**
     * Verify the security key
     *
     * @param keyDTO Encapsulate parameters
     * @return The verify result
     */
    public static Boolean  validateKey(AuthenKeyDTO keyDTO) {
        String data = AESUtil.decrypt(keyDTO.getKey(), ConstantsKeys.AES_KEY);
        String[] datas = data.split(ConstantsKeys.VIP);
        if(datas.length != 3){
            return false;
        }
        if (null == datas[0] || !datas[0].equals(keyDTO.getProductName())) {
            return false;
        }
        if (null == datas[1] || !datas[1].equals(keyDTO.getVersion())) {
            return false;
        }
        if (null == datas[2] || !datas[2].equals(keyDTO.getUserID())) {
            return false;
        }
        return true;
    }
}
