/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.sample;

import java.util.Date;
import java.util.Locale;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;

public class Format {
	
	public static void demo(Locale locale) {
		// Number format
		double num = 201703.5416926;
		System.out.println(">>>>>> Number format: " + num);
		String resultOfNumber = formatNumber(num);
		System.out.println(resultOfNumber);

		// Percent format
		double numOfPercent = 12.3456;
		System.out.println(">>>>>> Percent format: " + numOfPercent);
		String resultOfPercent = formatPercent(numOfPercent);
		System.out.println(resultOfPercent);

		// Currency format
		double numOfCurrency = 201703.5416926;
		System.out.println(">>>>>> Currency format: " + numOfCurrency);
		String resultOfCurrency = formatCurrency(numOfCurrency);
		System.out.println(resultOfCurrency);

		String code = "EUR";
		System.out.println(">>>>>> Currency format with code \"" + code + "\": " + numOfCurrency);
		String resultOfCurrency2 = formatCurrency(numOfCurrency, code);
		System.out.println(resultOfCurrency2);

		// Date format
		Date date = new Date(1511156364801l);
		String pattern = "long";
		String tz = "GMT-8";
		System.out.println(">>>>>> Date format with pattern \"" + pattern + "\": " + date);
		System.out.println(formatDate(date, pattern));
		System.out.println(">>>>>> Date format with pattern \"" + pattern + "\" and timezone \"" + tz + "\": " + date);
		System.out.println(formatDate(date, pattern, tz));
	}
	
	/**
	 * Format a number to an localized number string in decimal style according to locale's decimal format defined in cldr.
	 * Default fraction size in cldr decimal format is: the minimum is 0, the maximum is 3.
	 * 
	 * @param num  The number to be formatted. It can be Number or string representing number.
	 * @return The formatted number string.
	 */
	public static String formatNumber(Object num){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatNumber(num, locale);
	}

	/**
	 * Format a number to an localized number string in decimal style using user self defined fraction size.
	 * cldr decimal format's fraction size will be ignored.
	 * 
	 * @param num The number to be formatted. It can be Number or string representing number.
	 * @param fractionSize User self defined fraction size.
	 * @return The formatted number string.
	 */
	public static String formatNumber(Object num, int fractionSize){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatNumber(num, fractionSize, locale);
	}
	
	/**
	 * Format a number to an localized number string in percent style according to locale's percent format defined in cldr.
	 * Default fraction size in cldr percent format is 0.
	 * 
	 * @param num The number to be formatted. It can be Number or string representing number.
	 * @return The formatted percent string.
	 */
	public static String formatPercent(Object num){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatPercent(num, locale);
	}
	
	/**
	 * Format a number to an localized number string in percent style using user self defined fraction size.
	 * cldr percent format's fraction size will be ignored.
	 * 
	 * @param num The number to be formatted. It can be Number or string representing number.
	 * @param fractionSize User self defined fraction size.
	 * @return The formatted percent string.
	 */
	public static String formatPercent(Object num, int fractionSize){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatPercent(num, locale);
	}
	
	/**
	 * Format a number to an localized number string with default currency symbol($) according to locale's currency format defined in cldr.
	 * Default fraction size in cldr currency format is 2.
	 * 
	 * @param num The number to be formatted. It can be Number or string representing number.
	 * @return The formatted currency string.
	 */
	public static String formatCurrency(Object num){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatCurrency(num, locale);
	}
	
	/**
	 * Format a number to an localized number string with currency symbol specified by 3-letter currency code according to locale's currency format defined in cldr.
	 * Default fraction size in cldr currency format is 2.
	 * 
	 * @param num The number to be formatted. It can be Number or string representing number.
	 * @param currencyCode ISO 4217 3-letter code. For all legal code please refer to https://en.wikipedia.org/wiki/ISO_4217.
	 * @return The formatted currency string.
	 */
	public static String formatCurrency(Object num, String currencyCode){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		NumberFormatting nf = (NumberFormatting) I18nFactory.getInstance().getFormattingInstance(NumberFormatting.class);
		return nf.formatCurrency(num, currencyCode, locale);
	}
	
	/**
	 * Get the formatted string of default time zone in specified pattern and specified Locale.
	 * 
	 * @param date 
	 *           The object which represents date, it can be Date, Calendar, timestamp in long, or ISO string. For ISO string, please refer to https://www.w3.org/TR/NOTE-datetime.
	 * @param type
	 *           The format you want the date string show in. Currently VIP supports 12 formats, they are 
	 *           full, long, medium, short, fullDate, longDate, mediumDate, shortDate, fullTime, longTime, mediumTime, shortTime.
	 *           The first four formats combine date and time together. For the real pattern each format represents
	 * @return   
	 *           The formatted date/time string.
	 */
	public static String formatDate(Object date, String type){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		DateFormatting nf = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);
		return nf.formatDate(date, type, locale);
	}
	
	/**
	 * Get the formatted string of specified time zone in specified pattern and specified Locale.
	 * 
	 * @param date 
	 *           The object which represents date, it can be Date, Calendar, timestamp in long, or ISO string. For ISO string, please refer to https://www.w3.org/TR/NOTE-datetime.
	 * @param type
	 *           The format you want the date string show in. Currently VIP supports 12 formats, they are 
	 *           full, long, medium, short, fullDate, longDate, mediumDate, shortDate, fullTime, longTime, mediumTime, shortTime.
	 *           The first four formats combine date and time together. For the real pattern each format represents
	 * @param timeZoneID
	 *           The ID for a TimeZone, such as "America/Los_Angeles", or a custom ID such as "GMT-8:00".
	 * @return
	 *           The formatted date/time string.
	 */
	public static String formatDate(Object date, String type, String timeZoneID){
		Locale locale = com.vmware.vipclient.i18n.util.LocaleUtility
				.getLocale();
		DateFormatting nf = (DateFormatting) I18nFactory.getInstance().getFormattingInstance(DateFormatting.class);
		return nf.formatDate(date, type, timeZoneID, locale);
	}
}
