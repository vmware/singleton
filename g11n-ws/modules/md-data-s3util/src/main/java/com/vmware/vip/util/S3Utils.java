/**
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;

public class S3Utils {
	public static final String S3_NOT_EXIST_STR = "S3 File doesn't exist: ";
	public static final String S3_NOT_EXIST_ERR = "File's name doesn't exist!";
    
    
	private S3Utils() {
	}

	/**
	 * generate the product version path
	 */
	public static String genProductVersionS3Path(String basePath, String productName, String version) {
		StringBuilder path = new StringBuilder();
		path.append(basePath);
		if (!basePath.endsWith(ConstantsChar.BACKSLASH)) {
			path.append(ConstantsChar.BACKSLASH);
		}
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
		if (fileName.endsWith(ConstantsFile.FILE_TPYE_JSON) && (!fileName.endsWith(ConstantsFile.CREATION_INFO))
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

	public static String getBundleFilePath(String basePath, SingleComponentDTO dto) {
		if (StringUtils.isEmpty(dto.getComponent())) {
			dto.setComponent(ConstantsFile.DEFAULT_COMPONENT);
		}
		return S3Utils.genProductVersionS3Path(basePath, dto.getProductName(), dto.getVersion())
				+ dto.getComponent() + ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter.getLocalizedJSONFileName(dto.getLocale());
	}
}
