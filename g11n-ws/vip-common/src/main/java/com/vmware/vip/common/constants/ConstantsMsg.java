/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

/**
 * Constant messages
 *
 */
public class ConstantsMsg {
   // message
   public static final String TRANS_GET_FAILD = "Failed to get translations from the bundles for %s";
   public static final String TRANS_IS_NOT_FOUND = "translation is not found";
   public static final String TRANS_IS_FOUND = "translation is found";
   public static final String EN_NOT_SOURCE =
         "The %s and returned, but the according en string is not matched to source.";
   public static final String TRANS_NOT_EN_NOT =
         "The %s , English not found, return the recieved source as translation.";
   public static final String TRANS_NOTFOUND_EN_FOUND =
         "The %s, English found, return the English as translation.";
   public static final String TRANS_FOUND_RETURN = "The %s and returned.";
   public static final String TRANS_NOTFOUND_NOTLATEST =
         "The %s or it is not latest, return the received source.";
   public static final String PSEUDO_NOTFOUND =
         "The pseudo %s, return the received source with pseudo tag.";
   public static final String PSEUDO_FOUND = "The pseudo %s.";
   public static final String PARAM_NOT_VALIDATE = "The parameter is not pass the validate";
   public static final String PATTERN_NOT_VALIDATE = "The request pattern name is invalid";
   public static final String FIFE_NOT_FOUND = "File is not existing";
   public static final String NO_PATTERN_FOUND = "Data not found, no format pattern found due to no mapping language found for region and no plural/dateFields data found if they are included in 'scope'!";
   public static final String PART_PATTERN_FOUND = "Only part of data found, either no format pattern found due to no mapping language found for region or no plural/dateFields data found if they are included in 'scope'!";
   public static final String SCOPE_FILTER_NOT_VALIDATE = "The request parameter scopeFilter is invalid, only one filtering method is supported";
   public static final String PRODUCT_OR_VERSION_MISSING = "Only one parameter of 'productName' and 'version' is provided, they should be used in pairs!";
   public static final String SOURCE_IS_NOT_PROVIDE = "Source is not provided, the existing translation is found and returned";
   public static final String IMAGE_NOT_SUPPORT_REGION = "Current request region '%s' is not support.";
   public static final String IMAGE_NOT_SUPPORT_SCALE = "Current request image scale is not support.";
   private ConstantsMsg() {
   }
}
