/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using SingletonClient.Implementation;

namespace UnitTestSingleton
{
    public abstract class AbsTestClient : AbsTestBase
    {
        public AbsTestClient()
        {
            Console.WriteLine("--- product --- {0} ---", this.GetResStrings()[2]);
        }

        protected void PrepareData()
        {
            string[] locales = { "en", "de", "zh-CN" };
            string[] components = { "about", "aboutadd", "contact" };

            for (int i = 0; i < locales.Length; i++)
            {
                for (int k = 0; k < components.Length; k++)
                {
                    access.Translation().GetString(locales[i], access.Source(components[k], "$"));
                }
            }
        }

        protected void DoTestConfig()
        {
            ISingletonConfig configWrapper = new SingletonConfigWrapper(null, config);
            string productName = configWrapper.GetProduct();
            Assert.AreEqual(PRODUCT, productName);

            SingletonConfig theConfig = (SingletonConfig)config;
            string jointKey = configWrapper.GetReleaseName();
            Assert.AreEqual(PRODUCT + "^" + VERSION, jointKey);

            IConfigItem textItem = configWrapper.GetConfig().GetItem("_none");
            Assert.AreEqual(null, textItem);

            Assert.AreEqual(null, I18N.GetConfig(PRODUCT, null));
        }

        protected void DoTestRelease()
        {
            config = I18N.GetConfig(PRODUCT, VERSION);
            List<string> localeList = config.GetLocaleList(null);
            Assert.AreEqual(0, localeList.Count);
            localeList = config.GetLocaleList("");
            Assert.AreEqual(0, localeList.Count);
            localeList = config.GetLocaleList("NONE");
            Assert.AreEqual(0, localeList.Count);
        }

        protected void DoTestTranslation()
        {
            ISource src = access.Source("about", null, null, null);
            Assert.AreEqual(null, src);
            src = access.Source(null, null, null, null);
            Assert.AreEqual(null, src);

            src = access.Source("about", "about.message", "_content", "_comment");
            Assert.AreEqual("_comment", src.GetComment());

            DoGetStringGroup("TestGetString1");
            DoGetStringGroup("TestGetString1T");
            DoGetStringGroup("TestGetString1A");
            DoGetStringGroup("TestGetString2");

            string translation = Translation.GetString("de", null);
            Assert.AreEqual(null, translation);

            string groupName = "TestGetStringSameLocale";
            IConfigItem configItem = config.GetItem("default_locale");
            if (configItem != null)
            {
                string defaultLocale = config.GetItem("default_locale").GetString();
                configItem = config.GetItem("source_locale");
                if (configItem != null && configItem.GetString() != defaultLocale)
                {
                    groupName = "TestGetStringDifferentLocale";
                }
            }
            DoGetStringGroup(groupName);
            DoGetStringGroup("TestGetStringTemp");

            ISource srcObj = access.Source("about", "about.message");
            translation = Translation.Format("de", srcObj, "AAA");
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", translation);
            translation = Translation.Format("de", null, "AAA");
            Assert.AreEqual(null, translation);

            Translation.SetCurrentLocale("zh-CN");
            string locale = Translation.GetCurrentLocale();
            Assert.AreEqual("zh-CN", locale);

            Translation.SetCurrentLocale("zh-Hans");
            locale = Translation.GetCurrentLocale();
            Assert.AreEqual("zh-Hans", locale);

            src = access.Source("about", "about.title", null, null);
            translation = Translation.Format("zh-CN", src, PRODUCT, VERSION);
            Assert.AreEqual("关于 Version 1.0.0 of Product " + PRODUCT, translation);
            translation = Translation.Format("zh-CN", src, PRODUCT);
            Assert.AreEqual("关于 Version {1} of Product " + PRODUCT, translation);
        }

        protected void DoTestPseudoTranslation()
        {
            DoGetStringGroup("TestGetStringPseudo");
        }

        protected void DoTestMessages()
        {
            IReleaseMessages messages = access.Messages();
            List<string> localeList = messages.GetLocaleList();
            Assert.AreEqual(true, MatchLocale(localeList, "de"));
            List<string> componentList = messages.GetComponentList();
            Assert.AreEqual(true, componentList.Contains("about"));

            ILocaleMessages languageMessages = messages.GetLocaleMessages(null);
            Assert.AreEqual(null, languageMessages);

            languageMessages = messages.GetLocaleMessages("de");
            componentList = languageMessages.GetComponentList();
            Assert.AreEqual(true, componentList.Contains("about"));

            string translation = languageMessages.GetString("about", null);
            Assert.AreEqual(null, translation);
            translation = languageMessages.GetString(null, "key");
            Assert.AreEqual(null, translation);

            DoGetStringGroup("TestGetStringFromMessages1");

            IComponentMessages componentMessages = languageMessages.GetComponentMessages("about");
            Assert.AreEqual("de", componentMessages.GetLocale());
            Assert.AreEqual("about", componentMessages.GetComponent());

            ICollection<string> keys = componentMessages.GetKeys();

            Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
            languageMessages = allTranslations["de"];
            translation = languageMessages.GetString("about", "about.message");
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", translation);
        }
    }
}
