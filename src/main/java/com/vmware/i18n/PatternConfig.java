package com.vmware.i18n;

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
