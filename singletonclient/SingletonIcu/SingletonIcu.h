/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once
#include "SingletonIcuEnum.h"

using namespace System;

namespace SingletonIcu {

	/*
	Interface to handle date and time.
	*/
	public interface class IDateTime
	{
		/*
		Get text.
		*/
		String^ GetText(
			double seconds, DateTimeStyle timeStyle, DateTimeStyle dateStyle,
			String^ locale, String^ timeZone);
	};

	/*
	Interface to handle relative date and time.
	*/
	public interface class IRelativeDateTime
	{
		String^ GetText(
			double quantity,
			RelativeDateTimeStyle relativeDateTimeStyle,
			String^ locale,
			DisplayContext capitalizationContext,
			RelativeDateTimeUnit relativeDateTimeUnit);
	};

	/*
	Interface to handle number.
	*/
	public interface class INumber
	{
		String^ FormatDoubleCurrency(
			double amount, String^ locale, String^ currency);
		String^ FormatDouble(double amount, String^ locale);
		String^ Format(int amount, String^ locale);
		String^ FormatPercent(double amount, String^ locale);
		String^ FormatScientific(double amount, String^ locale);
	};

	/*
	Interface to handle plural.
	*/
	public interface class IPlural
	{
		String^ GetPluralRuleType(double amount, String^ locale);
	};

	/*
	Interface to handle message formatting.
	*/
	public interface class IMessage
	{
		String^ Format(String^ format, String^ locale, ... cli::array<Object^>^ values);
	};

	/*
	Interface to use icu.
	*/
	public interface class IUseIcu
	{
		IDateTime^ GetDateTime();
		INumber^ GetNumber();
		IPlural^ GetPlural();
		IMessage^ GetMessage();
		IRelativeDateTime^ GetRelativeDateTime();
	};

	/*
	Factory class to get interface to use icu.
	*/
	public ref class UseCldr
	{
	public:
		static IUseIcu^ GetIcu();
	};
}
