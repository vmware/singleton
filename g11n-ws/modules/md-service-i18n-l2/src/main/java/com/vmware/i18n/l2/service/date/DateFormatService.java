/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.date;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.ULocale;
import com.vmware.vip.core.messages.exception.L2APIException;

/**
 * The class represents date formatting
 */
@Service
public class DateFormatService implements IDateFormatService{

	/**
	 * Format a long date to localized date
	 *
	 * @param locale A string representing a specific locale in [lang]_[country (region)] format. e.g., ja_JP, zh_CN
	 * @param date Java timestamp format, e.g: 1472728030290
	 * @param pattern Date pattern,e.g: [YEAR = "y",QUARTER = "QQQQ"], ABBR_QUARTER =
	 *        "QQQ",YEAR_QUARTER = "yQQQQ",YEAR_ABBR_QUARTER = "yQQQ" and so on.
	 * @return Localized date
	 * @throws L2APIException
	 */
	public String formatDate(String locale, long date, String pattern) throws L2APIException {
		try{
			ULocale uLocale = new ULocale(locale);
			Date d = new Date(date);
			SimpleDateFormat format = new SimpleDateFormat(pattern, uLocale);
			return format.format(d);
		}catch(Exception e){
			throw new L2APIException(e.getMessage());
		}
	}

}
