﻿/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using System.Collections;

    /// <summary>
    /// Comment.
    /// </summary>
    public class SingletonCacheReleaseMessages : ICacheMessages
    {
        private readonly ISingletonRelease release;
        private readonly string cacheComponentType;
        private readonly Hashtable locales = SingletonUtil.NewHashtable(true);
        private readonly Hashtable sources = SingletonUtil.NewHashtable(true);

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheReleaseMessages(ISingletonRelease release)
        {
            this.release = release;
            this.cacheComponentType = release.GetSingletonConfig().GetCacheComponentType();
        }

        /// <summary>
        /// Get locale messages object.
        /// </summary>
        /// <param name="locale">locale.</param>
        /// <param name="asSource">asSource.</param>
        /// <returns>return.</returns>
        public ILocaleMessages GetLocaleMessages(string locale, bool asSource = false)
        {
            Hashtable table = asSource ? sources : locales;
            if (locale == null)
            {
                return null;
            }

            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            ILocaleMessages cache;
            int count = singletonLocale.GetCount();
            for (int i = 0; i < count; i++)
            {
                string nearLocale = singletonLocale.GetNearLocale(i);
                cache = (ILocaleMessages)table[nearLocale];
                if (cache != null)
                {
                    return cache;
                }
            }

            cache = new SingletonCacheLocaleMessages(release, locale, asSource);
            for (int i = 0; i < count; i++)
            {
                string nearLocale = singletonLocale.GetNearLocale(i);
                table[nearLocale] = cache;
            }

            return cache;
        }
    }
}
