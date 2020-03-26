/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

public final class ValidationMsg {
	public final static String PRODUCTNAME_NOT_VALIDE = "Incorrect produtName(only allows letter and number)";
	public final static String VERSION_NOT_VALIDE = "Incorrect version(only allows number and dot, e.g. 1.2.0)";
	public final static String COMPONENT_NOT_VALIDE = "Incorrect component(only allows letter, number, dot, underline, dashline)";
	public final static String COMPONENTS_NOT_VALIDE = "Incorrect components(only allows letter, number, comma, dot, underline, dashline)";
	public final static String KEY_NOT_VALIDE = "Incorrect key(only allows letter, dot, underline, dashline)";
	public final static String LOCALE_NOT_VALIDE = "Incorrect locale(only allows letter, number, dot, underline, dashline)";
	public final static String LOCALES_NOT_VALIDE = "Incorrect locales(only allows letter, number, comma, dot, underline, dashline)";
	public final static String LANGUAGE_NOT_VALIDE = "Incorrect language(only allows letter, number, dot, underline, dashline)";
	public final static String REGION_NOT_VALIDE = "Incorrect region(only allows letter, number, dot, underline, dashline)";
	public final static String COMBINE_NOT_VALIDE = "Incorrect combine(only 1 or 2)";
	public final static String SCOPE_NOT_VALIDE = "Incorrect scope(allows letter, comma)";
	public final static String COLLECTSOURCE_NOT_VALIDE = "Incorrect collectsource(only allows true, false)";
	public final static String PSEUDO_NOT_VALIDE = "Incorrect pseudo(only allows true, false)";
	public final static String NUMBER_NOT_VALIDE = "Incorrect number";
	public final static String SCALE_NOT_VALIDE = "Scale is empty or scale < 0";
	public final static String SOURCEFORMAT_NOT_VALIDE = "Incorrect sourceformat(only allows letter and number)";
	public final static String INVALID_URL = "Invalid URL or no mapping resources";
	public final static String PATTERN_NOT_VALIDE = "Incorrect pattern(only allows letter)";
	public final static String PRODUCTNAME_NOT_SUPPORTED = "The product '%s' is NOT supported yet!";
}
