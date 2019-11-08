/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import java.util.Calendar;

import com.vmware.vipclient.i18n.l2.text.PatternItem;

public class DatePatternParser implements PatternParser {

    private int     offset;
    private boolean trim    = false;
    private boolean negWrap = false;

    public DatePatternParser(int offset, boolean trim, boolean negWrap) {
        this.offset = offset;
        this.trim = trim;
        this.negWrap = negWrap;
    }

    public DatePatternParser() {
        this(0);
    }

    public DatePatternParser(int offset) {
        this(offset, false, false);
    }

    public String parse(PatternItem item, Calendar cal) {
        int value = cal.get(item.patternCharToCalendarField());
        int len = item.getLength();
        if (offset > 0 || value > -offset) {
            value += offset;
        }
        /*
         * if (value == 0 && offset == -12) { value = 12; }
         */
        String neg = "";
        if (value < 0 || (negWrap && value <= 0)) {
            if (negWrap) {
                value = -value + 1;
            } else {
                value = -value;
                neg = "-";
            }
        }
        String valueStr = String.valueOf(value);
        while (valueStr.length() < len) {
            valueStr = "0" + valueStr;
        }
        if (trim) {
            valueStr = valueStr.substring(valueStr.length() - len);
        }
        return neg + valueStr;
    }
}
