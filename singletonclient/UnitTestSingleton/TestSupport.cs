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

        private void CheckSetCulture(string culture, string target)
        {
            CultureHelper.SetCurrentCulture(culture);
            string currentCulture = CultureHelper.GetCurrentCulture();
            Assert.AreEqual(currentCulture, target); // check
        }

        [TestMethod]
        public void TestPropertiesParser()
        {
            string sourceAllText = BaseIo.obj().GetTestResource("TestSource");
            SingletonParserProperties p = new SingletonParserProperties();
            Hashtable ht = p.Parse(sourceAllText);

            string source = (string)ht["Contact.marketing"];
            Assert.AreEqual(source, "Marketing:");

            source = (string)ht["contact.chinese"];
            Assert.AreEqual(source, "中文");

            source = (string)ht["contact.unicode"];
            Assert.AreEqual(source, "in unicode (中文)");
        }

        [TestMethod]
        public void TestCulture()
        {
            string culture = CultureHelper.GetDefaultCulture();
            Assert.AreEqual(culture, DefaultCulture); // check

            CheckSetCulture(null, DefaultCulture);
            CheckSetCulture("", DefaultCulture);
            CheckSetCulture(SettingCulture, SettingCulture);

            CheckSetCulture(WrongCulture, DefaultCulture);
        }

        [TestMethod]
        public void TestLogger()
        {
            SingletonLogger logger = new SingletonLogger();
            logger.Log(SingletonClient.LogType.Debug, "log as debug");
            Assert.AreEqual(baseIo.GetLastConsoleText(), "--- Debug --- log as debug");
            logger.Log(SingletonClient.LogType.Info, "log as info");
            Assert.AreEqual(baseIo.GetLastConsoleText(), "--- Info --- log as info");
            logger.Log(SingletonClient.LogType.Warning, "log as warning");
            Assert.AreEqual(baseIo.GetLastConsoleText(), "--- Warning --- log as warning");
            logger.Log(SingletonClient.LogType.Error, "log as error");
            Assert.AreEqual(baseIo.GetLastConsoleText(), "--- Error --- log as error");
            logger.Log(SingletonClient.LogType.None, "log as none");
            Assert.AreEqual(baseIo.GetLastConsoleText(), "--- None --- log as none");
        }
    }
}
