/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonIcu;

namespace UnitTestSingletonIcu
{
    [TestClass]
    public class TestDateTime : TestBase
    {
        [TestMethod]
        public void Test1()
        {
            IDateTime dt = icu.GetDateTime();

            double seconds = 1579245087950;

            string text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT, "zh_CN", "GMT");
            Assert.AreEqual("2020/1/17 格林尼治标准时间 上午7:11:27", text);
            text = dt.GetText(seconds, DateTimeStyle.UDAT_SHORT, DateTimeStyle.UDAT_NONE, "zh_CN", "GMT+8");
            Assert.AreEqual("下午3:11", text);

            text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT, "fr_FR", "GMT");
            Assert.AreEqual("17/01/2020 07:11:27 heure moyenne de Greenwich", text);
        }
    }
}
