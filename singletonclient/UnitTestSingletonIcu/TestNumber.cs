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
            Assert.AreEqual(text, "€34.50");

            text = dn.FormatDoubleCurrency(34.5, "zh_CN", "CNY");
            Assert.AreEqual(text, "￥34.50");

            text = dn.FormatDouble(34.56789, "zh_CN");
            Assert.AreEqual(text, "34.568");

            text = dn.Format(34, "arab");
            Assert.AreEqual(text, "34");

            text = dn.FormatPercent(34.56789, "arab");
            Assert.AreEqual(text, "3,457%");

            text = dn.FormatScientific(34.56789, "arab");
            Assert.AreEqual(text, "3.456789E1");
        }
    }
}

