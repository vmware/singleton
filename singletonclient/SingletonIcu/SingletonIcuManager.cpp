/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#include "pch.h"

#include "SingletonIcu.h"
#include "SingletonIcuManager.h"

#include "SingletonDateTime.h"
#include "SingletonNumber.h"
#include "SingletonPlural.h"
#include "SingletonMessage.h"
#include "SingletonRelativeDateTime.h"


using namespace SingletonIcu;
using namespace SingletonIcuImplementation;


IDateTime^ SingletonIcuManager::GetDateTime()
{
	IDateTime^ obj = gcnew SingletonDateTime();
	return obj;
}

INumber^ SingletonIcuManager::GetNumber()
{
	INumber^ obj = gcnew SingletonNumber();
	return obj;
}

IPlural^ SingletonIcuManager::GetPlural()
{
	IPlural^ obj = gcnew SingletonPlural();
	return obj;
}

IMessage^ SingletonIcuManager::GetMessage()
{
	IMessage^ obj = gcnew SingletonMessage();
	return obj;
}

IRelativeDateTime^ SingletonIcuManager::GetRelativeDateTime()
{
	IRelativeDateTime^ obj = gcnew SingletonRelativeDateTime();
	return obj;
}
