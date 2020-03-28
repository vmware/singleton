/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonNumber : public INumber
	{
	public:
		virtual String^ FormatDoubleCurrency(
			double amount, String^ locale, String^ currency);

		virtual String^ FormatDouble(double amount, String^ locale);
		virtual String^ Format(int amount, String^ locale);
		virtual String^ FormatPercent(double amount, String^ locale);
		virtual String^ FormatScientific(double amount, String^ locale);
		
	};
}

