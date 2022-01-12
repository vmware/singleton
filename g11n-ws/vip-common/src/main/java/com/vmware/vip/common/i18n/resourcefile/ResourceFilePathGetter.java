/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.resourcefile;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.BaseDTO;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This class represents a getter for parsing the resource files from DTO's data, etc.
 * 
 */
public class ResourceFilePathGetter {

    /**
     * get the productName and version from DTO as prodcutName/version string
     *
     * @param baseDTO a data object contains productName and version
     * @return prodcutName/version string, e.g. VCG/2.0.0
     */
    public static String getProductVersionConcatName(BaseDTO baseDTO) {
        StringBuilder path = new StringBuilder();
        if (!StringUtils.isEmpty(baseDTO.getProductName())) {
            path.append(baseDTO.getProductName());
        }
        if (!StringUtils.isEmpty(baseDTO.getVersion())) {
            path.append(File.separator).append(baseDTO.getVersion());
        }
        return path.toString();
    }

    /**
     * get a localized JSON file's suffix.
     *
     * @param locale the locale string, e.g ja_JP
     * @return localized JSON file's suffix, e.g. messages_ja_JP.json
     */
	public static String getLocalizedJSONFileName(String locale) {
		return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsChar.UNDERLINE
				+ locale + ConstantsFile.FILE_TPYE_JSON;
	}

    /**
     * get a localized properties file's suffix.
     *
     * @param locale the locale string, e.g ja_JP
     * @return localized properties file's suffix, e.g. messages_ja_JP.properties
     */
    public static String getLocalizedPropertiesFileName(String locale) {
        if (locale.equals(ConstantsUnicode.EN_US) || locale.equals(ConstantsUnicode.EN)) {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsFile.FILE_TPYE_PROPERTIES;
        } else {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsChar.UNDERLINE + locale
                    + ConstantsFile.FILE_TPYE_PROPERTIES;
        }
    }

    /**
     * Parse the MultiComponentsDTO with basePath/component/fileName as a properties file path.
     *
     * @param basePath base path of the properties file
     * @param multiComponentsDTO instance of MultiComponentsDTO
     * @param component the component name
     * @param fileName the file name
     * @return localized properties file path, e.g. ./VCG/2.0.0/cim/messages_ja_JP.properties
     */
    public static String getLocalizedPropertiesFilesDir(String basePath,
            MultiComponentsDTO multiComponentsDTO, String component, String fileName) {
        String propertiesFilePathDir = basePath
                + ResourceFilePathGetter.getProductVersionConcatName(multiComponentsDTO)
                + ConstantsChar.BACKSLASH + component + ConstantsChar.BACKSLASH + fileName;
        return propertiesFilePathDir;
    }

    /**
     * Parse the MultiComponentsDTO as a properties file path.
     *
     * @param multiComponentsDTO instance of MultiComponentsDTO
     * @return localized properties file path, e.g. ./VCG/2.0.0/cim/messages_ja_JP.properties
     */
    public static String getLocalizedPropertiesFilesDir(MultiComponentsDTO multiComponentsDTO) {
        String classpath = ResourceFileWritter.class.getClassLoader()
                .getResource(ConstantsChar.EMPTY).getPath();
        String propertiesFilePathDir = classpath + ConstantsFile.L10N_BUNDLES_PATH
                + ResourceFilePathGetter.getProductVersionConcatName(multiComponentsDTO);
        return propertiesFilePathDir;
    }

    /**
     * Create a component directory by parsing a SingleComponentDTO instance
     *
     * @param singleComponentDTO instance of SingleComponentDTO
     * @return the path of created directory
     */
    public static String getLocalizedJSONFilesDir(SingleComponentDTO singleComponentDTO) {
        String classpath = ResourceFileWritter.class.getClassLoader()
                .getResource(ConstantsChar.EMPTY).getPath();
        String jsonFilePathDir = classpath + ConstantsFile.GENERATED_FOLDER
                + ResourceFilePathGetter.getProductVersionConcatName(singleComponentDTO)
                + ConstantsChar.BACKSLASH + singleComponentDTO.getComponent();
        new File(jsonFilePathDir).mkdirs();
        return jsonFilePathDir;
    }

    /**
     * Create a component directory under specific path by parsing a SingleComponentDTO instance.
     *
     * @param singleComponentDTO instance of SingleComponentDTO
     * @param generatedFilePath the path to create the directory
     * @return the path of created directory
     */
    public static String getLocalizedJSONFilesDir(String generatedFilePath,
            SingleComponentDTO singleComponentDTO) {
        String jsonFilePathDir = generatedFilePath + ConstantsChar.BACKSLASH
                + ResourceFilePathGetter.getProductVersionConcatName(singleComponentDTO)
                + ConstantsChar.BACKSLASH + singleComponentDTO.getComponent();
        new File(PathUtil.filterPathForSecurity(jsonFilePathDir)).mkdirs();
        return jsonFilePathDir;
    }
    
    public static String getLocaleByFileName(String fileName) {
        String locale = null;
        if (fileName.endsWith(ConstantsFile.FILE_TPYE_JSON)) {
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

}
