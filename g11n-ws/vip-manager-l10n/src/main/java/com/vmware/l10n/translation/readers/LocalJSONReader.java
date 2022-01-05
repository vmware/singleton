/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.readers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * A helper util java class for local translation json file read and write
 */
public class LocalJSONReader {

    private static Logger LOGGER = LoggerFactory.getLogger(LocalJSONReader.class);

    /**
     * Read translation file according to filePath, if it's a empty result, it will get
     * current jar path and read translation file in jar
     *
     * @param filePath Translation file path
     * @return Translation json content
     * @throws IOException
     */
    public String getTranslationOutJar(String filePath) throws IOException {
		String result = "";
		String localTranslationPath = PathUtil.getProjectAbsolutePath()
				.replace("\\", ConstantsChar.BACKSLASH) + ConstantsChar.BACKSLASH + filePath;
		LOGGER.info("[file path out of jar] {}",  localTranslationPath);
		result = readLocalJSONFile(localTranslationPath);
		if (StringUtils.isEmpty(result)) {
			java.net.URL url = getClass().getResource(
					ConstantsChar.BACKSLASH + filePath.replace("\\", ConstantsChar.BACKSLASH));
			LOGGER.info("[file path in jar] {}",  url.getPath());
			result = IOUtils.toString(url, ConstantsUnicode.UTF8);
		}
		return result;
	}

    /**
     * Load local JSON file as string output
     *
     * @param path The json file output path
     * @return Json file content
     */
    public String readLocalJSONFile(String path) {
        String result = "";
        try {
            result = FileUtils.readFileToString(new File(path), ConstantsUnicode.UTF8);
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        }
        return result;
    }
}
