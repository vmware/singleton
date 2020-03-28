using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace UnitTestSingletonIcu
{
    [TestClass]
    public class TestPlural: TestBase
    {
        private string TestOncePlural(double amount, string format, string locale, string msg = null)
        {
            string typeName = dp.GetPluralRuleType(amount, locale);
            string text = null;
            if (msg == null)
            {
                text = dm.Format(format, locale, amount);
            }
            else
            {
                text = dm.Format(format, locale, amount, msg);
            }
            return text;
        }

        [TestMethod]
        public void Test1()
        {
            string locale = "ar";
            string format = "There {0, plural, one { is one result } other { are # results }}";

            string text = TestOncePlural(10, format, locale);
            Assert.AreEqual(text, "There  are ?? results ");
            text = TestOncePlural(1, format, locale);
            Assert.AreEqual(text, "There  is one result ");
            text = TestOncePlural(0, format, locale);
            Assert.AreEqual(text, "There  are ? results ");

            locale = "en_US";
            format = "There {0, plural, one { is one result } other { are # results }}. <{1}>";
            text = TestOncePlural(10, format, locale, "addition");
            Assert.AreEqual(text, "There  are 10 results . <addition>");
            text = TestOncePlural(1, format, locale, "addition");
            Assert.AreEqual(text, "There  is one result . <addition>");
            text = TestOncePlural(0, format, locale, "addition");
            Assert.AreEqual(text, "There  are 0 results . <addition>");
        }
    }
}
