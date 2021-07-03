/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;

namespace UnitTestSingleton
{
    [TestClass]
    public class TestClient1 : AbsTestClient
    {
        public override string[] GetResStrings()
        {
            string[] strings = { "res.Singleton1", "sgtn_online_localsource", "CSHARP1" };
            return strings;
        }

        [TestMethod]
        public void TestConfig1()
        {
            DoTestConfig();
        }

        [TestMethod]
        public void TestRelease1()
        {
            DoTestRelease();
        }

        [TestMethod]
        public void TestTranslation1()
        {
            DoTestTranslation();
        }

        [TestMethod]
        public void TestMessages1()
        {
            DoTestMessages();
        }
    }
}
