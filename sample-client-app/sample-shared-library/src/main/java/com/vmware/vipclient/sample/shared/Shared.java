/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample.shared;

import java.util.Locale;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class Shared {
    TranslationMessage tm = null;
    Locale locale = null;
    static String component = "shared";

    public Shared(TranslationMessage tm , Locale locale) {
        this.tm = tm;
        this.locale = locale;
    }

    public String someMethod () {
        System.out.println(">>>>>> Shared.someMethod");
        String msg = tm.getMessage(locale, component, "shared.library.key1");
        return msg;
    }
}
