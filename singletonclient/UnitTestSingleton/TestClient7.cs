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
            string[] strings = { "res.Singleton1", "sgtn_debug", "product2: CSHARP7"};
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
            ISource srcObj = null;
            string translation = null;

            srcObj = access.Source("contact", "contact.support");
            translation = Translation.GetString("en-US", srcObj);
            Assert.AreEqual("Support:", translation);

            srcObj = access.Source("about", "about.message");
            translation = Translation.GetString("ZH-hans", srcObj);
            Assert.AreEqual("应用程序说明页。", translation);

            srcObj = access.Source("contact", "contact.support");
            translation = Translation.GetString("ZH-hans", srcObj);
            Assert.AreEqual("支持：", translation);
        }
    }
}
