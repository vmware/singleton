/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.pattern;

public interface IPatternDao {

    /**
     * Get i18n pattern JSON data from CLDR
     * @param locale
     * @param categories dates,numbers,plurals,measurements, split by ','
     * @return pattern JSON
     */
    public String getPattern(String locale, String categories);

}
