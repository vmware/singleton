/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.pattern.service;

import com.vmware.i18n.utils.timezone.TimeZoneName;

public interface IPatternService {

    /**
     * Get i18n pattern by specific locale and categories
     * @param locale A string specified by the product to represent a specific locale. e.g. de, fr.
     * @param categories The pattern categories: dates, numbers, plurals, measurements. use ',' to split.
     *        e.g. "dates,numbers,plurals"
     * @return pattern
     */
    public String getPattern(String locale, String categories);

	public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory);
}
