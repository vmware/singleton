/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient6 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_online_offline", "product: CSHARP6"};
            return strings;
        }

        [TestMethod]
        public void TestConfig6()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease6()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestTranslation6()
        {
            ISource srcObj = access.Source("about", "about.message");
            string translation = Translation.GetString("zh-CN", srcObj);
            Assert.AreEqual(translation, "应用程序说明页。");
            //DoTestTranslation();
        }
    }
}
