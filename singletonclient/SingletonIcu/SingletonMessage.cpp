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
#include "SingletonNativeUtility.h"
#include "SingletonUtility.h"
#include "SingletonMessage.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;

String^ SingletonMessage::Format(
	String^ format, String^ locale, ... cli::array<Object^>^ values)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);
	UChar* puFormat = SingletonUtility::GetUCharPtr(format);

	UChar result[1024];
	UErrorCode status = U_ZERO_ERROR;

	va_list vaList = SingletonUtility::GetVaList(values);

	u_vformatMessage(
		pszLocale,
		puFormat,
		u_strlen(puFormat),
		result,
		1024,
		vaList,
		&status);

	String^ text = nullptr;
	if (U_SUCCESS(status)) {
		text = SingletonUtility::GetString(result);
	}

	free(vaList);
	return text;
}
