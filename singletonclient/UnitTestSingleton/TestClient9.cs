/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient9 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_online_offline",
                "product: CSHARP9" + "\n" + "default_locale: de" + "\n" + "source_locale: en-US"};
            return strings;
        }

        [TestMethod]
        public void TestTranslation9()
        {
            ISource srcObj = access.Source("about", "about.message");
            string translation = Translation.GetString("zh-CN", srcObj);
            Assert.AreEqual("应用程序说明页。", translation);
            DoTestTranslation();

            if (mixed)
            {
                DoGetStringGroup("TestGetString3");
            }
        }
    }
}
