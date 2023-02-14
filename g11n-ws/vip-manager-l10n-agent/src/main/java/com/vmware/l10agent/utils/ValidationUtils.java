/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import com.vmware.vip.common.utils.RegExpValidatorUtils;

public class ValidationUtils {

    private ValidationUtils(){}

    public static boolean validateProductName(String productName) {
        return RegExpValidatorUtils.IsLetterOrNumber(productName);
    }

    public static boolean validateVersion(String version){
        return RegExpValidatorUtils.IsNumberAndDot(version);
    }
}
