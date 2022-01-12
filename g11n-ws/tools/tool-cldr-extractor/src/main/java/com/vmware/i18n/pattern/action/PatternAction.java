/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.pattern.action;

import com.vmware.i18n.pattern.service.IPatternService;
import com.vmware.i18n.pattern.service.impl.PatternServiceImpl;
import com.vmware.i18n.utils.timezone.TimeZoneName;

public class PatternAction {

    private static volatile PatternAction instance = null;
    private IPatternService service = null;

    private PatternAction() {
        service = new PatternServiceImpl();
    }

    public static PatternAction getInstance() {
        if (null == instance) {
            synchronized (PatternAction.class) {
                if (null == instance) {
                    instance = new PatternAction();
                }
            }
        }
        return instance;
    }

    public String getPattern(String locale, String categories) {
        return service.getPattern(locale, categories);
    }
    
    public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) {
        return service.getTimeZoneName(locale, defaultTerritory);
    }
}
