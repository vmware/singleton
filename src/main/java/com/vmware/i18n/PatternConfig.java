/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

/**
 * Singleton class for store the path of pattern bundle
 */
public class PatternConfig {
    private static volatile PatternConfig instance = null;
    private String patternPath;

    private PatternConfig() {
    }

    public static PatternConfig getInstance() {
        if (null == instance) {
            synchronized (PatternConfig.class) {
                if (null == instance) {
                    instance = new PatternConfig();
                }
            }
        }
        return instance;
    }

    public String getPatternPath() {
        return patternPath;
    }

    public void setPatternPath(String patternPath) {
        this.patternPath = patternPath;
    }
}
