/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.gcs.util;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;

public class GcsUtils {
   public static final String GCS_L10N_BUNDLES_PATH =
         "l10n" + ConstantsChar.BACKSLASH + "bundles" + ConstantsChar.BACKSLASH;

   private GcsUtils() {
   }

   /**
    * generate the product version path
    */
   public static String genProductVersionGcsPath(String productName, String version) {
      StringBuilder path = new StringBuilder();
      path.append(GCS_L10N_BUNDLES_PATH);
      path.append(productName);
      path.append(ConstantsChar.BACKSLASH);
      path.append(version);
      path.append(ConstantsChar.BACKSLASH);
      return path.toString();

   }

   /**
    * get the locale by message file name
    */
   public static String getLocaleByFileName(String fileName) {
      String locale = null;
      if (fileName.endsWith(ConstantsFile.FILE_TPYE_JSON)
            && (!fileName.endsWith(ConstantsFile.CREATION_INFO))
            && (!fileName.endsWith(ConstantsFile.VERSION_FILE))) {
         locale = fileName.substring(fileName.indexOf(ConstantsFile.LOCAL_FILE_SUFFIX) + 8,
               fileName.lastIndexOf(ConstantsChar.DOT));
         if (!locale.equals(ConstantsChar.EMPTY)) {
            locale = locale.replaceFirst(ConstantsChar.UNDERLINE, ConstantsChar.EMPTY);
         } else {
            locale = ConstantsUnicode.EN;
         }
      }
      return locale;
   }

}
