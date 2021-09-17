/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;


namespace UnitTestSingleton
{

    [TestClass]
    public class TestClient01: AbsPlanTest
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.SingletonOther", "sgtn_online_testing", "http_response.txt", null,
                "product: CSHARP1" };
            return strings;
        }

        [TestMethod]
        public void Test1()
        {
            string text = Translation.GetString("de", Translation.CreateSource("about", "about.message"));
            IReleaseMessages releaseMessages = access.Release.GetMessages();
            string message = releaseMessages.GetLocaleMessages("de").GetString("about", "about.message");
            Assert.AreEqual(text, message);

            message = releaseMessages.GetLocaleMessages("zh-Hans").GetString("about", "about.message");
            Assert.AreEqual(null, message);

            access.PrepareData();
            message = releaseMessages.GetLocaleMessages("zh-Hans").GetString("about", "about.message");
            text = Translation.GetString("zh-Hans", Translation.CreateSource("about", "about.message"));
            Assert.AreEqual(false, string.IsNullOrEmpty(text));
            Assert.AreEqual(text, message);
        }
    }
}
