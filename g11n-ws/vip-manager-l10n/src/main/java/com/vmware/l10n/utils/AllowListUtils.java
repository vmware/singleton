/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.l10n.conf.S3Client;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;

public abstract class AllowListUtils {
	@Value("${allow.list.location:bundle.json}")
	protected String allowlistLocation;

	protected abstract String readAllowlistFile();

	public Map<String, List<String>> getAllowList() {
		String result = readAllowlistFile();
		if (!StringUtils.isEmpty(result)) {
			ObjectMapper objmap = new ObjectMapper();
			JavaType stringType = objmap.constructType(String.class);
			CollectionLikeType arrayType = objmap.getTypeFactory().constructCollectionLikeType(ArrayList.class,
					String.class);
			MapLikeType mapType = objmap.getTypeFactory().constructMapLikeType(HashMap.class, stringType, arrayType);
			Map<String, List<String>> obj = null;
			try {
				obj = objmap.readValue(result, mapType);
			} catch (JsonProcessingException e) {
				return null;
			}
			return obj;
		}
		return null;
	}

	public static class LocalAllowlistUtils extends AllowListUtils {
		@Override
		protected String readAllowlistFile() {
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

	public static class S3AllowlistUtils extends AllowListUtils {
		@Autowired
		public S3Client s3Client;

		@Override
		protected String readAllowlistFile() {
			if (s3Client.isObjectExist(allowlistLocation)) {
				return s3Client.readObject(allowlistLocation);
			}
			return "";
		}
	}
}
