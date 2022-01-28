/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.pattern;

import com.vmware.i18n.utils.timezone.TimeZoneName;

public interface IPatternDao {

    /**
     * Get i18n pattern JSON data from CLDR
     * @param locale
     * @param categories dates,numbers,plurals,measurements, split by ','
     * @return pattern JSON
     */
    public String getPattern(String locale, String categories);

    /**
	 * @param locale
	 * @param boolean value representing default territory
	 * @return timezone name of the locale 
	 */
    public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory);
}
