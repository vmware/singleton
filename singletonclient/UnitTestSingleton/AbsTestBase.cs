/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    public class TestThread
    {
        private int idThread;
        private AccessSingleton access;
        private ITranslation trans;
        private Hashtable group;
        private bool needPrint;
        private int times;

        public TestThread(int idThread, AccessSingleton access, ITranslation trans,
            Hashtable group, bool needPrint, int times)
        {
            this.idThread = idThread;
            this.access = access;
            this.trans = trans;
            this.group = group;
            this.needPrint = needPrint;
            this.times = times;
        }

        public int DoOneOperation(Hashtable oneData, bool needPrint)
        {
            string ty = (string)oneData["type"];
            if (ty == "GetString")
            {
                string testLocale = (string)oneData["locale"];
                if (testLocale == null)
                {
                    testLocale = this.trans.GetCurrentLocale();
                }
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];
                string testSource = (string)oneData["source"];
                string testComment = (string)oneData["comment"];

                ISource src = access.Source(testComponent, testKey, testSource, testComment);
                string testTranslation = this.trans.GetString(testLocale, src);
                if (needPrint)
                {
                    Console.WriteLine(String.Format("--- [{0}]{1} --- {2} --- {3} --- {4} --- {5}",
                        group["NAME"], this.idThread, testComponent, testKey, testLocale, testExpected));
                }
                Assert.AreEqual(testTranslation, testExpected);
                return (int)this.group["_interval"];
            }
            else if (ty == "SetLocale")
            {
                string testLocale = (string)oneData["locale"];
                this.trans.SetCurrentLocale(testLocale);
            }
            else if (ty == "GetStringFromLocaleMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages();
                ILocaleMessages languageMessages = messages.GetLocaleMessages(testLocale);
                string testTranslation = languageMessages.GetString(testComponent, testKey);
                Assert.AreEqual(testTranslation, testExpected);
            }
            else if (ty == "GetStringFromComponentMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages();
                ILocaleMessages languageMessages = messages.GetLocaleMessages(testLocale);
                IComponentMessages componentMessages = languageMessages.GetComponentMessages(testComponent);
                string testTranslation = componentMessages.GetString(testKey);
                Assert.AreEqual(testTranslation, testExpected);
            }
            else if (ty == "GetStringFromAllMessages")
            {
                string testLocale = (string)oneData["locale"];
                string testKey = (string)oneData["key"];
                string testComponent = (string)oneData["component"];
                string testExpected = (string)oneData["expect"];

                IReleaseMessages messages = access.Messages();
                Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
                ILocaleMessages languageMessages = allTranslations["de"];

                string testTranslation = languageMessages.GetString(testComponent, testKey);
                Assert.AreEqual(testTranslation, testExpected);
            }

            return 0;
        }

        public void DoGroupOperation()
        {
            Thread.Sleep((this.idThread % 2) * 100);
            for (int k = 0; k < this.times; k++)
            {
                bool needPrint = (k == 0) && this.needPrint;

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

        private async Task AsyncTask()
        {
            await Task.Run(() =>
            {
                DoGroupOperation();
            });
        }

        public async void TestInAsyncMode()
        {
            await AsyncTask();
        }
    }

    public class AccessSingleton
    {
        private readonly IRelease rel;

        public AccessSingleton(IConfig cfg)
        {
            rel = I18N.GetRelease(cfg);
        }

        public IConfig Config()
        {
            return rel.GetConfig();
        }

        public IRelease Release()
        {
            return rel;
        }

        public IReleaseMessages Messages()
        {
            return rel.GetMessages();
        }

        public ITranslation Translation()
        {
            return rel.GetTranslation();
        }

        public ISource Source(string component, string key, string source = null, string comment = null)
        {
            return rel.GetTranslation().CreateSource(component, key, source, comment);
        }
    }

    public abstract class AbsTestBase
    {
        protected AccessSingleton access;
        protected IConfig config;
        protected IRelease release;

        protected string product;
        protected string version;

        public AbsTestBase()
        {
            BaseIo.obj();

            string[] resStrings = GetResStrings();
            Dictionary<string, string> configMap = new Dictionary<string, string>();
            configMap.Add("$PRODUCT", resStrings[2]);

            IConfig cfg = I18N.LoadConfig(resStrings[0], Assembly.GetExecutingAssembly(), resStrings[1], configMap);
            product = cfg.GetItem(ConfigConst.KeyProduct).GetString();
            version = cfg.GetItem(ConfigConst.KeyVersion).GetString();

            access = new AccessSingleton(cfg);
            release = access.Release();
            config = I18N.GetConfig(PRODUCT, VERSION);
        }

        public abstract string[] GetResStrings();

        public string PRODUCT
        {
            get { return product; }
        }

        public string VERSION
        {
            get { return version; }
        }

        public ITranslation Translation
        {
            get { return access.Translation(); }
        }

        private string GetGroupConfig(Hashtable group, string name, string defaultText)
        {
            if (group == null)
            {
                return defaultText;
            }
            string text = (string)group[name];
            if (text == null)
            {
                text = defaultText;
            }
            return text;
        }

        protected void DoGetStringGroup(string name)
        {
            Hashtable group = BaseIo.obj().GetTestData(name);

            int times = int.Parse(GetGroupConfig(group, "TIMES", "1"));
            int threads = int.Parse(GetGroupConfig(group, "THREAD", "0"));
            int asyncNum = int.Parse(GetGroupConfig(group, "ASYNC", "0"));
            group["_interval"] = int.Parse(GetGroupConfig(group, "INTERVAL", "0"));
            group["_lock"] = new Object();
            Console.WriteLine("--- group --- {0} --- {1} ---", name, times);
            int start = System.Environment.TickCount;

            if (threads == 0 && asyncNum == 0)
            {
                group["_running"] = 0;
                TestThread testThread = new TestThread(0, this.access, this.Translation, group, true, times);
                testThread.DoGroupOperation();
            }
            else
            {
                int cut = (threads > 0) ? threads : asyncNum;
                group["_running"] = cut;
                int count = times / cut;
                TestThread[] batches = new TestThread[cut];
                for (int i = 0; i < cut; i++)
                {
                    bool needPrint = (i == 0) || (i == cut - 1);
                    TestThread testThread = new TestThread(i + 1, this.access, this.Translation, group, needPrint, count);
                    batches[i] = testThread;
                }

                if (threads == 0)
                {
                    for (int i = 0; i < cut; i++)
                    {
                        batches[i].TestInAsyncMode();
                    }
                }
                else
                {
                    for (int i = 0; i < cut; i++)
                    {
                        Thread th = new Thread(batches[i].DoGroupOperation);
                        th.Start();
                    }
                }

                while ((int)group["_running"] > 0)
                {
                    Thread.Sleep(1);
                }

                string mtype = (asyncNum == 0 ? "thread" : "async");
                Console.WriteLine("--- {0} --- {1} --- over ---", name, mtype);
            }

            if (times > 1 && (int)group["_interval"] == 0)
            {
                double span = System.Environment.TickCount - start;

                Console.WriteLine("--- time span --- {0}(s) --- {1} ---", span / 1000, times * (int)group["_test_count"]);
            }
        }

        protected bool MatchLocale(List<string> localeList, string locale)
        {
            List<string> localeNames = access.Translation().GetLocaleSupported(locale);

            foreach (var one in localeList)
            {
                List<string> names = access.Translation().GetLocaleSupported(one);
                foreach (var oneName in localeNames)
                {
                    if (names.Contains(oneName))
                        return true;
                }
            }
            return false;
        }
    }
}
