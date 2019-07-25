/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.date;

import com.vmware.vip.core.messages.exception.L2APIException;

public interface IDateFormatService {

	public String formatDate(String locale, long date, String pattern) throws L2APIException;
}
