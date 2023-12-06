/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.ULocale;
import com.vmware.i18n.l2.dao.pattern.IPatternDao;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.i18n.utils.timezone.TimeZoneName;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.SingletonCache;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.exception.L2APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

/**
 * The class represents date formatting
 */
@Service
public class DateFormatService implements IDateFormatService{

	private static final Logger logger = LoggerFactory.getLogger(DateFormatService.class.getName());
	
	@Autowired
	private IPatternDao patternDao;

	@Autowired
	private SingletonCache singletonCache;
	
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

	
	/**
   	 * @param locale
   	 * @param boolean value is default territory or not
   	 * @return matching locale TimeZoneName
   	 */
	@Override
	public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) throws L2APIException {
		// TODO Auto-generated method stub
		String newLocale = locale.replace("_", "-");
		newLocale = CommonUtil.getCLDRLocale(newLocale, localePathMap, localeAliasesMap);
		if (CommonUtil.isEmpty(newLocale)){
			logger.info("Invalid locale!");
			throw new L2APIException(String.format(ValidationMsg.LOCALENAME_NOT_SUPPORTED, locale));
		}
		String timezoneNameJson = null;
		try {
			timezoneNameJson = singletonCache.getCachedObject(CacheName.PATTERN, getTimeZoneNameKey(newLocale, defaultTerritory), String.class);
		} catch (VIPCacheException e) {
			timezoneNameJson = null;
		}
		TimeZoneName timeZoneName = null;
		if (StringUtils.isEmpty(timezoneNameJson)) {
			logger.info("get timezoneNameJson data from file");
			timeZoneName = patternDao.getTimeZoneName(newLocale, defaultTerritory);
			if (StringUtils.isEmpty(timeZoneName)) {
				logger.info("file data don't exist");
				return null;
			}
			try {
				timezoneNameJson = new ObjectMapper().writeValueAsString(timeZoneName);
				singletonCache.addCachedObject(CacheName.PATTERN, getTimeZoneNameKey(newLocale, defaultTerritory),
						String.class, timezoneNameJson);
			} catch (Exception e) {
				throw new L2APIException(e.getMessage());
			}
		} else {
			try {
				timeZoneName = new ObjectMapper().readValue(timezoneNameJson, TimeZoneName.class);
			} catch (Exception e) {
				throw new L2APIException(e.getMessage());
			}
		}
		return timeZoneName;
	}

	private String getTimeZoneNameKey(String locale, boolean defauritorltTery) {
		return ConstantsKeys.TIMEZONE_NAME + locale + String.valueOf(defauritorltTery);
	}
}
