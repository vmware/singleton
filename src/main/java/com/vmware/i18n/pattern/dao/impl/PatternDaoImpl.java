/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.pattern.dao.impl;

import com.vmware.i18n.pattern.dao.IPatternDao;
import com.vmware.i18n.utils.LocalJSONReader;

public class PatternDaoImpl implements IPatternDao {

    public String getPattern(String jsonPath, String filePath) {
        String result = "";
        if (jsonPath.lastIndexOf(".jar") > 0)
            result = LocalJSONReader.readJarJsonFile(jsonPath, filePath);// jar
        else
            result = LocalJSONReader.readLocalJSONFile(jsonPath + filePath);// local
        return result;
    }

}
