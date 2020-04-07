/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using System;
using System.Collections;
using System.Diagnostics;
using System.Reflection;
using System.Threading;

namespace Product2ResLib
{
    public class Function2
    {
        public void Involve()
        {
            Util2.Init();
        }

        private void Log(string text)
        {
            string threadId = Thread.CurrentThread.ManagedThreadId.ToString();
            string localeName = Util2.Translation().GetCurrentLocale();
            localeName += new string(' ', 7 - localeName.Length);

            string logText = string.Format("--- P2 --- thread: {0} [{1}] [{2}] {3}",
                threadId, 0, localeName, text);
            Console.WriteLine(logText);
        }

        public string GetTranslation(string component, string key)
        {
            string locale = Util2.Translation().GetCurrentLocale();
            string trans = Util2.Translation().GetString(locale, Util2.Source(component, key));
            Log("--- translation --- " + component + " --- " + key + " --- " + trans);
            return trans;
        }

        public void UseProduct()
        {
            SetCurrentLocale("de");
            GetTranslation("about", "about.message");

            SetCurrentLocale("zh-Hans");
            GetTranslation("about", "about.message");

            Util2.CountDown();
        }

        public void SetCurrentLocale(string locale)
        {
            Util2.Translation().SetCurrentLocale(locale);
        }
    }
}
