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
import com.vmware.vipclient.i18n.util.JSONUtils;

public class EraPatternParser implements PatternParser {

    private JSONObject erasFormat;

    public EraPatternParser(JSONObject erasFormat, boolean longFlag) {
        this.erasFormat = erasFormat;
    }

    public EraPatternParser(JSONObject erasFormat) {
        this(erasFormat, false);
    }

    public String parse(PatternItem item, Calendar cal) {
        int era = cal.get(Calendar.ERA);// cal.get(item.patternCharToCalendarField());
        JSONArray erasData = null;
        switch (item.getLength()) {
        case 4:
            erasData = (JSONArray) JSONUtils.getFromJSONObject(erasFormat, PatternKeys.WIDE);
            break;
        case 5:
            erasData = (JSONArray) JSONUtils.getFromJSONObject(erasFormat, PatternKeys.NARROW);
            break;
        default:
            erasData = (JSONArray) JSONUtils.getFromJSONObject(erasFormat, PatternKeys.ABBREVIATED);
        }
        return erasData.get(era).toString();
    }
}
