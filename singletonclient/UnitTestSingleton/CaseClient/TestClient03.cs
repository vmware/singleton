/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;


namespace UnitTestSingleton
{

    [TestClass]
    public class TestClient03: AbsPlanTest
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.SingletonProperties", "sgtn_offline_disk_properties", "http_response.txt", "test_define.txt",
                "product: CSHARP3" };
            return strings;
        }

        [TestMethod]
        public void Test3()
        {
            DoCommonTestConfig(access);
            DoCommonTestTranslation(access);
            DoCommonTestMessages(access);
        }
    }
}
