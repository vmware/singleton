/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

/**
 * Constant messages
 *
 */
public class ConstantsMsg {
   // message
   public static final String TRANS_IS_NOT_FOUND = "translation is not found";
   public static final String TRANS_IS_FOUND = "translation is found";
   public static final String EN_NOT_SOURCE =
         "The {0} and returned, English is not equal to recieved source.";
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
   public static final String FIFE_NOT_FOUND= "File is not existing";
   public static final String SCOPE_FILTER_NOT_VALIDATE = "The request parameter scopeFilter is invalid, only one filtering method is supported";

   private ConstantsMsg() {
   }
}
