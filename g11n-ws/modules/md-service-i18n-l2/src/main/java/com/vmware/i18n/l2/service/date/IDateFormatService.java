/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.date;

import com.vmware.i18n.utils.timezone.TimeZoneName;
import com.vmware.vip.core.messages.exception.L2APIException;

public interface IDateFormatService {

	public String formatDate(String locale, long date, String pattern) throws L2APIException;
	
	/**
   	 * @param locale
   	 * @param default territory
   	 * @return matching locale TimeZoneName
   	 */
     public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) throws L2APIException;
}
