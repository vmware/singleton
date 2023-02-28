/**
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;

public class S3Utils {
   private S3Utils() {
   }

   public static final String S3_L10N_BUNDLES_PATH =
         "l10n" + ConstantsChar.BACKSLASH + "bundles" + ConstantsChar.BACKSLASH;

   /**
    * generate the product version path
    */
   public static String genProductVersionS3Path(String productName, String version) {
      StringBuilder path = new StringBuilder();
      path.append(S3_L10N_BUNDLES_PATH);
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

   /**
    * convert the S3 Object to String
    */
   public static String convertS3Obj2Str(S3Object s3Obj) throws IOException {
      S3ObjectInputStream s3is = s3Obj.getObjectContent();
      ByteArrayOutputStream fos = new ByteArrayOutputStream();
      byte[] read_buf = new byte[1024];
      int read_len = 0;
      try {
         while ((read_len = s3is.read(read_buf)) > 0) {
            fos.write(read_buf, 0, read_len);
         }
         return fos.toString(ConstantsUnicode.UTF8);
      } finally {
         s3is.close();
         fos.close();

      }
   }

}
