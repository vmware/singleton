/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonRelativeDateTime : public IRelativeDateTime
	{
	public:
		virtual String^ GetText(
			double quantity,
			RelativeDateTimeStyle relativeDateTimeStyle,
			String^ locale,
			DisplayContext capitalizationContext,
			RelativeDateTimeUnit relativeDateTimeUnit);
	};
}


