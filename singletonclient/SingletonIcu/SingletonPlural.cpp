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

using namespace std;


#include "SingletonIcu.h"
#include "SingletonUtility.h"
#include "SingletonPlural.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;


String^ SingletonPlural::GetPluralRuleType(double amount, String^ locale) 
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);

	UErrorCode status = U_ZERO_ERROR;
	UPluralRules* uplrules = uplrules_open(pszLocale, &status);

	String^ text = nullptr;
	if (U_SUCCESS(status)) {
		UChar result[256];
		int32_t keywdLen = uplrules_select(uplrules, amount, result, 256, &status);
		if (U_SUCCESS(status)) {
			text = SingletonUtility::GetString(result);
		}
	}

	return text;
}

