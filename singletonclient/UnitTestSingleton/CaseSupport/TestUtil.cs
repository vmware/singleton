/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using SingletonClient.Implementation;
using static SingletonClient.Implementation.SingletonUtil;


namespace UnitTestSingleton
{
    [TestClass]
    public class TestUtil
    {
        [TestMethod]
        public void TestSingletonUtil()
        {
            string status;
            string text = BaseIo.obj().HttpGet("11.22.33", null, 0, out status);
            Assert.AreEqual(true, string.IsNullOrEmpty(text));

            Assert.AreEqual(ResponseStatus.NetFail, SingletonUtil.CheckResponseValid(null, null));

            byte[] bytes = { 0xef, 0xbb, 0xbf, 0x31 };
            text = SingletonUtil.ConvertToText(bytes);
            Assert.AreEqual("1", text);
            byte[] bytes2 = { 0xef, 0x31 };
            text = SingletonUtil.ConvertToText(bytes2);

            SingletonReleaseManager mgr = (SingletonReleaseManager)I18N.GetExtension();
            Assert.AreEqual(null, mgr.GetRelease(null));

            ICacheManager tempCache = mgr.GetCacheManager("try");
            Assert.AreEqual(true, tempCache.GetType() != null);
        }
    }
}
