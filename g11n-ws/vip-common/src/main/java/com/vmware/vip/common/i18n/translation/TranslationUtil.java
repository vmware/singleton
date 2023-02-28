/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.translation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.BaseDTO;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;

/**
 * Translation utility class
 * 
 */
public class TranslationUtil {

    /**
     * Generate locale-based key by parsing MultiComponentsDTO instance, the key will be used to
     * cache object.
     * 
     * @param multiComponentsDTO the MultiComponentsDTO instance
     * @return the key string, e.g. com.vmware.vrb.admin.7.0.1.fr,zh_CN
     */
    public static String generateKey(MultiComponentsDTO multiComponentsDTO) {
        StringBuilder key = new StringBuilder();
        if (!StringUtils.isEmpty(multiComponentsDTO.getProductName())) {
            key.append(multiComponentsDTO.getProductName());
        }
        if (multiComponentsDTO.getComponents().size() > 0) {
            key.append(ConstantsChar.DOT).append(multiComponentsDTO.getComponents());
        }
        if (!StringUtils.isEmpty(multiComponentsDTO.getVersion())) {
            key.append(ConstantsChar.DOT).append(multiComponentsDTO.getVersion());
        }
        if (multiComponentsDTO.getLocales().size() > 0) {
            key.append(ConstantsChar.DOT).append(multiComponentsDTO.getLocales());
        }
        return key.toString();
    }

    /**
     * Generate component-based key by parsing MultiComponentsDTO instance, the key will be used to
     * cache object.
     * 
     * @param singleComponentDTO the SingleComponentDTO instance
     * @return the key string, e.g. com.vmware.vrb.admin.fr
     */
    public static String generateCompnentKey(SingleComponentDTO singleComponentDTO) {
        StringBuilder key = new StringBuilder();
        if (!StringUtils.isEmpty(singleComponentDTO.getProductName())) {
            key.append(singleComponentDTO.getProductName());
        }
        if (!StringUtils.isEmpty(singleComponentDTO.getComponent())) {
            key.append(ConstantsChar.DOT).append(singleComponentDTO.getComponent());
        }
        if (!StringUtils.isEmpty(singleComponentDTO.getLocale())) {
            key.append(ConstantsChar.DOT).append(singleComponentDTO.getLocale());
        }
        return key.toString();
    }

    /**
     * Generate the product and version concat name.
     * 
     * @param baseDTO the BaseDTO instance
     * @return the concat name string, e.g. itfm-cloud/7.0.0.
     */
    public static String generateProductVersionConcatName(BaseDTO baseDTO) {
        StringBuilder path = new StringBuilder();
        if (!StringUtils.isEmpty(baseDTO.getProductName())) {
            path.append(baseDTO.getProductName());
        }
        if (!StringUtils.isEmpty(baseDTO.getVersion())) {
            path.append(ConstantsChar.BACKSLASH).append(baseDTO.getVersion());
        }
        return path.toString();
    }

    /**
     * Generate the resource's localized JSON file name,
     * 
     * @param locale The locale string
     * @return the JSON suffix name, e.g. messages_de.json
     */
    public static String genernateJsonLocalizedFileName(String locale) {
        if (locale.equals(ConstantsUnicode.EN_US) || locale.equals(ConstantsUnicode.EN)) {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsFile.FILE_TPYE_JSON;
        } else {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsChar.UNDERLINE + locale
                    + ConstantsFile.FILE_TPYE_JSON;
        }
    }

    /**
     * Generate the resource's localized properties file name.
     * 
     * @param locale The locale string
     * @return the properties suffix name, e.g. messages_de.properties
     */
    public static String genernatePropertiesLocalizedFileName(String locale) {
        if (locale.equals(ConstantsUnicode.EN_US) || locale.equals(ConstantsUnicode.EN)) {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsFile.FILE_TPYE_PROPERTIES;
        } else {
            return ConstantsFile.LOCAL_FILE_SUFFIX + ConstantsChar.UNDERLINE + locale
                    + ConstantsFile.FILE_TPYE_PROPERTIES;
        }
    }

    /**
     * Get the MultiComponentsDTO instance by parsing the well-defined JSON string
     * 
     * @param jsonStr The well-define JSON string
     * @return MultiComponentsDTO instance
     */
    public static MultiComponentsDTO getBaseTranslationDTO(String jsonStr) {
        JSONObject genreJsonObject = null;
        try {
            genreJsonObject = (JSONObject) JSONValue.parseWithException(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (genreJsonObject == null) {
            return null;
        }
        MultiComponentsDTO baseTranslationDTO = new MultiComponentsDTO();
        String bundleStr = (String)genreJsonObject.get(ConstantsKeys.BUNDLES);
        baseTranslationDTO.setProductName((String) genreJsonObject.get(ConstantsKeys.PRODUCTNAME));
        baseTranslationDTO.setVersion((String) genreJsonObject.get(ConstantsKeys.VERSION));
        baseTranslationDTO.setLocales((List) genreJsonObject.get(ConstantsKeys.lOCALES));
        baseTranslationDTO.setComponents((List) genreJsonObject.get(ConstantsKeys.COMPONENTS));
        baseTranslationDTO.setBundles((JSONArray)JSONValue.parse(bundleStr));
        return baseTranslationDTO;
    }

    /**
     * Get the SingleComponentDTO instance by parsing the well-defined JSON string
     * 
     * @param jsonStr The well-define JSON string
     * @return SingleComponentDTO instance
     */
    public static SingleComponentDTO getBaseComponentMessagesDTO(String jsonStr) {
        JSONObject genreJsonObject = null;
        try {
            genreJsonObject = (JSONObject) JSONValue.parseWithException(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (genreJsonObject == null) {
            return null;
        }
        SingleComponentDTO baseComponentMessagesDTO = new SingleComponentDTO();
        baseComponentMessagesDTO.setProductName((String) genreJsonObject
                .get(ConstantsKeys.PRODUCTNAME));
        baseComponentMessagesDTO
                .setComponent((String) genreJsonObject.get(ConstantsKeys.COMPONENT));
        baseComponentMessagesDTO.setLocale((String) genreJsonObject.get(ConstantsKeys.lOCALE));
        baseComponentMessagesDTO.setMessages(JSONValue.toJSONString(genreJsonObject
                .get(ConstantsKeys.MESSAGES)));
        return baseComponentMessagesDTO;
    }
}
