/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#pragma once

#include "SingletonIcu.h"

using namespace System;
using namespace SingletonIcu;

namespace SingletonIcuImplementation {

	public ref class SingletonIcuManager : public IUseIcu
	{
	public:
		virtual IDateTime^ GetDateTime();
		virtual INumber^ GetNumber();
		virtual IPlural^ GetPlural();
		virtual IMessage^ GetMessage();
		virtual IRelativeDateTime^ GetRelativeDateTime();
	};

	public class SingletonUtil
	{

	};

}


