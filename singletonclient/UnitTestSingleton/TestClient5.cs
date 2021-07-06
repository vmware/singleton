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
            string[] strings = { "res.Singleton1", "sgtn_online_testing", "CSHARP5" };
            return strings;
        }

        [TestMethod]
        public void TestTranslation5()
        {
            ISource src = access.Source("about", "about.message2", null, null);
            string translation = Translation.GetString("zh-CN", src);
            Assert.AreEqual(translation, "@about.message2");
        }
    }
}
