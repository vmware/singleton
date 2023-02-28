/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.l3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vmware.vip.core.messages.utils.LocaleUtility;

public class LocalePickupTest {

	@Test
	public void testLocalePickup() {
		List<Locale> list = new ArrayList<Locale>();
		list.add(Locale.forLanguageTag("en"));
		list.add(Locale.forLanguageTag("de"));
		list.add(Locale.forLanguageTag("ja"));
		list.add(Locale.forLanguageTag("fr"));
		list.add(Locale.forLanguageTag("ko"));
		list.add(Locale.forLanguageTag("es"));
		list.add(Locale.forLanguageTag("zh-Hans"));
		list.add(Locale.forLanguageTag("zh-Hant"));
		Locale pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("en-US"));
		Assert.assertTrue("en".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("de-DE"));
		Assert.assertTrue("de".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("zh"));
		Assert.assertTrue("en".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("zh-Hans"));
		Assert.assertTrue("zh-Hans".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("zh-Hant"));
		Assert.assertTrue("zh-Hant".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("zh-CN"));
		Assert.assertTrue("zh-Hans".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("zh-Hans-CN"));
		Assert.assertTrue("zh-Hans".equalsIgnoreCase(pLocale.toLanguageTag()));
		pLocale = LocaleUtility.pickupLocaleFromList(list, Locale.forLanguageTag("es-419"));
		Assert.assertTrue("es".equalsIgnoreCase(pLocale.toLanguageTag()));
	}

}
