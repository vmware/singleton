/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Product1ResLib;
using Product2ResLib;
using SingletonIcu;
using System;
using System.Threading;

namespace ClientTestApp
{
    class Program
    {
        private static int endWait = 0;
        private static int selection = 0;

        private void Init()
        {
            endWait = new Function1().Involve(endWait);
            endWait = new Function2().Involve(selection, endWait);
        }

        private void Work()
        {
            Init();

            new Function1().UseProduct();
            new Function2().UseProduct();

            while (Util1.GetCount() > 0 || Util2.GetCount() > 0)
            {
                Thread.Sleep(100);
            }
        }

        private void TestDateTime(IUseIcu icu)
        {
            IDateTime dt = icu.GetDateTime();

            DateTimeOffset ee = DateTimeOffset.UtcNow;
            double seconds = ee.ToUnixTimeMilliseconds() - 3600 * 1000;

            string text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT,
                null, null);
            Console.WriteLine("--- date 1 --- " + text);
            text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT,
                null, "GMT");
            Console.WriteLine("--- date 2 --- " + text);
            text = dt.GetText(seconds, DateTimeStyle.UDAT_SHORT, DateTimeStyle.UDAT_NONE,
                null, "GMT+8");
            Console.WriteLine("--- date 3 --- " + text);

            DateTime st = DateTime.UtcNow;
            st.ToUniversalTime();

            text = dt.GetText(seconds, DateTimeStyle.UDAT_FULL, DateTimeStyle.UDAT_SHORT,
                "fr_FR", "GMT");
            Console.WriteLine("--- date 4 --- " + text);
        }

        private void TestNumber(IUseIcu icu)
        {
            INumber dn = icu.GetNumber();
            string text = dn.FormatDoubleCurrency(34.5, "zh_CN", "EUR");
            Console.WriteLine("--- number currency --- " + text);

            text = dn.FormatDoubleCurrency(34.5, "zh_CN", "CNY");
            Console.WriteLine("--- number currency --- " + text);

            text = dn.FormatDouble(34.56789, "zh_CN");
            Console.WriteLine("--- number double --- " + text);

            text = dn.Format(34, "arab");
            Console.WriteLine("--- number int --- " + text);

            text = dn.FormatPercent(34.56789, "arab");
            Console.WriteLine("--- number percentage --- " + text);

            text = dn.FormatScientific(34.56789, "arab");
            Console.WriteLine("--- number scientific --- " + text);
        }

        private void TestOncePlural(IPlural dp, IMessage dm, double amount, string format, string locale, string msg = null)
        {
            string typeName = dp.GetPluralRuleType(amount, locale);
            string text = null;
            if (msg == null)
            {
                dm.Format(format, locale, amount);
            } else 
            {
                text = dm.Format(format, locale, amount, msg);
            }
            Console.WriteLine("--- number plural --- " + typeName + " --- " + amount + " --- " + text);
        }

        private void TestPlural(IUseIcu icu)
        {
            IPlural dp = icu.GetPlural();
            IMessage dm = icu.GetMessage();

            string locale = "ar";

            //string format = "There was a {0} on planet {1, number, integer} aa, {2, plural, one { is one result } other { are (#) results }}";
            //string format = "There was a {0} on planet {1, number, integer} aa";
            string format = "There {0, plural, one { is one result } other { are # results }}";

            TestOncePlural(dp, dm, 10, format, locale);
            TestOncePlural(dp, dm, 1, format, locale);
            TestOncePlural(dp, dm, 0, format, locale);

            locale = "en_US";
            format = "There {0, plural, one { is one result } other { are # results }}. <{1}>";
            TestOncePlural(dp, dm, 10, format, locale, "addition");
            TestOncePlural(dp, dm, 1, format, locale, "addition");
            TestOncePlural(dp, dm, 0, format, locale, "addition");
        }

        private void TestRelativeDateTime(IUseIcu icu, double value, string locale, DisplayContext dc)
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

            Console.WriteLine("--- RelativeDateTime --- " + locale + " --- " + value + " ---" + text);
        }

        private void WorkIcu(params object[] values)
        {
            IUseIcu icu= UseCldr.GetIcu();
            TestDateTime(icu);
            TestNumber(icu);
            TestPlural(icu);

            TestRelativeDateTime(icu, 1, "zh-CN", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            TestRelativeDateTime(icu, 1, "en", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            TestRelativeDateTime(icu, -1, "en", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            TestRelativeDateTime(icu, 1, "es", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
            TestRelativeDateTime(icu, -1, "es", DisplayContext.UDISPCTX_CAPITALIZATION_FOR_UI_LIST_OR_MENU);
        }

        static void Main(string[] args)
        {
            if (args.Length > 0)
            {
                selection = Convert.ToInt32(args[0]);
            }
            Console.WriteLine("Hello World!");

            new Program().Work();
            //new Program().WorkIcu("ab", 12);

            Console.WriteLine("--- End --- ");
            Thread.Sleep(1000 * endWait);
        }
    }
}
