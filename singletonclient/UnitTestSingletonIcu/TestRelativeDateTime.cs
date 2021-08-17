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
    public class TestRelativeDateTime: TestBase
    {
        private string TestOne(double value, string locale, DisplayContext dc)
        {
            IRelativeDateTime dp = icu.GetRelativeDateTime();
            string text = dp.GetText(
                value,
                RelativeDateTimeStyle.UDAT_STYLE_LONG,
                locale,
                dc,
                //DisplayContext.UDISPCTX_LENGTH_FULL,
                //DisplayContext.UDISPCTX_STANDARD_NAMES,
                //DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU, 
                RelativeDateTimeUnit.UDAT_REL_UNIT_DAY);
            return text;
        }

        [TestMethod]
        public void Test1()
        {
            String text = TestOne(1, "zh-CN", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            Assert.AreEqual("明天", text);
        }
    }
}
