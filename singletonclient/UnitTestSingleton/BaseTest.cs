/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;

using SingletonClient;
using YamlDotNet.RepresentationModel;


namespace UnitTestSingleton
{
    public class BaseTest
    {
        public const string Version = "1.0.0";
        public string Component { get; }
        public string Locale { get; }

        private Hashtable testData = new Hashtable();

        public BaseTest()
        {
            Component = "about";
            Locale = "de";
        }

        public void Show(string text1, string text2, object value)
        {
            string text = String.Format("--- {0} --- {1} --- {2} ---", text1, text2, value);
            Console.WriteLine(text);
        }

        public void Show(string text1, object value)
        {
            string text = String.Format("--- {0} --- {1} ---", text1, value);
            Console.WriteLine(text);
        }

        public void Show(string text1)
        {
            string text = String.Format("--- {0} ---", text1);
            Console.WriteLine(text);
        }

        public void CheckLocale(ITranslation trans, string locale)
        {
            List<string> fallbackLocale = trans.GetLocaleSupported(locale);
            string text = "";
            foreach (string one in fallbackLocale)
            {
                if (text.Length > 0)
                {
                    text += " / ";
                }
                text += one;
            }
            Show("locale", locale, text);
        }

        public bool NeedWait(string product)
        {
            return true;
        }

        public int HandleLoadService(Hashtable oneData)
        {
            string filesText = (string)oneData["files"];
            int start = int.Parse((string)oneData["start"]);
            int stop = int.Parse((string)oneData["stop"]);
            string[] files = filesText.Split(',');

            for (int k = start; k <= stop; k++)
            {
                string product = "PYTHON" + k;

                foreach (string one in files)
                {
                    BaseIo.obj().LoadOneResponse(one, product, Version);
                }
            }

            return 0;
        }

        public void RunTestData(AccessSingleton access, string name)
        {
            Hashtable group = GetTestData(name);
            if (group == null)
            {
                Console.WriteLine("--- group --- {0} --- empty ---", name);
                return;
            }

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
                WorkThread testThread = new WorkThread(this);
                testThread.Set(0, access, group, true, times);
                testThread.DoGroupOperation();
            }
            else
            {
                int cut = (threads > 0) ? threads : asyncNum;
                group["_running"] = cut;
                int count = times / cut;
                WorkThread[] batches = new WorkThread[cut];
                for (int i = 0; i < cut; i++)
                {
                    bool needPrint = (i == 0) || (i == cut - 1);
                    WorkThread testThread = new WorkThread(this);
                    testThread.Set(i + 1, access, group, needPrint, count);
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

        protected void LoadTestData(string[] files)
        {
            testData.Clear();

            foreach (string fileName in files)
            {
                BaseIo.obj().PrepareTestData(testData, fileName.Substring(0, fileName.Length - 4));
            }
        }

        protected Hashtable GetTestData(string name)
        {
            return (Hashtable)testData[name];
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

        protected AccessSingleton GetAccess(YamlMappingNode plan)
        {
            string resConfig = TextUtil.GetItemValue(plan, "config");
            resConfig = resConfig.Substring(7, resConfig.Length - 11);

            YamlMappingNode outside = TextUtil.GetMapChild(plan, "outside");
            string text = TextUtil.GetText(outside);
            IConfig cfgOutside = I18N.LoadConfigFromText(text);

            IConfig cfg = I18N.LoadConfig("res.SingletonConfig", Assembly.GetExecutingAssembly(), resConfig, cfgOutside);

            AccessSingleton access = new AccessSingleton(cfg);
            return access;
        }

        protected bool MatchLocale(AccessSingleton access, List<string> localeList, string locale)
        {
            List<string> localeNames = access.Translation.GetLocaleSupported(locale);

            foreach (var one in localeList)
            {
                List<string> names = access.Translation.GetLocaleSupported(one);
                foreach (var oneName in localeNames)
                {
                    if (names.Contains(oneName))
                        return true;
                }
            }
            return false;
        }

        protected void PrepareData(AccessSingleton access)
        {
            Show("prepare data");
            access.PrepareData();
        }

        protected void CheckFunction()
        {
            WorkThread wt = new WorkThread(this);
            Hashtable oneData = new Hashtable();

            oneData.Clear();
            oneData.Add("time", "0.2");
            wt.HandleDelay(oneData);

            oneData.Clear();
            oneData.Add("locale", "de");
            oneData.Add("as_source", "true");
            oneData.Add("format", "json");
            wt.HandleShowCache(oneData);
        }
    }
}
