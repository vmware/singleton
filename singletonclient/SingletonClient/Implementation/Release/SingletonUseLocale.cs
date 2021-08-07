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
        public ISingletonRelease Release { get; }
        public ISingletonLocale SingletonLocale { get; }
        public string Locale { get; }
        public bool IsSourceLocale { get; }
        public bool AsSource { get; }

        public Hashtable Components { get; }

        public ISingletonByKeyLocale LocaleItem { get; }

        public ILocaleMessages LocaleCache { get; }

        public SingletonUseLocale(ISingletonRelease release, ISingletonLocale singletonLocale, string sourceLocale, bool asSource)
        {
            Release = release;
            SingletonLocale = singletonLocale;
            Locale = singletonLocale.GetOriginalLocale();
            AsSource = asSource;

            ISingletonLocale singletonSourceLocale = SingletonUtil.GetSingletonLocale(sourceLocale);
            IsSourceLocale = singletonSourceLocale.GetNearLocaleList().Contains(Locale);

            ISingletonByKey byKey = Release.GetSingletonByKey();
            if (byKey != null)
            {
                LocaleItem = byKey.GetLocaleItem(Locale, asSource);
            }

            Components = SingletonUtil.NewHashtable(true);

            ICacheMessages productCache = release.GetReleaseMessages();
            LocaleCache = productCache.GetLocaleMessages(Locale, asSource);
        }

        public ISingletonComponent GetComponent(string component)
        {
            if (!Release.IsInScope(SingletonLocale, component))
            {
                return null;
            }

            bool onlineSupported = Release.GetSingletonConfig().IsOnlineSupported();
            ISingletonComponent obj = (ISingletonComponent)Components[component];
            if (obj == null)
            {
                obj = new SingletonComponent(Release, SingletonLocale, component, AsSource);

                if (!onlineSupported)
                {
                    obj.GetDataFromLocal();
                }
            }

            if (onlineSupported)
            {
                ISingletonAccessTask task = obj.GetAccessTask();
                if (task != null)
                {
                    task.Check();
                }
            }
            return obj;
        }

        public string GetMessage(string component, string key)
        {
            if (!string.IsNullOrEmpty(component) && Components[component] == null)
            {
                Components[component] = GetComponent(component);
            }

            return LocaleCache.GetString(component, key);
        }
    }
}
