/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPAPIException;

/**
 * This class represents the DTO for multiple components, and each component contains one
 * translation file's content.
 * 
 */
public class MultiComponentsDTO extends BaseDTO {
    // The string contains multiple locale, e.g. "zh_CN, ja_JP"
    private List<String> locales;

    // The string contains multiple components, e.g. "aim, home,"
    private List<String> components;

    // The array contains the component's translation
    private JSONArray bundles;

    // The location where to get the translation
    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public List<String> getLocales() {
        return locales;
    }

    public void setLocales(List<String> locales) {
        //String[] normalizeLocales = LocaleUtils.normalizeLocaleStr(locales.split(","));
        //this.locales = CustomStringUtils.connectStringArrayAsString(normalizeLocales);
        this.locales = locales;
    }

    public JSONArray getBundles() {
        return bundles;
    }

    public void setBundles(JSONArray bundles) {
        this.bundles = bundles;
    }

    @SuppressWarnings({ "unchecked" })
    public String toJSONString() {
        JSONObject jo = new JSONObject();
        jo.put(ConstantsKeys.PRODUCTNAME, this.getProductName());
        jo.put(ConstantsKeys.VERSION, this.getVersion());
        jo.put(ConstantsKeys.lOCALES, this.getLocales());
        jo.put(ConstantsKeys.COMPONENTS, this.getComponents());
        jo.put(ConstantsKeys.BUNDLES, this.getBundles());
        return jo.toString();
    }

    /*
     * Get the BaseTranslationDTO by json string
     */

    /**
     * Get multiple component DTO from a JSON string.
     *
     * @param jsonStr JSON string contains multiple component's translation
     * @return MultiComponentsDTO to wrap the translation
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static MultiComponentsDTO getMultiComponentsDTO(String jsonStr) throws VIPAPIException {
        JSONObject genreJsonObject = null;
        try {
            genreJsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            throw new VIPAPIException("Parse string '" + jsonStr + "' failed.");
        }
        if (genreJsonObject == null) {
            return null;
        }
        String bundleStr = (String) genreJsonObject.get(ConstantsKeys.BUNDLES);
        MultiComponentsDTO baseTranslationDTO = new MultiComponentsDTO();
        baseTranslationDTO.setProductName((String) genreJsonObject.get(ConstantsKeys.PRODUCTNAME));
        baseTranslationDTO.setVersion((String) genreJsonObject.get(ConstantsKeys.VERSION));
        baseTranslationDTO.setLocales((List) genreJsonObject.get(ConstantsKeys.lOCALES));
        baseTranslationDTO.setComponents((List) genreJsonObject.get(ConstantsKeys.COMPONENTS));
        baseTranslationDTO.setBundles(new JSONArray(bundleStr));
        return baseTranslationDTO;
    }
}
