/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vmware.vip.common.constants.ConstantsChar;

/**
 *
 * Translation Key related operations
 *
 */
public class KeyUtils {
	/**
	 * Generate the unique identification of the translation.
	 *
	 * @param component The name of component.
	 * @param fileName The file name where the source comes from
	 * @param source The English string that you want to translate.
	 * @return The unique identification of the translation
	 */
	public static String generateKey(String component, String fileName, String source) {
		StringBuffer key = new StringBuffer();
		// component section
		getSectionKey(component, key);
		if (!StringUtils.isEmpty(fileName)) {
			// fileName section
			getSectionKey(fileName, key);
		}
		// abbrOfSource section
		getSectionKey(getAbbrOfSource(source), key);
		// hashCode section
		getSectionKey(MD5Utils.getHex16Str(source), key);
		return key.toString().substring(0, key.length() - 1);
	}

	/**
	 * Generate part of key.
	 * 
	 * @param sectionString The part content of key.
	 * @param key Pre generated key.
	 */
	private static void getSectionKey(String sectionString, StringBuffer key) {
		key.append(sectionString);
		key.append(ConstantsChar.UNDERLINE);
	}

	/**
	 * Get the abbreviation of source,the abbreviation of the message that take first three words with
	 * filtering preposition and special chars(‘on’, ‘the‘).
	 *
	 * @param source The English string that you want to translate.
	 * @return The abbreviation of the source
	 */
	private static String getAbbrOfSource(String source) {
		// remove special chars
		source = source.replaceAll("[^a-zA-Z,\\r]", ConstantsChar.SPACING);
		// TODO: add the nonsense word in the future
		String[] nonsenseWords = new String[] { "on", "the", "to", "and" };
		List<String> nonsenseWordList = Arrays.asList(nonsenseWords);
		String[] words = source.split(ConstantsChar.SPACING);
		StringBuffer buffer = new StringBuffer();
		int countWord = 0;
		for (String word : words) {
			if (!StringUtils.isEmpty(word) && !nonsenseWordList.contains(word) && countWord++ < 3) {
				buffer.append(StringUtils.capitalize(StringUtils.trim(word)));
			}
			if (countWord >= 3)
				break;
		}
		return buffer.toString();
	}

	
	public static void main(String[] args) {
		System.out.println(getAbbrOfSource("Click on the 'model' to view more details and to subscribe to RSS feeds"));
		System.out.println(generateKey("JAVA", "index","Click on the model to view more details and to subscribe to RSS feeds"));
	}
}
