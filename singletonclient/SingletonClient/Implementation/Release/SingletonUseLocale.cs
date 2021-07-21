/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;
using System.Collections;

namespace SingletonClient.Implementation.Release
{
    public class SingletonUseLocale
    {
        public ISingletonLocale SingletonLocale { get; }
        public string Locale { get; }
        public bool IsSourceLocale { get; }
        public bool AsSource { get; }

        public Hashtable Components { get; }
        public ILocaleMessages LocaleMessages { get; set; }

        public ISingletonByKeyLocale LocaleItem { get; }

        public SingletonUseLocale(ISingletonLocale singletonLocale, string sourceLocale, bool asSource, ISingletonByKey byKey)
        {
            SingletonLocale = singletonLocale;
            Locale = singletonLocale.GetOriginalLocale();
            AsSource = asSource;

            ISingletonLocale singletonSourceLocale = SingletonUtil.GetSingletonLocale(sourceLocale);
            IsSourceLocale = singletonSourceLocale.GetNearLocaleList().Contains(Locale);

            if (byKey != null)
            {
                LocaleItem = byKey.GetLocaleItem(Locale, asSource);
            }
            Components = SingletonUtil.NewHashtable(true);
        }
    }
}
