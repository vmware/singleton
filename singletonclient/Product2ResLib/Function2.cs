/*
 * Copyright 2020-2021 VMware, Inc.
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
        private static int wait = 7;

        public int Involve(int index, int endWait)
        {
            int delay = 5;
            if (index <= 5)
            {
                wait = 0;
                delay = 0;
            }
            Util2.Init(index);
            return endWait > delay ? endWait : delay;
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
            Thread.Sleep(wait * 1000);

            SetCurrentLocale("de");
            GetTranslation("about", "about.message");

            SetCurrentLocale("ur_PK");
            GetTranslation("about", "about.message");

            SetCurrentLocale("zh-Hans");
            GetTranslation("about", "about.message");

            SetCurrentLocale("zh-Hans");
            GetTranslation("index", "index.title");

            SetCurrentLocale("zh-Hans");
            GetTranslation("contact", "contact.applicationname");

            Thread.Sleep(wait * 1000);
            SetCurrentLocale("de");
            GetTranslation("about", "about.message");

            Util2.CountDown();
        }

        public void SetCurrentLocale(string locale)
        {
            Util2.Translation().SetCurrentLocale(locale);
        }
    }
}
