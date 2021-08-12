/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient5 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton1", "sgtn_online_testing", "product: CSHARP5"};
            return strings;
        }

        [TestMethod]
        public void TestTranslation5()
        {
            string text = Translation.GetString("de", Translation.CreateSource("about", "about.message"));
            IReleaseMessages releaseMessages = release.GetMessages();
            string message = releaseMessages.GetLocaleMessages("de").GetString("about", "about.message");
            Assert.AreEqual(text, message);

            message = releaseMessages.GetLocaleMessages("zh-Hans").GetString("about", "about.message");
            Assert.AreEqual(message, null);

            this.PrepareData();
            message = releaseMessages.GetLocaleMessages("zh-Hans").GetString("about", "about.message");
            text = Translation.GetString("zh-Hans", Translation.CreateSource("about", "about.message"));
            Assert.AreEqual(string.IsNullOrEmpty(text), false);
            Assert.AreEqual(text, message);
        }
    }
}
