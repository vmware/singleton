/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient7 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton1", "sgtn_debug", "product: CSHARP19"};
            return strings;
        }

        [TestMethod]
        public void TestConfig7()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease7()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestTranslation7()
        {
            ISource srcObj = access.Source("about", "about.message");
            string translation = Translation.GetString("ZH-hans", srcObj);
            Assert.AreEqual(translation, "应用程序说明页。");

            srcObj = access.Source("contact", "contact.support");
            translation = Translation.GetString("ZH-hans", srcObj);
            Assert.AreEqual(translation, "支持：");
        }
    }
}
