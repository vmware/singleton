/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
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
            IConfig cfg = I18N.LoadConfig(resStrings[0], Assembly.GetExecutingAssembly(), resStrings[1]);
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

        protected void DoGetStringGroup(string name)
        {
            Hashtable testData = BaseIo.obj().GetTestData(name);
            for (int i = 1; ; i++)
            {
                Hashtable oneData = (Hashtable)testData["_test_" + i];
                if (oneData == null)
                {
                    break;
                }

                string ty = (string)oneData["type"];
                if (ty == "GetString")
                {
                    string testLocale = (string)oneData["locale"];
                    string testKey = (string)oneData["key"];
                    string testComponent = (string)oneData["component"];
                    string testExpected = (string)oneData["expect"];
                    string testSource = (string)oneData["source"];
                    string testComment = (string)oneData["comment"];

                    ISource src = access.Source(testComponent, testKey, testSource, testComment);
                    string testTranslation = Translation.GetString(testLocale, src);
                    Assert.AreEqual(testTranslation, testExpected);
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
