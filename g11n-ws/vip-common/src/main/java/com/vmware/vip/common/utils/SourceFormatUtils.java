/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import org.apache.commons.codec.binary.Base64;

public class SourceFormatUtils {

    private SourceFormatUtils() {}

    public static boolean isBase64Encode(String sourceFormatUpper){
        return sourceFormatUpper.contains(ConstantsKeys.SOURCE_FORMAT_BASE64);
    }

    public static String formatSourceFormatStr(String sourceFormatUpper){
       return sourceFormatUpper.replace(ConstantsKeys.SOURCE_FORMAT_BASE64,"").replace(ConstantsChar.COMMA, "");
    }

    public static String decodeSourceBase64Str(String sourceBase64Str){
        return new String(Base64.decodeBase64(sourceBase64Str));
    }
}
