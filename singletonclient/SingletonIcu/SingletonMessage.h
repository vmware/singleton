/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonMessage : public IMessage
	{
	public:
		virtual String^ Format(
			String^ format, String^ locale, ... cli::array<Object^>^ values);
	};
}

