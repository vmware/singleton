/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File utility class for common operation to files
 *
 */
public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	/**
	 * copy the files and folders in the source folder to the target folder
	 *
	 * @param srcPath
	 *            path string of the source folder
	 * @param destPath
	 *            path string of the desitination folder
	 * @return <code>true</code> if and only if the files and folders are
	 *         successfully copied; <code>false</code> otherwise
	 * @throws IOException
	 */
	public static boolean copyFiles(String srcPath, String destPath) throws IOException {
		boolean flag = true;
		File sourceFolder = new File(PathUtil.filterPathForSecurity(srcPath));
		if (sourceFolder.exists()) {
			File[] files = sourceFolder.listFiles();
			if (files.length > 0) {
				File targetFolder = new File(PathUtil.filterPathForSecurity(destPath));
				if (!targetFolder.exists()) {
					targetFolder.mkdirs();
				} else {
					FileUtils.cleanDirectory(targetFolder);
				}
				FileUtils.copyDirectory(sourceFolder, targetFolder);
			} else {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * unzip zip file
	 *
	 * @param zipPath
	 * @param targetDir
	 * @throws IOException
	 */
	public static void unzipFiles(String zipPath, String targetDir) throws IOException {

		try (ZipFile zf = new ZipFile(new File(zipPath), Charset.forName("UTF-8"))) {
			Enumeration<? extends ZipEntry> en = zf.entries();
			Path targetDirPath =  new File(targetDir).toPath();
			while (en.hasMoreElements()) {
				ZipEntry ze = en.nextElement();
				File f = new File(targetDir + ze.getName());
				if (!f.toPath().normalize().startsWith(targetDirPath)) {
					String normalStr = f.toPath().normalize().toString();
					throw new IOException("Bad zip entry extract path: normalize path "+normalStr+" not start path "+targetDirPath.toString());
				}
				     
				if (ze.isDirectory()) {
					f.mkdirs();
				} else {
					writerUnzipFile(zf, ze, f);
					printInfoLog(ze.getName());
				}
			}

		} catch (IOException e) {
			
			throw e;
		}
		printInfoLog("***********************Unzip Complete***************************");
	}

	private static void writerUnzipFile(ZipFile zf, ZipEntry ze, File f) throws IOException {

		int length = 0;
		byte[] b = new byte[2048];
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		try (OutputStream outputStream = new FileOutputStream(f); InputStream inputStream = zf.getInputStream(ze)) {
			while ((length = inputStream.read(b)) > 0) {
				outputStream.write(b, 0, length);
			}
		} catch (IOException e) {
			throw e;
		}

	}

	private static void printInfoLog(String infoStr) {
		logger.info(infoStr);
	}
}
