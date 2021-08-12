/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient4 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton1", "sgtn_online_different", "product: CSHARP4"};
            return strings;
        }

        [TestMethod]
        public void TestTranslation4()
        {
            DoTestTranslation();
        }
    }
}
