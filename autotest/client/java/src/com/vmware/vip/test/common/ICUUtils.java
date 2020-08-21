package com.vmware.vip.test.common;

import java.util.Locale;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.CurrencyAmount;

public class ICUUtils {
	public static String getCurrencyFormat(Object num, String currencyCode, String locale) {
		Locale localeObj = new Locale(locale);
		NumberFormat currencyInstance =NumberFormat.getCurrencyInstance(localeObj);
        CurrencyAmount currAmt = new CurrencyAmount((Number) num, Currency.getInstance(currencyCode));
        String formatResult = currencyInstance.format(currAmt);
        return formatResult;
	}
}
