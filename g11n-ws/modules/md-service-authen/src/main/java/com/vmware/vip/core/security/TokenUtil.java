/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import java.util.UUID;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * A helper util for generate token java class
 */
public class TokenUtil {

	private TokenUtil() {}
    /**
     * According to productName and version generate token string for security
     *
     * @param productName The name of product, e.g: vCG, SIM...
     * @param version Translation resource version, e.g: 1.0.0
     * @return Token string
     */
    public static String getToken(String productName, String version) {
        String token = (productName + ConstantsChar.UNDERLINE + version).hashCode() + ""
                + ConstantsKeys.VIP.hashCode() + UUID.randomUUID().toString();
        return token;
    }


}
