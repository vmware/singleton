/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegExpValidatorUtils {
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/*
	 * is letter
	 */
	public static boolean IsLetter(String str) {
		String regex = "^[A-Za-z]+$";
		return match(regex, str);
	}

	/*
	 * is letter array
	 */
	public static boolean isLetterArray(String str) {
		String regex = "^[A-Za-z\\,\\s]+$";
		return match(regex, str);
	}
	/*
	 * is letter or number
	 */
	public static boolean IsLetterOrNumber(String str) {
		String regex = "^[A-Za-z0-9]+$";
		return match(regex, str);
	}

    /*
     * Validate string is number or not.
     */
	public static boolean isNumeric(String number) {
        String reg = "-?[0-9]+\\.?[0-9]*";
        return number.matches(reg);
    }
	/*
	 * is number and dot and length is (5,10)
	 */
	public static boolean IsNumberAndDot(String str) {
		String regex = "^[0-9\\.]+";
		return match(regex, str);
	}

	/*
	 * is number dot and comm
	 */
	public static boolean IsNumberDotAndComm(String str) {
		String regex = "^[0-9\\.\\,]+";
		return match(regex, str);
	}
	/*
	 * is number and dot and length is (5,10)
	 */
	public static boolean isOneOrTwo(String str) {
		String regex = "^[1-2]";
		return match(regex, str);
	}
	/*
	 * is letter and -, _
	 */
	public static boolean IsLetterAndNumberAndValidchar(String str) {
		String regex = "^[A-Za-z0-9_\\-\\.]+$";
		return match(regex, str);
	}
	
	/*
	 * is letter and -, _, #
	 */
	public static boolean isLetterNumbPoundAndValidchar(String str) {
		String regex = "^[A-Za-z0-9_\\-\\.#]+$";
		return match(regex, str);
	}
	
	/*
	 * is letter comma， -, _, 
	 */
	public static boolean isLetterNumbPoundCommAndValidchar(String str) {
		String regex = "^[A-Za-z0-9_\\-\\.\\,\\s#]+$";
		return match(regex, str);
	}
	/*
	 * is letter number,comma, -, _
	 */
	public static boolean isLetterNumbCommaAndValidchar(String str) {
		String regex = "^[A-Za-z0-9_\\-\\.\\,\\s]+$";
		return match(regex, str);
	}
	
	/*
	 * is letter and -, _
	 */
	public static boolean IsLetterAndValidchar(String str) {
		String regex = "^[A-Za-z_\\-\\.]+$";
		return match(regex, str);
	}

	/*
	 * is true or false
	 */
	public static boolean IsTrueOrFalse(String b) {
		String regex = "^(true|false)$";
		return match(regex, b);
	}
	
	/**
	 * 
	 * return true when the input parameter only contain ASCII code
	 * 
	 */
	public static boolean isAscii(String inputStr) {
		return inputStr.matches("\\A\\p{ASCII}*\\z");
	}

	/**
	 * validate if the inputstr contains html tags
	 *
	 * @param inputStr
	 * @return
	 */
	public static boolean containsHTML(String inputStr) {
		if(inputStr != null) {
			String p = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
			return Pattern.compile(p).matcher(inputStr).find();
		}
		return false;
	}

	public static boolean startLetterAndCommValidchar(String str) {
		String regex = "^[A-Za-z\\^][\\w\\,\\-\\(\\)]+$";
		return match(regex, str);
	}



	public static void main(String[] args) {
		System.out.println(RegExpValidatorUtils.IsLetter("bba"));
		System.out.println(RegExpValidatorUtils.IsNumberAndDot("1.9.0"));
		System.out.println(RegExpValidatorUtils.isNumeric("1.9.0"));
		System.out.println(RegExpValidatorUtils.isNumeric("1.90"));
		System.out.println(RegExpValidatorUtils.IsLetterAndValidchar("zhcn.-_"));
		System.out.println(RegExpValidatorUtils.IsTrueOrFalse("false"));
		System.out.println(RegExpValidatorUtils.isLetterNumbPoundAndValidchar("#zhCN55.-_#"));
	}
}
