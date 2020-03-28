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

            string text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT, null, "GMT");
            Assert.AreEqual(text, "2020/1/17 格林尼治标准时间 上午7:11:27");
            text = dt.GetText(seconds, DateTimeStyle.UDAT_SHORT, DateTimeStyle.UDAT_NONE, null, "GMT+8");
            Assert.AreEqual(text, "下午3:11");

            text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT, "fr_FR", "GMT");
            Assert.AreEqual(text, "17/01/2020 07:11:27 heure moyenne de Greenwich");
        }
    }
}
