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
    public class TestNumber: TestBase
    {
        [TestMethod]
        public void Test1()
        {
            INumber dn = icu.GetNumber();
            string text = dn.FormatDoubleCurrency(34.5, "zh_CN", "EUR");
            Assert.AreEqual("€34.50", text);

            text = dn.FormatDoubleCurrency(34.5, "zh_CN", "CNY");
            Assert.AreEqual("￥34.50", text);

            text = dn.FormatDouble(34.56789, "zh_CN");
            Assert.AreEqual("34.568", text);

            text = dn.Format(34, "arab");
            Assert.AreEqual("34", text);

            text = dn.FormatPercent(34.56789, "arab");
            Assert.AreEqual("3,457%", text);

            text = dn.FormatScientific(34.56789, "arab");
            Assert.AreEqual("3.456789E1", text);
        }
    }
}

