/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.l2.text.PatternItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class DateStrPatternParser implements PatternParser {
    private JSONObject formatObj;

    public DateStrPatternParser(JSONObject formatObj) {
        this.formatObj = formatObj;
    }

    public String parse(PatternItem item, Calendar cal) {
        int value = cal.get(item.patternCharToCalendarField());
        JSONArray formatData = null;
        if (String.valueOf(item.getType()).indexOf("M") != -1
                || String.valueOf(item.getType()).indexOf("L") != -1) {// monthsFormat
            switch (item.getLength()) {
            case 3:
                formatData = (JSONArray) formatObj.get(PatternKeys.ABBREVIATED);
                break;
            case 4:
                formatData = (JSONArray) formatObj.get(PatternKeys.WIDE);
                break;
            case 5:
                formatData = (JSONArray) formatObj.get(PatternKeys.NARROW);
                break;
            default:

            }
        } else if (String.valueOf(item.getType()).indexOf("E") != -1) {// daysFormat
            formatObj = (JSONObject) formatObj.get("daysFormat");
            switch (item.getLength()) {
            case 4:
                formatData = (JSONArray) formatObj.get(PatternKeys.WIDE);
                break;
            case 5:
                formatData = (JSONArray) formatObj.get(PatternKeys.NARROW);
                break;
            case 6:
                formatData = (JSONArray) formatObj.get(PatternKeys.SHORT);
                break;
            default:
                formatData = (JSONArray) formatObj.get(PatternKeys.ABBREVIATED);
            }
            value = value - 1;// Since the day index in Calendar starts from 1, but that in format data starts from 0, hence value need reduce one
        }

        if (null == formatData) {
            return "";
        }

        return formatData.get(value) == null ? "" : formatData.get(value).toString();
    }
}
