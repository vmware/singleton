/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.utils.CLDRUtils;
import com.vmware.i18n.utils.LocaleDataUtils;
import com.vmware.i18n.utils.MiscUtils;
import com.vmware.i18n.utils.SupplementUtils;

public class PatternParseMain {

    public static void main(String[] args) {
//        CLDRUtils.download();
        CLDRUtils.patternDataExtract();
        LocaleDataUtils.localesExtract();
        MiscUtils.miscDataExtract();
        SupplementUtils.supplementalCurrencyExtract();
        SupplementUtils.supplementalNumberingSystemsExtract();
        CLDRUtils.aliasesExtract();
        CLDRUtils.defaultContentExtract();
        CLDRUtils.regionDataExtract();
        CLDRUtils.pluralsExtract();
        CLDRUtils.languageDataExtract();
        CLDRUtils.patternTimeZoneNameExtract();
    }

}
