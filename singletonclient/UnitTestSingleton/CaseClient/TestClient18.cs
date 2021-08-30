/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient18 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_online_pseudo", "product: CSHARP18"};
            return strings;
        }

        [TestMethod]
        public void TestTranslation18()
        {
            DoTestPseudoTranslation();
        }
    }
}
