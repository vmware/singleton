/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.vmware.l10n.source.dao.AllowListDao;
import com.vmware.l10n.utils.AllowListUtils;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
@Repository
@Profile(value="bundle")
public class LocalAllowListDaoImpl implements AllowListDao{
	@Value("${allow.list.location:bundle.json}")
	protected String allowlistLocation;

	@Override
	public Map<String, List<String>> getAllowList() {
		 String jsonStr = readAllowlistFile();
		return AllowListUtils.parseAllowList(jsonStr);
	}

	
	private String readAllowlistFile() {
		StringBuilder sb = new StringBuilder();
		File file = new File(allowlistLocation);
		InputStream inputStream = null;
		if (file.exists()) {
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}
		} else {
			inputStream = AllowListUtils.class.getClassLoader().getResourceAsStream(ConstantsFile.ALLOW_LIST_FILE);
		}
		try (BufferedReader inputReader = new BufferedReader(
				new InputStreamReader(inputStream, ConstantsUnicode.UTF8))) {
			String line = inputReader.readLine();
			while (null != line) {
				sb.append(line);
				line = inputReader.readLine();
			}
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return sb.toString();
	}
}
