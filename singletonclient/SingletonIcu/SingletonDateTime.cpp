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
#include "SingletonDateTime.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;

String^ SingletonDateTime::GetText(
	double seconds, DateTimeStyle timeStyle, DateTimeStyle dateStyle, 
	String^ locale, String^ timeZone)
{
	char* pszLocale = SingletonUtility::GetCharPtr(locale);
	UChar* uszTimeZone = SingletonUtility::GetUCharPtr(timeZone);

	String^ obj;

	UErrorCode status = U_ZERO_ERROR;

	// Create a ICU date formatter, using only the 'short date' style format.
	UDateFormat* dateFormatter = udat_open(
		static_cast<UDateFormatStyle>(timeStyle),
		static_cast<UDateFormatStyle>(dateStyle),
		pszLocale, uszTimeZone, -1, nullptr, 0, &status);

	if (U_FAILURE(status))
	{
		std::cout << (L"Failed to create date formatter.");
		return obj;
	}

	// Determine how large the formatted string from ICU would be.
	int32_t stringSize = udat_format(
		dateFormatter, seconds, nullptr, 0, nullptr, &status);

	if (status == U_BUFFER_OVERFLOW_ERROR)
	{
		status = U_ZERO_ERROR;
		// Allocate space for the formatted string.
		auto dateString = std::make_unique<UChar[]>(stringSize + 1);

		// Format the date time into the string.
		udat_format(dateFormatter, seconds, dateString.get(), stringSize + 1, nullptr, &status);

		if (U_FAILURE(status))
		{
			std::cout << (L"Failed to format the date time.");
			return obj;
		}

		UChar* pu = dateString.get();
		obj = SingletonUtility::GetString(pu);
	}
	else
	{
		std::cout << (L"An error occured while trying to determine the size of the formatted date time.");
		return obj;
	}

	// We need to close the ICU date formatter.
	udat_close(dateFormatter);

	return obj;
}
