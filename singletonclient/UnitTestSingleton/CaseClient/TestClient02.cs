/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;


namespace UnitTestSingleton
{

    [TestClass]
    public class TestClient02: AbsPlanTest
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.SingletonOther", "sgtn_online_offline", "http_response.txt", "test_define.txt",
                "product: CSHARP2" };
            return strings;
        }

        [TestMethod]
        public void Test2()
        {
            ISource srcObj = access.Source("about", "about.message");
            string translation = Translation.GetString("zh-CN", srcObj);
            Assert.AreEqual("应用程序说明页。", translation);

            DoCommonTestTranslation(access);
        }
    }
}
