/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support;
using System.Collections;

namespace SingletonClient.Implementation.Release
{
    public class SingletonUseLocale
    {
        public ISingletonLocale SingletonLocale { get; }
        public string Locale { get; }
        public bool IsSourceLocale { get; }

        public Hashtable Components { get; }
        public ILocaleMessages LocaleMessages { get; set; }

        public SingletonUseLocale(ISingletonLocale singletonLocale, string sourceLocale)
        {
            SingletonLocale = singletonLocale;
            Locale = singletonLocale.GetOriginalLocale();

            ISingletonLocale singletonSourceLocale = SingletonUtil.GetSingletonLocale(sourceLocale);
            IsSourceLocale = singletonSourceLocale.GetNearLocaleList().Contains(Locale);

            Components = SingletonUtil.NewHashtable(true);
        }
    }
}
