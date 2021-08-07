/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient3 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton3", "sgtn_offline_disk_properties", "CSHARP3",
                "load_on_startup: true"};
            return strings;
        }

        [TestMethod]
        public void TestConfig3()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease3()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestTranslation3()
        {
            DoTestTranslation();
        }

        [TestMethod]
        public void TestMessages3()
        {
            DoTestMessages();
        }
    }
}
