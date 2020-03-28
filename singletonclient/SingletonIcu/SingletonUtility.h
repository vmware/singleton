/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonUtility
	{
	public:
		static char* GetCharPtr(String^ text);
		static UChar* GetUCharPtr(String^ text);
		static String^ GetString(UChar* uchars);

		static va_list GetVaList(cli::array<Object^>^ values);
	};
}

