/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient.Implementation;
using SingletonClient.Implementation.Support;


namespace UnitTestSingleton
{
    [TestClass]
    public class TestBaseIo
    {
        [TestMethod]
        public void TestAccessService()
        {
            SingletonAccessService service = new SingletonAccessService();
            Hashtable headers = SingletonUtil.NewHashtable(false);
            string status;
            String text = service.HttpGet("https://github.com", headers, 0, out status);
            Assert.AreEqual(true, text.Contains("GitHub"));

            headers.Clear();
            text = service.HttpGet("https://github.next.com", headers, 0, out status);
            Assert.AreEqual(true, text == null);
        }
    }
}
