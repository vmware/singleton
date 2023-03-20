/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vip.initdb.model.TransCompDocFile;
import com.vmware.vip.initdb.model.TransDocProperties;

/**
 * 
 *
 * @author shihu
 *
 */
public class BundleProductUtils {

	private static File[] listDir(File file) {
		File[] fileList = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}

		});

		return fileList;

	}

	private static TransCompDocFile createTransDocFile(String product, String version, String component, String locale,
			File docFile) {
		TransCompDocFile tdp = new TransCompDocFile();
		tdp.setProduct(product);
		tdp.setVersion(version);
		tdp.setComponent(component);
		tdp.setLocale(locale);
		tdp.setDocFile(docFile);
		return tdp;

	}

	private static TransDocProperties createTransCompDoc(String product, String version, String component,
			String componentAbsPath) {
		TransDocProperties tdp = new TransDocProperties();
		tdp.setProduct(product);
		tdp.setVersion(version);
		tdp.setComponent(component);
		tdp.setAbsPath(componentAbsPath);
		return tdp;

	}

	public static List<TransDocProperties> listVersionComponents(File versionFile, String product) {

		List<TransDocProperties> list = new ArrayList<TransDocProperties>();

		File[] componentList = listDir(versionFile);

		for (File compfile : componentList) {

			list.add(
					createTransCompDoc(product, versionFile.getName(), compfile.getName(), compfile.getAbsolutePath()));
		}

		return list;

	}

	public static List<TransCompDocFile> listComponentDocFiles(TransDocProperties component) {

		File compfile = new File(component.getAbsPath());

		File[] docListfile = compfile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isDirectory()) {
					return false;
				} else {
					return true;
				}
			}

		});

		List<TransCompDocFile> list = new ArrayList<TransCompDocFile>();

		for (File doc : docListfile) {

			String local = doc.getName().replace("messages_", "").replaceAll(".json", "");

			list.add(createTransDocFile(component.getProduct(), component.getVersion(), component.getComponent(), local,
					doc));

		}

		return list;
	}

	public static Map<String, List<String>> listProductVersionPath(File file) {

		File[] fileList = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}

		});

		Map<String, List<String>> map = new HashMap<>();

		for (File productFile : fileList) {

			File[] versionList = productFile.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					if (pathname.isDirectory()) {
						return true;
					}
					return false;
				}

			});

			List<String> paths = new ArrayList<String>();
			for (File versionFile : versionList) {

				paths.add(versionFile.getAbsolutePath());
			}

			map.put(productFile.getName(), paths);

		}

		return map;

	}

}
