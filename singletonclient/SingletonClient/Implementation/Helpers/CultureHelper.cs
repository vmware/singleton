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
            if (string.IsNullOrEmpty(cultureName))
                cultureName = GetDefaultCulture();
            try
            {
                Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo(cultureName);
            }
            catch (CultureNotFoundException)
            {
                Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo(GetDefaultCulture());
            }
            Thread.CurrentThread.CurrentUICulture = Thread.CurrentThread.CurrentCulture;
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
