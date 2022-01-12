/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.pattern;

import org.springframework.stereotype.Repository;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.pattern.action.PatternAction;
import com.vmware.i18n.utils.timezone.TimeZoneName;

@Repository
public class PatternDaoImpl implements IPatternDao {

	public String getPattern(String locale, String categories) {
		locale = locale.replace("_", "-");
		return PatternUtil.getPatternFromLib(locale, categories);
	}
	
	
	/**
	 * @param locale
	 * @param boolean value representing default territory
	 * @return timezone name of the locale
	 */
	public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) {
		locale = locale.replace("_", "-");
        return PatternUtil.getTimeZoneName(locale, defaultTerritory);
	}


}
