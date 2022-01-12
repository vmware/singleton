/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.util.FileUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * The class represents to unzip tanslation files from build jar to server local
 * and vIP server will load translation from this local
 */
public class UnzipTranslationUtils {
	private static Logger logger = LoggerFactory.getLogger(UnzipTranslationUtils.class);

	private UnzipTranslationUtils() {
	}

	/**
	 * When run project jar on server, program will unzip translation zip file which
	 * was compressed into jar file when building to server local
	 *
	 * @throws IOException
	 */
	public static void unzipTranslationToLocal(boolean overrided, Class<?> clazz) throws IOException {
		if (overrided == false && isBundleExistOutofJar()) {
			return; // don't unzip the bundle again if bundle exists already.
		} else {
			String fileName = ConstantsKeys.RESOURCES + ConstantsKeys.ZIP_SUFFIX;

			String delPath = PathUtil.getProjectAbsolutePath() + File.separator + "l10n" + File.separator;
			deleteDir(new File(delPath));
			String zipPath = PathUtil.getProjectAbsolutePath() + File.separator + fileName;
			try (InputStream is = clazz.getResourceAsStream(ConstantsChar.BACKSLASH + fileName);
					FileOutputStream out = new FileOutputStream(new File(zipPath));

			) {
				if (is != null) {
					IOUtils.copy(is, out);
					FileUtil.unzipFiles(zipPath, PathUtil.getProjectAbsolutePath() + File.separator);
				}
			}
		}
	}

	private static boolean isBundleExistOutofJar() {
		String rootAppPath = PathUtil.getProjectAbsolutePath() + File.separator;
		String bundlePath = rootAppPath + ConstantsFile.L10N_BUNDLES_PATH;
		return new File(bundlePath).exists();
	}

	private static String getDelPath(String basePathWithSeparate) {
		String delPath = null;
		if ((basePathWithSeparate.substring(0, 2)).equals(("." + File.separator))) {
			if (basePathWithSeparate.length() == 2) {
				delPath = new File(ConstantsChar.EMPTY).getAbsolutePath() + File.separator
						+ ConstantsFile.L10N_BUNDLES_PATH;
			} else {
				delPath = new File(ConstantsChar.EMPTY).getAbsolutePath() + basePathWithSeparate.substring(1)
						+ ConstantsFile.L10N_BUNDLES_PATH;
			}
		} else if ((basePathWithSeparate.substring(0, 2)).equals("..")) {
			delPath = new File(ConstantsChar.EMPTY).getParentFile().getAbsolutePath()
					+ basePathWithSeparate.substring(2) + ConstantsFile.L10N_BUNDLES_PATH;
		} else {
			delPath = basePathWithSeparate + ConstantsFile.L10N_BUNDLES_PATH;
		}

		return delPath;
	}

	public static void unzipTranslationToLocal(String basePathWithSeparate, boolean overrided, Class<?> clazz)
			throws IOException {
		if (overrided == false && isBundleExistOutofJar(basePathWithSeparate)) {
			return; // don't unzip the bundle again if bundle exists already.
		} else {
			String fileName = ConstantsKeys.RESOURCES + ConstantsKeys.ZIP_SUFFIX;
			if (overrided) {
				String delPath = getDelPath(basePathWithSeparate);
				logger.info("begin to delete the bundle file Directory from {}", delPath);
				try {
					deleteDir(new File(delPath));
				}catch(Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
			String zipPath = basePathWithSeparate + fileName;
			File zipFile = new File(zipPath);
			if (!zipFile.getParentFile().exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}
			try (InputStream is = clazz.getResourceAsStream(ConstantsChar.BACKSLASH + fileName);) {
				if (is != null) {
					Files.copy(is, zipFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					logger.info("***********************Begin Extract Bundle files****************************");
					FileUtil.unzipFiles(zipPath, basePathWithSeparate);
				}

			}
		}
	}

	private static boolean isBundleExistOutofJar(String basePath) {
		String bundlePath = basePath + ConstantsFile.L10N_BUNDLES_PATH;
		return new File(bundlePath).exists();
	}

	private static void deleteDir(File path) {
			if ((path == null) || (!path.exists())) {
				return;
			}else if (path.isFile()) {
				logger.info("delete file ---{}", path.getAbsolutePath());
				path.deleteOnExit();
			}else {
				File[] files = path.listFiles();
				if (files != null ) {
					for (int i = 0; i < files.length; i++) {
						deleteDir(files[i]);
					}
				}
				logger.info("delete file Directory---{}", path.getAbsolutePath());
				path.deleteOnExit();
			}
	}
}
