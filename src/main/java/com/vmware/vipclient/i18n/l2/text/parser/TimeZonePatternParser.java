/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import java.util.Calendar;

import com.vmware.vipclient.i18n.l2.text.PatternItem;

public class TimeZonePatternParser implements PatternParser {

    public String parse(PatternItem item, Calendar cal) {
        String timeZoneStr = null;
        int value = cal.get(item.patternCharToCalendarField());
        if (String.valueOf(item.getType()).indexOf("z") != -1) {
            int offset = cal.getTimeZone().getRawOffset() / (60 * 60 * 1000);
            switch (item.getLength()) {
            case 4:// full
                   // timeZoneStr= cal.getTimeZone().getDisplayName(new Locale("fr"));//new
                   // Locale("zh","CN") new Locale("en")
                if (offset >= 0) {
                    if (offset < 10) {
                        timeZoneStr = "GMT+0" + String.valueOf(offset) + ":00";
                    } else {
                        timeZoneStr = "GMT+" + String.valueOf(offset) + ":00";
                    }
                } else {
                    if (offset > -10) {
                        timeZoneStr = "GMT-0" + String.valueOf(Math.abs(offset)) + ":00";
                    } else {
                        timeZoneStr = "GMT" + String.valueOf(offset) + ":00";
                    }
                }
                break;
            default:// long
                if (offset >= 0) {
                    timeZoneStr = "GMT+" + String.valueOf(offset);
                } else {
                    timeZoneStr = "GMT" + String.valueOf(offset);
                }
            }
        }
        return timeZoneStr;
    }
}
