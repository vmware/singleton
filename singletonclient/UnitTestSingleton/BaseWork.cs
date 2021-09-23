/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;


namespace UnitTestSingleton
{
    public class BaseWork
    {
        protected BaseTest baseTest;
        protected int idThread;
        protected AccessSingleton access;
        protected Hashtable group;
        protected bool printNeed;
        protected int times;

        public void Set(int idThread, AccessSingleton access, Hashtable group, bool needPrint, int times)
        {
            this.idThread = idThread;
            this.access = access;
            this.group = group;
            this.printNeed = needPrint;
            this.times = times;
        }

        public void EndTest()
        {
            lock (group["_lock"])
            {
                if ((int)group["_running"] > 0)
                {
                    group["_running"] = (int)group["_running"] - 1;
                }
            }
        }

        public void DoGroupOperation()
        {
            Thread.Sleep((this.idThread % 2) * 100);
            for (int k = 0; k < this.times; k++)
            {
                bool needPrint = (k == 0) && this.printNeed;

                for (int i = 1; ; i++)
                {
                    Hashtable oneData = (Hashtable)group["_test_" + i];
                    if (oneData == null)
                    {
                        break;
                    }

                    int delay = DoOneOperation(oneData, needPrint);
                    if (delay > 0)
                    {
                        Thread.Sleep(delay);
                    }
                }

            }
            EndTest();
        }

        public int HandleGetString(Hashtable oneData, bool needPrint)
        {
            string testLocale = (string)oneData["locale"];
            if (testLocale == null)
            {
                testLocale = access.Translation.GetCurrentLocale();
            }
            string testKey = (string)oneData["key"];
            string testComponent = (string)oneData["component"];
            string testExpected = (string)oneData["expect"];
            string testSource = (string)oneData["source"];
            string testComment = (string)oneData["comment"];

            ISource src = access.Source(testComponent, testKey, testSource, testComment);
            string testTranslation = access.Translation.GetString(testLocale, src);
            if (needPrint)
            {
                Console.WriteLine(String.Format("--- [{0}]{1} --- {2} --- {3} --- {4} --- {5}",
                    group["NAME"], this.idThread, testComponent, testKey, testLocale, testExpected));
            }
            if (testTranslation != testExpected)
            {
                baseTest.RunTestData(access, "TestShowCache");
                testTranslation = access.Translation.GetString(testLocale, src);
            }
            Assert.AreEqual(testExpected, testTranslation);
            return (int)this.group["_interval"];
        }

        public int HandleDelay(Hashtable oneData)
        {
            double delay = double.Parse((string)oneData["time"]);
            int ms = (int)(delay * 1000);
            Thread.Sleep(ms);
            return 0;
        }

        public int HandleShowCache(Hashtable oneData)
        {
            string locale = (string)oneData["locale"];
            bool asSource = (string)oneData["as_source"] == "true";

            Console.WriteLine(String.Format("--- {0} --- {1} ---", asSource ? "as source" : "revisable", locale));
            if (access != null)
            {
                ILocaleMessages localeMessages = access.Messages.GetLocaleMessages(locale, asSource);
                foreach (string component in localeMessages.GetComponentList())
                {
                    IComponentMessages componentMessages = localeMessages.GetComponentMessages(component);
                    ICollection<string> keys = componentMessages.GetKeys();
                    if (keys.Count > 0)
                    {
                        Console.WriteLine(string.Format("    {0}:", component));
                    }
                    foreach (string key in keys)
                    {
                        Console.WriteLine(string.Format("        [ {0} ]  {1}", key, componentMessages.GetString(key)));
                    }
                }
            }

            return 0;
        }

        private int DoOneOperation(Hashtable oneData, bool needPrint)
        {
            string ty = (string)oneData["type"];
            if (ty == "GetString")
            {
                return HandleGetString(oneData, needPrint);
            }
            else if (ty == "SetLocale")
            {
                string testLocale = (string)oneData["locale"];
                access.Translation.SetCurrentLocale(testLocale);
            }
            else if (ty == "LoadService")
            {
                return baseTest.HandleLoadService(oneData);
            }
            else if (ty == "Delay")
            {
                return HandleDelay(oneData);
            }
            else if (ty == "ShowCache")
            {
                return HandleShowCache(oneData);
            }
            else if (ty == "GetStringFromLocaleMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages;
                ILocaleMessages languageMessages = messages.GetLocaleMessages(testLocale);
                string testTranslation = languageMessages.GetString(testComponent, testKey);
                Assert.AreEqual(testExpected, testTranslation);
            }
            else if (ty == "GetStringFromComponentMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages;
                ILocaleMessages languageMessages = messages.GetLocaleMessages(testLocale);
                IComponentMessages componentMessages = languageMessages.GetComponentMessages(testComponent);
                string testTranslation = componentMessages.GetString(testKey);
                Assert.AreEqual(testExpected, testTranslation);
            }
            else if (ty == "GetStringFromAllMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages;
                Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
                ILocaleMessages languageMessages = allTranslations["de"];

                string testTranslation = languageMessages.GetString(testComponent, testKey);
                Assert.AreEqual(testExpected, testTranslation);
            }

            return 0;
        }
    }
}
