/*
 * Copyright 2019-2023 VMware, Inc.
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
    	System.out.println("start patternDataExtract");
        CLDRUtils.patternDataExtract();
        System.out.println("start localesExtract");
        LocaleDataUtils.localesExtract();
        System.out.println("start miscDataExtract");
        MiscUtils.miscDataExtract();
        System.out.println("start supplementalCurrencyExtract");
        SupplementUtils.supplementalCurrencyExtract();
        System.out.println("start supplementalNumberingSystemsExtract");
        SupplementUtils.supplementalNumberingSystemsExtract();
        System.out.println("start extractSupplementalDayPeriods");
        SupplementUtils.extractSupplementalDayPeriods();
        System.out.println("start aliasesExtract");
        CLDRUtils.aliasesExtract();
        System.out.println("start defaultContentExtract");
        CLDRUtils.defaultContentExtract();
        System.out.println("start regionDataExtract");
        CLDRUtils.regionDataExtract();
        System.out.println("start pluralsExtract");
        CLDRUtils.pluralsExtract();
        System.out.println("start languageDataExtract");
        CLDRUtils.languageDataExtract();
        System.out.println("start patternTimeZoneNameExtract");
        CLDRUtils.patternTimeZoneNameExtract();
        
        System.out.println("end extract cldr data");
    }

}
