/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#include "pch.h"

#include <iostream>

#include <atlstr.h>

#define SUPPRESS_LEGACY_ICU_HEADER_WARNINGS

#include <icu.h>

#include <stdlib.h>


#include "SingletonIcu.h"
#include "SingletonUtility.h"
#include "SingletonNumber.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;

String^ SingletonNumber::FormatDoubleCurrency(
	double amount, String^ locale, String^ currency) 
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);
	UChar* uszCurrency = SingletonUtility::GetUCharPtr(currency);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_CURRENCY, 0, 0, pszLocale, NULL, &status);

	UChar result[256];
	int32_t len = unum_formatDoubleCurrency(unf, amount, uszCurrency, result, 256, NULL, &status);

	unum_close(unf);

	String^ text = SingletonUtility::GetString(result);
	return text;
}

String^ SingletonNumber::FormatDouble(double amount, String^ locale)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_DEFAULT, 0, 0, pszLocale, NULL, &status);

	UChar result[256];
	unum_formatDouble(unf, amount, result, 256, NULL, &status);
	String^ text = SingletonUtility::GetString(result);
	return text;
}

String^ SingletonNumber::Format(int amount, String^ locale)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_DECIMAL, 0, 0, pszLocale, NULL, &status);

	UChar result[256];
	unum_format(unf, amount, result, 256, NULL, &status);
	String^ text = SingletonUtility::GetString(result);
	return text;
}

String^ SingletonNumber::FormatPercent(double amount, String^ locale)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_PERCENT, 0, 0, pszLocale, NULL, &status);

	UChar result[256];
	unum_formatDouble(unf, amount, result, 256, NULL, &status);
	String^ text = SingletonUtility::GetString(result);
	return text;
}

String^ SingletonNumber::FormatScientific(double amount, String^ locale)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_SCIENTIFIC, 0, 0, pszLocale, NULL, &status);

	UChar result[256];
	unum_formatDouble(unf, amount, result, 256, NULL, &status);
	String^ text = SingletonUtility::GetString(result);
	return text;
}

