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
            string[] strings = { "res.Singleton3", "sgtn_offline_disk_properties" };
            return strings;
        }
    }
}
