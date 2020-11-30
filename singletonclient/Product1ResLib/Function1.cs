﻿/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using SingletonClient.Implementation;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using System.Threading.Tasks;

namespace Product1ResLib
{
    public class Test
    {
        private string _localeName;
        private int _delaySeconds;
        private string _mode;  // M: main T: thread A: async

        private string _component = "about";

        public Test(string localeName, int delaySeconds)
        {
            Util1.IncreaseCount();
            _localeName = localeName;
            _delaySeconds = delaySeconds;
        }

        private async Task AsyncTask()
        {
            await Task.Run(() =>
            {
                DoTest();
            });
        }

        public async void TestInAsyncMode()
        {
            _mode = "A";
            await AsyncTask();
        }

        public void TestInDifferentThread()
        {
            _mode = "T";
            Thread th = new Thread(this.DoTest);
            th.Start();
        }

        private void Log(string text)
        {
            string threadId = Thread.CurrentThread.ManagedThreadId.ToString();
            string localeName = Util1.Translation().GetCurrentLocale();
            localeName += new string(' ', 5 - localeName.Length);

            string logText = string.Format("--- P1 --- thread: {0} [{1}] [{2}] {3}",
                threadId, _mode, localeName, text);
            Console.WriteLine(logText);
        }

        public void TestInSameThread()
        {
            _mode = "M";
            DoTest();
            DoTestExtra();
        }

        public void DoTest()
        {
            Thread.Sleep(1000 * _delaySeconds);

            ILocaleMessages data = Util1.Messages().GetLocaleMessages(ConfigConst.DefaultLocale);

            IComponentMessages cache = data.GetComponentMessages("about");
            List<string> sl = data.GetComponentList();

            if (_localeName != null)
            {
                Util1.Translation().SetCurrentLocale(_localeName);
                Log("--- set locale  --- " + _localeName + " --- ");
            }
            else
            {
                _localeName = Util1.Translation().GetCurrentLocale();
                Log("--- use default --- " + _localeName + " --- ");
            }

            ICollection keys = cache.GetKeys();
            string strKey = "second_key";
            string strValue = cache.GetString(strKey);

            string trans = Util1.Translation().GetString(_localeName, Util1.Source(_component, strKey));
            Log("--- trans without source --- " + trans);

            trans = Util1.Translation().GetString(_localeName, Util1.Source(_component, strKey, strValue));
            Log("--- trans with source    --- " + trans);

            ILocaleMessages data2 = Util1.Messages().GetLocaleMessages(_localeName);
            string trans2 = data2.GetComponentMessages(_component).GetString(strKey);
            if (string.IsNullOrEmpty(trans2))
            {
                List<string> nearLocalList = Util1.Translation().GetLocaleSupported(_localeName);
                data2 = Util1.Messages().GetLocaleMessages(nearLocalList[0]);
                trans2 = data2.GetComponentMessages(_component).GetString(strKey);
            }
            Log("--- trans from messages  --- " + trans2);

            List<string> sa = Util1.Messages().GetComponentList();

            IConfigItem configItem = Util1.Config().GetComponentAttribute(_component, ConfigConst.KeyLocales);
            IConfigItem configLocaleItem = configItem.GetArrayItem(ConfigConst.KeyLanguage, ConfigConst.DefaultLocale);
            List<string> resList = configLocaleItem.GetMapItem(ConfigConst.KeyOfflinePath).GetStringList();

            Util1.DecreaseCount();
        }

        private void DoTestExtra()
        {
            Test1.DoTest1();

            ISource src = Util1.Source(_component, "first_key");
            string text = Util1.Translation().Format(_localeName, src, "-aa-", "-bb-");
            Log("--- Format --- " + text);

            string strKey = "first_key";
            string trans = Util1.Translation().GetString(_localeName, Util1.Source(_component, strKey));
            Log("--- trans first_key --- " + trans);

            string currentLocale = Util1.Translation().GetCurrentLocale();
            Log("--- current locale --- " + currentLocale + " --- ");
        }
    }

    public class Function1
    {
        private string _component = "about";

        public void Involve()
        {
            Util1.Init();
        }

        public void UseProduct()
        {
            new Test(null, 0).TestInDifferentThread();
            //new Test(null, 0).TestInAsyncMode();
            Thread.Sleep(100);

            string localeName = "de";
            Util1.Translation().SetCurrentLocale(localeName);
            Console.WriteLine("--- set locale in main thread --- " + localeName + "-- -");

            //new Test(null, 0).TestInDifferentThread();
            //new Test(null, 0).TestInAsyncMode();

            new Test(localeName, 0).TestInSameThread();

            GetSampleMessage();
        }

        private ITranslation InitLevel3()
        {
            Assembly assembly = typeof(Values).Assembly;
            IConfig config = I18N.LoadConfig(
                "Product1ResLib.SingletonRes.Singleton", assembly, "singleton_config");

            IRelease release = I18N.GetRelease(config);
            return release.GetTranslation();
        }

        private void FullParameterCall(ITranslation translate, string key)
        {
            ISource src = translate.CreateSource(_component, key);
            string transaltionMessage = translate.GetString("de", src);
            Console.WriteLine(transaltionMessage);
        }

        private void SimpleParameterCall(ITranslation translate, string key)
        {
            translate.SetCurrentLocale("de");
            string transaltionMessage = translate.GetString(_component, key);
            Console.WriteLine(transaltionMessage);
        }

        public void GetSampleMessage()
        {
            ITranslation translate = InitLevel3();
            string key = "first_key";
            FullParameterCall(translate, key);
            SimpleParameterCall(translate, key);
        }
    }
}
