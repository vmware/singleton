/*
 * Copyright 2019-2020 VMware, Inc.
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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;

public class WhiteListUtils {

	private WhiteListUtils() {
	}

	public static Map<String, List<String>> getWhiteList(String path) {
		StringBuilder sb = new StringBuilder();
		File file = new File(path);
		InputStream inputStream = null;
		if (file.exists()) {
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}
		} else {
			inputStream = WhiteListUtils.class.getClassLoader().getResourceAsStream(ConstantsFile.WHITE_LIST_FILE);
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
		String result = sb.toString();
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

}
