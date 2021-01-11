/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonDateTime : public IDateTime
	{
	public:
		virtual String^ GetText(
			double seconds, DateTimeStyle timeStyle, DateTimeStyle dateStyle,
			String^ locale, String^ timeZone);
	};
}


