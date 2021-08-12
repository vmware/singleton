/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient2 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_offline_disk_resx",
                "product: CSHARP2" + "\n" + "load_on_startup: true"};
            return strings;
        }

        [TestMethod]
        public void TestConfig2()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease2()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestTranslation2()
        {
            DoTestTranslation();
        }

        [TestMethod]
        public void TestMessages2()
        {
            DoTestMessages();
        }
    }
}
