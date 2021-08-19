/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient8 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_online_offline", 
                "product: CSHARP8" + "\n" + "online_service_url: http://127.0.0.1:80901" + "\n" + "load_on_startup: true"};
            return strings;
        }

        [TestMethod]
        public void TestConfig8()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease8()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestMessages8()
        {
            DoTestMessages();
        }
    }
}
