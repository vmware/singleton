/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonPlural : public IPlural
	{
	public:
		virtual String^ GetPluralRuleType(double amount, String^ locale);
	};
}

