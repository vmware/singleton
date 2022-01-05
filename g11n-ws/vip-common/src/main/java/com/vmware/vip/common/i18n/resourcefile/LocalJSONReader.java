/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.resourcefile;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.vmware.vip.common.constants.ConstantsUnicode;

/**
 * This Class represents a Reader to read local JSON file
 * 
 */
public class LocalJSONReader {
    /**
     * Read a local JSON file from specific path.
     *
     * @param path the location of the JSON file to be read
     * @return one string as from the file's content
     */
    public String readLocalJSONFile(String path) {
        String result = "";
        try {
            result = FileUtils.readFileToString(new File(path), ConstantsUnicode.UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
