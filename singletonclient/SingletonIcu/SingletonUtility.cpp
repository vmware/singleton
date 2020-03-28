/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

#include "pch.h"

#include <iostream>

#include <atlstr.h>

#include <icu.h>

#include "SingletonUtility.h"

using namespace System::Runtime::InteropServices;
using namespace SingletonIcuImplementation;

char* SingletonUtility::GetCharPtr(String^ text)
{
	char* psz = (text == nullptr) ?
		nullptr : (char*)(void*)Marshal::StringToHGlobalAnsi(text);

	return psz;
}

UChar* SingletonUtility::GetUCharPtr(String^ text)
{
	UChar* usz = nullptr;
	if (text != nullptr)
	{
		UChar* uchars = (UChar*)malloc(text->Length * sizeof(UChar));
		char* psz = (char*)(void*)Marshal::StringToHGlobalAnsi(text);
		u_uastrcpy(uchars, psz);
		usz = uchars;
	}
	return usz;
}

String^ SingletonUtility::GetString(UChar* uchars)
{
	if (uchars == nullptr) 
	{
		return nullptr;
	}
	char ar[1024];
	DWORD dBufSize = WideCharToMultiByte(
		CP_OEMCP, 0, (wchar_t*)uchars, -1, ar, 1024, NULL, FALSE);
	std::cout << "--- string --- " << ar << std::endl;

	String^ text = Marshal::PtrToStringAnsi((IntPtr)ar);
	return text;
}

va_list SingletonUtility::GetVaList(cli::array<Object^>^ values)
{
	int nSize = 0;
	for (int i = 0; i < values->Length; i++)
	{
		Object^ obj = values[i];
		String^ objectType = obj->GetType()->ToString();
		if (objectType == "System.Double")
		{
			nSize += sizeof(double);
		}
		else if (objectType == "System.String")
		{
			nSize += sizeof(char*);
		}
	}

	char* m = (char*)malloc(nSize);
	char* current = m;

	for (int i = 0; i < values->Length; i++)
	{
		Object^ obj = values[i];
		String^ objectType = obj->GetType()->ToString();
		if (objectType == "System.Double")
		{
			*(double*)current = (double)obj;
			current += sizeof(double);
		}
		else if (objectType == "System.String")
		{
			*(UChar**)current = GetUCharPtr((String^)obj);
			current += sizeof(UChar*);
		}
	}

	va_list va = (va_list)m;
	return va;
}
