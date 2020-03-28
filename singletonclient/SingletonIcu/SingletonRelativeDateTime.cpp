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
#include "SingletonRelativeDateTime.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;

String^ SingletonRelativeDateTime::GetText(
	double quantity, 
	RelativeDateTimeStyle relativeDateTimeStyle,
	String^ locale, 
	DisplayContext capitalizationContext, 
	RelativeDateTimeUnit relativeDateTimeUnit)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UNumberFormat* unf = unum_open(UNUM_DECIMAL, 0, 0, pszLocale, NULL, &status);

	URelativeDateTimeFormatter* formatter = ureldatefmt_open(pszLocale,
		unf, 
		static_cast<UDateRelativeDateTimeFormatterStyle>(relativeDateTimeStyle),
		static_cast<UDisplayContext>(capitalizationContext),
		&status);

	UChar result[1024];

	ureldatefmt_format(formatter, quantity,
		static_cast<URelativeDateTimeUnit>(relativeDateTimeUnit),
		result, 1024, &status);

	String^ obj = SingletonUtility::GetString(result);

	UDisplayContextType utype = UDisplayContextType::UDISPCTX_TYPE_CAPITALIZATION;
	DisplayContextType a = DisplayContextType::UDISPCTX_TYPE_CAPITALIZATION;

	return obj;
}
