/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text.parser;

import java.util.Calendar;

import com.vmware.vipclient.i18n.l2.text.PatternItem;

public interface PatternParser {

    public String parse(PatternItem item, Calendar cal);

}
