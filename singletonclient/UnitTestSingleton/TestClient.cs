/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json.Linq;
using SingletonClient;
using SingletonClient.Implementation;
using static SingletonClient.Implementation.SingletonUtil;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient
    {
        private IConfig config;
        private IRelease release;

        public TestClient()
        {
            BaseIo.obj();
            Util.Init();

            config = Util.Config();
            release = Util.Release();

            Util.Translation().GetString("de", Util.Source("about", "about.message"));
        }

        [TestMethod]
        public void TestUtil()
        {
            string text = BaseIo.obj().GetHttpInfo("aaa");
            Assert.AreEqual(text, "");

            text = BaseIo.obj().HttpGet("11.22.33", null);
            Assert.AreEqual(text, "");

            text = BaseIo.obj().HttpPost("11.22.33", "44.55", null);
            Assert.AreEqual(text, null);

            config = I18N.GetConfig("CSHARP", "1.0.0");
            ISingletonConfig configWrapper = new SingletonConfigWrapper(config);
            string productName = configWrapper.GetProduct();
            Assert.AreEqual(productName, "CSHARP");

            SingletonConfig theConfig = (SingletonConfig)config;
            string jointKey = configWrapper.GetReleaseName();
            Assert.AreEqual(jointKey, "CSHARP^1.0.0");

            IConfigItem textItem = configWrapper.GetConfig().GetItem("_none");
            Assert.AreEqual(textItem, null);

            Assert.AreEqual(SingletonUtil.CheckResponseValid(null, null), ResponseStatus.NetFail);

            byte[] bytes = { 0xef, 0xbb, 0xbf, 0x31 };
            text = SingletonUtil.ConvertToText(bytes);
            Assert.AreEqual(text, "1");
            byte[] bytes2 = { 0xef, 0x31 };
            text = SingletonUtil.ConvertToText(bytes2);

            JObject obj = SingletonUtil.HttpPost(BaseIo.obj(), "__url", "body", null);
            Assert.AreEqual(obj.Count, 0);

            Assert.AreEqual(I18N.GetConfig("CSHARP", null), null);

            SingletonClientManager mgr = (SingletonClientManager)I18N.GetExtension();
            Assert.AreEqual(mgr.GetRelease(null), null);

            ICacheManager tempCache = mgr.GetCacheManager("try");
            Assert.AreEqual(tempCache.GetType() != null, true);
        }

        [TestMethod]
        public void TestRelease()
        {
            config = I18N.GetConfig("CSHARP", "1.0.0");
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
            ISource src = Util.Source("about", null, null, null);
            Assert.AreEqual(src, null);
            src = Util.Source(null, null, null, null);
            Assert.AreEqual(src, null);

            src = Util.Source("about", "about.message", "_content", "_comment");
            Assert.AreEqual(src.GetComment(), "_comment");

            string translation = Util.Translation().GetString("de", null);
            Assert.AreEqual(translation, null);

            translation = Util.Translation().GetString("de", Util.Source("about", "about.message"));
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");

            translation = Util.Translation().GetString("zh-Hans", Util.Source("about", "about.message"));
            Assert.AreEqual(translation, "应用程序说明页。");
            translation = Util.Translation().GetString("zh_Hans", Util.Source("about", "about.message"));
            Assert.AreEqual(translation, "应用程序说明页。");
            translation = Util.Translation().GetString("zh-CN", Util.Source("about", "about.message"));
            Assert.AreEqual(translation, "应用程序说明页。");

            translation = Util.Translation().Format("de", Util.Source("about", "about.message"), "AAA");
            translation = Util.Translation().Format("de", null, "AAA");
            Assert.AreEqual(translation, null);

            Util.Translation().SetCurrentLocale("zh-CN");
            string locale = Util.Translation().GetCurrentLocale();
            Assert.AreEqual(locale, "zh-CN");

            Util.Translation().SetCurrentLocale("zh-Hans");
            locale = Util.Translation().GetCurrentLocale();
            Assert.AreEqual(locale, "zh-Hans");

            src = Util.Source("about", "about.title", null, null);
            translation = Util.Translation().GetString("zh-CN", src);
            Assert.AreEqual(translation, "关于 Version {1} of Product {0}");
            translation = Util.Translation().Format("zh-CN", src, "CSHARP", "1.0.0");
            Assert.AreEqual(translation, "关于 Version 1.0.0 of Product CSHARP");
            translation = Util.Translation().Format("zh-CN", src, "CSHARP");
            Assert.AreEqual(translation, "关于 Version {1} of Product CSHARP");
        }

        [TestMethod]
        public void TestMessages()
        {
            IReleaseMessages messages = Util.Messages();
            List<String> localeList = messages.GetLocaleList();
            Assert.AreEqual(localeList.Contains("de"), true);
            List<String> componentList = messages.GetComponentList();
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

            translation = languageMessages.GetString("about", "about.message");
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");

            translation = languageMessages.GetString("else", "about.message");
            Assert.AreEqual(translation, null);

            IComponentMessages componentMessages = languageMessages.GetComponentMessages("about");
            Assert.AreEqual(componentMessages.GetLocale(), "de");
            Assert.AreEqual(componentMessages.GetComponent(), "about");

            ICollection keys = componentMessages.GetKeys();

            translation = componentMessages.GetString("about.message");
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");

            Dictionary<string, ILocaleMessages> allTranslations = messages.GetAllLocaleMessages();
            languageMessages = allTranslations["de"];
            translation = languageMessages.GetString("about", "about.message");
            Assert.AreEqual(translation, "Ihrer Bewerbungs Beschreibung Seite.");
        }
    }
}
