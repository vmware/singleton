/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient.Implementation.Helpers;
using SingletonClient.Implementation.Support;


namespace UnitTestSingleton
{
    [TestClass]
    public class TestSupport
    {
        public const string DefaultCulture = "en-US";
        public const string SettingCulture = "zh-CN";
        public const string WrongCulture = "11-22";

        private BaseIo baseIo;

        public TestSupport()
        {
            baseIo = BaseIo.obj();
            SingletonBaseIo.SetInstance(BaseIo.obj());
        }

        private void CheckSetCulture(string target, string culture)
        {
            CultureHelper.SetCurrentCulture(culture);
            string currentCulture = CultureHelper.GetCurrentCulture();
            Assert.AreEqual(target, currentCulture); // check
        }

        [TestMethod]
        public void TestPropertiesParser()
        {
            string sourceAllText = BaseIo.obj().LoadResourceText("TestSource");
            SingletonParserProperties p = new SingletonParserProperties();
            Hashtable ht = p.Parse(sourceAllText);

            string source = (string)ht["Contact.marketing"];
            Assert.AreEqual("Marketing:", source);

            source = (string)ht["contact.chinese"];
            Assert.AreEqual("中文", source);

            source = (string)ht["contact.unicode"];
            Assert.AreEqual("in unicode (中文)", source);
        }

        [TestMethod]
        public void TestCulture()
        {
            string culture = CultureHelper.GetDefaultCulture();
            Assert.AreEqual(DefaultCulture, culture); // check

            CheckSetCulture(DefaultCulture, null);
            CheckSetCulture(DefaultCulture, "");
            CheckSetCulture(SettingCulture, SettingCulture);

            CheckSetCulture(DefaultCulture, WrongCulture);
        }

        [TestMethod]
        public void TestLogger()
        {
            SingletonLogger logger = new SingletonLogger();
            logger.Log(SingletonClient.LogType.Debug, "log as debug");
            Assert.AreEqual("--- Debug --- log as debug", baseIo.GetLastConsoleText());
            logger.Log(SingletonClient.LogType.Info, "log as info");
            Assert.AreEqual("--- Info --- log as info", baseIo.GetLastConsoleText());
            logger.Log(SingletonClient.LogType.Warning, "log as warning");
            Assert.AreEqual("--- Warning --- log as warning", baseIo.GetLastConsoleText());
            logger.Log(SingletonClient.LogType.Error, "log as error");
            Assert.AreEqual("--- Error --- log as error", baseIo.GetLastConsoleText());
            logger.Log(SingletonClient.LogType.None, "log as none");
            Assert.AreEqual("--- None --- log as none", baseIo.GetLastConsoleText());
        }
    }
}
