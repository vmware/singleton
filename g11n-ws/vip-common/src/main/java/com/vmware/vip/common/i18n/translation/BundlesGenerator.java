/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;
import com.vmware.vip.common.l10n.source.util.IOUtil;

/**
 * This class represents a bundle generator including parse string and write it to a bundles.
 * 
 */
public class BundlesGenerator {

    /**
     * This function performs: 1. Parse the remote's responding result, the result would be like:
     * {"locales":"en, ja, zh_CN","components":"default","bundles":"[{ \"en\":
     * {\"cancel\":\"Abbrechen\"}, \"ja\": {\"cancel\":\"Abbrechen\"}, \"zh_CN\":
     * {\"cancel\":\"Abbrechen\"}, \"component\":\"default\"
     * }]","version":"1.0.0","productName":"devCenter"} 2. Convert the packaged JSON content with
     * multiple component messages to multiple JSO resource files 3. Write the translation to path
     * src-generated/main/resources/l10n/bundles/";
     * 
     * @param remoteRusult the remote's string
     */
    public void handleRemoteRusult(String remoteRusult) {
        MultiComponentsDTO baseTranslationDTO = TranslationUtil.getBaseTranslationDTO(remoteRusult);
        List bundles = baseTranslationDTO.getBundles();
        Iterator<?> it = bundles.iterator();
        String jsonFilePathDir = this.getJsonPath(baseTranslationDTO);
        while (it.hasNext()) {
            try {
                JSONObject bundleObj = (JSONObject) JSONValue.parseWithException(it.next()
                        .toString());
                String component = (String) bundleObj.get(ConstantsKeys.COMPONENT);
                List<String> locales = baseTranslationDTO.getLocales();
                String componentPath = jsonFilePathDir + ConstantsChar.BACKSLASH + component;
                new File(componentPath).mkdir();
                for (String locale : locales) {
                    String tLocale = StringUtils.trim(locale);
                    this.writeToBundle(
                            componentPath + ConstantsChar.BACKSLASH
                                    + TranslationUtil.genernateJsonLocalizedFileName(tLocale),
                            component, tLocale, (JSONObject) bundleObj.get(tLocale));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a directory by parsing MultiComponentsDTO instance.
     *
     * @param multiComponentsDTO the instance of MultiComponentsDTO
     * @return one string for the directory
     */
    private String getJsonPath(MultiComponentsDTO multiComponentsDTO) {
        String classpath = BundlesGenerator.class.getClassLoader().getResource(ConstantsChar.EMPTY)
                .getPath();
        String jsonFilePathDir = classpath + ConstantsFile.GENERATED_FOLDER
                + TranslationUtil.generateProductVersionConcatName(multiComponentsDTO);
        new File(jsonFilePathDir).mkdirs();
        return jsonFilePathDir;
    }

    /**
     * Write a JSON pairs to a JSON file
     *
     * @param jsonFileName The JSON file to be written
     * @param component The component name
     * @param locale The locale string
     * @param pairs The JSON pairs
     */
    @SuppressWarnings("unchecked")
    public void writeToBundle(String jsonFileName, String component, String locale, JSONObject pairs) {
        JSONObject json = new JSONObject();
        json.put(ConstantsKeys.COMPONENT, component);
        json.put(ConstantsKeys.lOCALE, locale);
        json.put(ConstantsKeys.MESSAGES, pairs);
        OutputStreamWriter write = null;
        BufferedWriter writer = null;
        FileOutputStream out = null;
        try {
            File f = new File(jsonFileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            out = new FileOutputStream(f);
            write = new OutputStreamWriter(out, ConstantsUnicode.UTF8);
            writer = new BufferedWriter(write);
            writer.write(json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeWriter(writer);
            IOUtil.closeWriter(write);
            IOUtil.closeOutputStream(out);
        }
    }

    /**
     * Parse MultiComponentsDTO as JSON string and write it to a JSON file
     *
     * @param jsonFileName The JSON file to be written
     * @param multiComponentsDTO The instance of MultiComponentsDTO
     */
    public void writeBaseTranslationDTO(String jsonFileName, MultiComponentsDTO multiComponentsDTO) {
        FileWriter jsonFileWriter = null;
        try {
            if (new File(jsonFileName).createNewFile()) {
                jsonFileWriter = new FileWriter(jsonFileName);
                jsonFileWriter.write(multiComponentsDTO.toJSONString());
                jsonFileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeWriter(jsonFileWriter);
        }
    }
}
