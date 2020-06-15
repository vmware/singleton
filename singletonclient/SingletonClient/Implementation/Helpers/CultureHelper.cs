/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;
using System.Globalization;
using System.Threading;

namespace SingletonClient.Implementation.Helpers
{
    public static class CultureHelper
    {
        private const string DefaultCulture = "en-US";
        private static readonly List<string> _cultures = new List<string> { DefaultCulture };

        public static void SetCurrentCulture(string cultureName)
        {
            Thread.CurrentThread.CurrentCulture = GetCulture(cultureName);
            Thread.CurrentThread.CurrentUICulture = Thread.CurrentThread.CurrentCulture;
        }

        public static CultureInfo GetCulture(string cultureName)
        {
            if (!string.IsNullOrEmpty(cultureName))
            {
                try
                {
                    return new System.Globalization.CultureInfo(cultureName);
                }
                catch (CultureNotFoundException)
                {
                }
            }
            return new System.Globalization.CultureInfo(GetDefaultCulture());
        }

        public static string GetDefaultCulture()
        {
            return _cultures[0];
        }

        public static string GetCurrentCulture()
        {
            return Thread.CurrentThread.CurrentCulture.Name;
        }
    }
}
