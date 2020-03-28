/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#include "pch.h"

#include "SingletonIcu.h"
#include "SingletonIcuManager.h"

using namespace SingletonIcu;
using namespace SingletonIcuImplementation;


IUseIcu^ UseCldr::GetIcu()
{
	IUseIcu^ obj = gcnew SingletonIcuManager();
	return obj;
}

