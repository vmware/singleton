/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

public enum TransWithPatternDataScope {
   TRANSLATION_PATTERN_WITH_REGION(1), TRANSLATION_PATTERN_NO_REGION(2), ONLY_PATTERN_WITH_REGION(
         3), ONLY_PATTERN_NO_REGION(4);

   private int value;

   private TransWithPatternDataScope(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }


}
