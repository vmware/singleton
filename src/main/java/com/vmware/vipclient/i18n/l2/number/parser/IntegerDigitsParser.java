/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.number.parser;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.l2.common.PatternKeys;

public class IntegerDigitsParser {
    private JSONObject numberSymbols;

    public IntegerDigitsParser(JSONObject numberSymbols) {
        this.numberSymbols = numberSymbols;
    }

    public String groupIntegerDigits(String integerDigits, int groupingSize) {
        if (integerDigits.length() > groupingSize) {
            String localizedGroupSep = (String) numberSymbols.get(PatternKeys.GROUP);
            String reverseNumStr = new StringBuilder(integerDigits).reverse().toString();
            StringBuilder groupedReverseNumStr = new StringBuilder(reverseNumStr);
            int index;
            for (index = 3; index < groupedReverseNumStr.length(); index += 4) {

                groupedReverseNumStr.insert(index, localizedGroupSep);

            }
            return new StringBuilder(groupedReverseNumStr).reverse().toString();
        } else {
            return integerDigits;
        }
    }
}
