/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.l2.text.PatternItem;

/**
 * The parser to parse pattern item of 'a'.
 *
 */
public class AMPMPatternParser implements PatternParser {
    private JSONObject dayPeriodsFormat;

    public AMPMPatternParser(JSONObject dayPeriodsFormat) {
        this.dayPeriodsFormat = dayPeriodsFormat;
    }

    public String parse(PatternItem item, Calendar cal) {
        int AM_PM = cal.get(Calendar.AM_PM);
        JSONArray dayPeriodsData = null;
        if (String.valueOf(item.getType()).indexOf("a") != -1) {// dayPeriodsFormat
            switch (item.getLength()) {
            case 4:
                dayPeriodsData = (JSONArray) dayPeriodsFormat.get(PatternKeys.WIDE);
                break;
            case 5:
                dayPeriodsData = (JSONArray) dayPeriodsFormat.get(PatternKeys.NARROW);
                break;
            default:
                dayPeriodsData = (JSONArray) dayPeriodsFormat.get(PatternKeys.ABBREVIATED);
            }
        } else {// dayPeriodsStandalone

        }
        if (null == dayPeriodsData) {
            return "";
        }
        return dayPeriodsData.get(AM_PM).toString();
    }
}
