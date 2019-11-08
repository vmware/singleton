/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import java.util.Calendar;
import java.util.Date;

import com.vmware.vipclient.i18n.l2.text.PatternItem;

/**
 * Default parser
 *
 */
public class DefaultPatternParser implements PatternParser {

    public String parse(PatternItem item, Date date) {
        return String.valueOf(item.getType());
    }

    public String parse(PatternItem item, Calendar cal) {
        // TODO Auto-generated method stub
        return null;
    }

}
