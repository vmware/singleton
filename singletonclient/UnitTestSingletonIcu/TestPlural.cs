/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

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
            Assert.AreEqual("There  are ١٠ results ", text);
            text = TestOncePlural(1, format, locale);
            Assert.AreEqual("There  is one result ", text);
            text = TestOncePlural(0, format, locale);
            Assert.AreEqual("There  are ٠ results ", text);

            locale = "en_US";
            format = "There {0, plural, one { is one result } other { are # results }}. <{1}>";
            text = TestOncePlural(10, format, locale, "addition");
            Assert.AreEqual("There  are 10 results . <addition>", text);
            text = TestOncePlural(1, format, locale, "addition");
            Assert.AreEqual("There  is one result . <addition>", text);
            text = TestOncePlural(0, format, locale, "addition");
            Assert.AreEqual("There  are 0 results . <addition>", text);
        }
    }
}
