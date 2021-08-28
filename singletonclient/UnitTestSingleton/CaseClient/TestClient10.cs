/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient10 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton2", "sgtn_online_only", "product: CSHARP10" };
            return strings;
        }

        [TestMethod]
        public void TestTranslation10()
        {
            DoTestTranslation();
        }
    }
}
