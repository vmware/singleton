/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using SingletonClient.Implementation;

namespace UnitTestSingleton
{
    [TestClass]
    public abstract class AbsTestClient : AbsTestBase
    {

        public AbsTestClient()
        {
            string[] locales = { "en", "de", "zh-CN" };
            string[] components = { "about", "contact" };

            for(int i=0; i<locales.Length; i++)
            {
                for(int k=0; k<components.Length; k++)
                {
                    access.Translation().GetString(locales[i], access.Source(components[k], "$"));
                }
            }
        }

        [TestMethod]
        public void TestConfig()
        {
            ISingletonConfig configWrapper = new SingletonConfigWrapper(config);
            string productName = configWrapper.GetProduct();
            Assert.AreEqual(productName, PRODUCT);

            SingletonConfig theConfig = (SingletonConfig)config;
            string jointKey = configWrapper.GetReleaseName();
            Assert.AreEqual(jointKey, PRODUCT + "^" + VERSION);

            IConfigItem textItem = configWrapper.GetConfig().GetItem("_none");
            Assert.AreEqual(textItem, null);

            Assert.AreEqual(I18N.GetConfig(PRODUCT, null), null);
        }

        [TestMethod]
        public void TestRelease()
        {
            config = I18N.GetConfig(PRODUCT, VERSION);
            List<string> localeList = config.GetLocaleList(null);
            Assert.AreEqual(localeList.Count, 0);
            localeList = config.GetLocaleList("");
            Assert.AreEqual(localeList.Count, 0);
            localeList = config.GetLocaleList("NONE");
            Assert.AreEqual(localeList.Count, 0);
        }

        [TestMethod]
        public void TestTranslation()
        {
            ISource src = access.Source("about", null, null, null);
            Assert.AreEqual(src, null);
            src = access.Source(null, null, null, null);
            Assert.AreEqual(src, null);

            src = access.Source("about", "about.message", "_content", "_comment");
            Assert.AreEqual(src.GetComment(), "_comment");

            string translation = Translation.GetString("de", null);
            Assert.AreEqual(translation, null);

            DoGetStringGroup("TestGetString1");

            ISource srcObj = access.Source("about", "about.message");
            translation = Translation.Format("de", srcObj, "AAA");
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");
            translation = Translation.Format("de", null, "AAA");
            Assert.AreEqual(translation, null);

            Translation.SetCurrentLocale("zh-CN");
            string locale = Translation.GetCurrentLocale();
            Assert.AreEqual(locale, "zh-CN");

            Translation.SetCurrentLocale("zh-Hans");
            locale = Translation.GetCurrentLocale();
            Assert.AreEqual(locale, "zh-Hans");

            src = access.Source("about", "about.title", null, null);
            translation = Translation.Format("zh-CN", src, PRODUCT, VERSION);
            Assert.AreEqual(translation, "关于 Version 1.0.0 of Product " + PRODUCT);
            translation = Translation.Format("zh-CN", src, PRODUCT);
            Assert.AreEqual(translation, "关于 Version {1} of Product " + PRODUCT);
        }

        [TestMethod]
        public void TestMessages()
        {
            IReleaseMessages messages = access.Messages();
            List<string> localeList = messages.GetLocaleList();
            Assert.AreEqual(MatchLocale(localeList, "de"), true);
            List<string> componentList = messages.GetComponentList();
            Assert.AreEqual(componentList.Contains("about"), true);

            ILocaleMessages languageMessages = messages.GetLocaleMessages(null);
            Assert.AreEqual(languageMessages, null);

            languageMessages = messages.GetLocaleMessages("de");
            componentList = languageMessages.GetComponentList();
            Assert.AreEqual(componentList.Contains("about"), true);

            string translation = languageMessages.GetString("about", null);
            Assert.AreEqual(translation, null);
            translation = languageMessages.GetString(null, "key");
            Assert.AreEqual(translation, null);

            DoGetStringGroup("TestGetStringFromMessages1");

            IComponentMessages componentMessages = languageMessages.GetComponentMessages("about");
            Assert.AreEqual(componentMessages.GetLocale(), "de");
            Assert.AreEqual(componentMessages.GetComponent(), "about");

            ICollection<string> keys = componentMessages.GetKeys();

            Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
            languageMessages = allTranslations["de"];
            translation = languageMessages.GetString("about", "about.message");
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");
        }
    }
}
