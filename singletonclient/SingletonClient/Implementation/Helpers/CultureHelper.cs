/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Globalization;
using System.Threading;

namespace SingletonClient.Implementation.Helpers
{
    public static class CultureHelper
    {
        private const string DefaultCulture = "en-US";

        public static string GetDefaultCulture()
        {
            return DefaultCulture;
        }

        public static CultureInfo GetCulture(string cultureName)
        {
            if (string.IsNullOrEmpty(cultureName))
            {
                return null;
            }

            try
            {
                CultureInfo cultureInfo1 = new System.Globalization.CultureInfo(cultureName);
                CultureInfo cultureInfo2 = new System.Globalization.CultureInfo(cultureInfo1.LCID);
                if (cultureInfo1.LCID == cultureInfo2.LCID)
                {
                    return cultureInfo1;
                }
            }
            catch (CultureNotFoundException)
            {
                if (cultureName.Contains("_"))
                {
                    return GetCulture(cultureName.Replace('_', '-'));
                }
                return null;
            }

            return null;
        }

        public static void SetCurrentCulture(string cultureName)
        {
            CultureInfo cultureInfo = GetCulture(cultureName);
            if (cultureInfo == null)
            {
                cultureInfo = GetCulture(GetDefaultCulture());
            }

            Thread.CurrentThread.CurrentCulture = cultureInfo;
            Thread.CurrentThread.CurrentUICulture = cultureInfo;
        }

        public static string GetCurrentCulture()
        {
            return Thread.CurrentThread.CurrentCulture.Name;
        }
    }
}
