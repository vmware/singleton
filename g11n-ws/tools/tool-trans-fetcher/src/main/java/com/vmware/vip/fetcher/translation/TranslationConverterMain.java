/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.translation;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFileWritter;
import com.vmware.vip.common.utils.PropertiesFileUtil;
import com.vmware.vip.fetcher.common.FileUtils.OrderedPropertiesUtils;
import com.vmware.vip.fetcher.common.dto.OrderedProperties;
import com.vmware.vip.fetcher.common.resourcefile.ResourceFilePathGetter;

/**
 * Single main class to convert properties resources to JSON resource. It could be used to help
 * conversion manually.
 */
public class TranslationConverterMain {
    public static Properties p = PropertiesFileUtil.loadFromStream("config.properties");
    public static String sourceLocation = (String) p.get("sourceLocation");
    public static String targetLocation = (String) p.get("targetLocation");
    public static String productName = (String) p.get("productName");
    public static String components = (String) p.get("components");
    public static String version = (String) p.get("version");
    public static String propertiesFilePrefixs = (String) p.get("propertiesFilePrefixs");
    public static String locales = (String) p.get("locales");
    public static String pseudo = (String) p.get("pseudo");
    public static String pseudoTag = (String) p.get("pseudoTag");

    /**
     * Convert single properties file to single json file
     */
    public static void convertPropertiesToJSON() {
        SingleComponentDTO singleComponentDTO = new SingleComponentDTO();
        singleComponentDTO.setProductName(productName);
        singleComponentDTO.setVersion(version);
        System.out.println("sourceLocation: "+sourceLocation);
        System.out.println("propertiesFilePrefixs: "+propertiesFilePrefixs);
        String versionPath=sourceLocation+ResourceFilePathGetter.getProductVersionConcatName(singleComponentDTO);
        File file=new File(versionPath);
        String[] componentsArray = StringUtils.split(components, ConstantsChar.COMMA);
        String[] filePrefixsArray = StringUtils.split(propertiesFilePrefixs, ConstantsChar.COMMA);
        String[] localesArray = StringUtils.split(locales, ConstantsChar.COMMA);
        int componentIndex=0;
        for (String component : componentsArray) {
            singleComponentDTO.setComponent(component);
            String filePrefix=filePrefixsArray[componentIndex];
            for (String locale : localesArray) {
                singleComponentDTO.setLocale(locale);
                String jsonFileName = ResourceFilePathGetter.getLocalizedJSONFileName(locale);
                String url = "";
                System.out.println("properties file path: "+url);
                try {
                     OrderedProperties orderedPro= OrderedPropertiesUtils.loadFromFile(url);
                     Map pairs = null;
                     if(pseudo.equals("true")){
                         pairs= OrderedPropertiesUtils.getOrderedMapFromPropForPseudo(orderedPro,pseudoTag); 
                     }else{
                         pairs= OrderedPropertiesUtils.getOrderedMapFromProp(orderedPro);
                     }
                    singleComponentDTO.setMessages(pairs);
                    ResourceFileWritter.writeJSONObjectToJSONFile(
                            ResourceFilePathGetter.getLocalizedJSONFilesDir(targetLocation,
                                    singleComponentDTO) + ConstantsChar.BACKSLASH + jsonFileName,
                            singleComponentDTO);
                } catch (VIPResourceOperationException e) {
                    e.printStackTrace();
                }
            }
            componentIndex++;
        }
        System.out.println("Convertion completed!");
    }

    /**
     * Combine multiple properties resource files to one JSON file
     */
    @SuppressWarnings({ "unchecked", "static-access" })
    public static void combinePropertiesToJSON() {
        MultiComponentsDTO baseTranslationDTO = new MultiComponentsDTO();
        baseTranslationDTO.setProductName(productName);
        //baseTranslationDTO.setComponents(components);
        //baseTranslationDTO.setLocales(TranslationConverterMain.locales);
        baseTranslationDTO.setVersion(version);
        String jsonFileName = ResourceFilePathGetter.getLocalizedJSONFileName("en");
        JSONArray bundles = new JSONArray();
        String[] componentArray = StringUtils.split(components, ConstantsChar.COMMA);
        String[] localeArray = StringUtils.split(locales, ConstantsChar.COMMA);
        SingleComponentDTO singleComponentDTO = new SingleComponentDTO();
        singleComponentDTO.setProductName(productName);
        singleComponentDTO.setVersion(version);
        for (String component : componentArray) {
            singleComponentDTO.setComponent(component);
            Map<String, Object> bundle = new LinkedHashMap<String, Object>();
            bundle.put("component", component);
            for (String locale : localeArray) {
                singleComponentDTO.setLocale(locale);
                String url = "";
                // Properties pro= PropertiesFileUtil.loadFromURL(url);//if the resource files to be converted are placed on a remote server,use this code
                // Properties pro= PropertiesFileUtil.loadFromStream(url);//if the resource files to be converted are placed in the project path,use this code
                Properties pro = PropertiesFileUtil.loadFromFile(url);// if the resource files to be converted are placed in the disk path,use this code
                JSONObject pairs = new ProToJSONConverter().getJSONFromProp(pro);
                bundle.put(locale, pairs);
            }
            bundles.put(bundle);
        }
        baseTranslationDTO.setBundles(bundles);
        singleComponentDTO.setComponent("");
        ResourceFileWritter bundlesGenerator = new ResourceFileWritter();
        try {
            bundlesGenerator.writeMultiComponentsDTOToJSONFile(
                    ResourceFilePathGetter.getLocalizedJSONFilesDir(targetLocation,
                            singleComponentDTO) + "/" + jsonFileName, baseTranslationDTO);
        } catch (VIPResourceOperationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // ProTranslationFetcherMain.convertPropertiesToJSON();
        TranslationConverterMain.convertPropertiesToJSON();
        // TranslationConverterMain.combinePropertiesToJSON();
    }
}
