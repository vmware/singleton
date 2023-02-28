/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.initdb.model.DBI18nDocument;
import com.vmware.vip.initdb.model.TransCompDocFile;

/**
 * 
 *
 * @author shihu
 *
 */
public class ReadCompJsonFile {
	private static Logger logger = LoggerFactory.getLogger(ReadCompJsonFile.class);

	public static StringBuilder file2String(File file, Charset cs) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), cs)) {
			StringBuilder result = new StringBuilder();
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				result.append(line);
			}
			return result;
		}
	}

	public static DBI18nDocument Json2DBDoc(TransCompDocFile compDoc)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();

		logger.info(compDoc.getDocFile().getAbsolutePath());

		StringBuilder strBuilder = file2String(compDoc.getDocFile(), StandardCharsets.UTF_8);
		logger.debug(strBuilder.toString());

		DBI18nDocument doc = mapper.readValue(strBuilder.toString(), DBI18nDocument.class);
		logger.debug("bundle doc component:" + doc.getComponent());
		logger.debug("bundle doc locale:" + doc.getComponent());
		doc.setProduct(compDoc.getProduct());
		doc.setVersion(compDoc.getVersion());
		doc.setComponent(compDoc.getComponent());
		doc.setLocale(compDoc.getLocale());
		return doc;
	}

}
