/*
 * Copyright 2020 VMware, Inc.
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
        private readonly string cacheComponentType;
        private readonly Hashtable locales = SingletonUtil.NewHashtable(true);

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheReleaseMessages(string cacheComponentType)
        {
            this.cacheComponentType = cacheComponentType;
        }

        /// <summary>
        /// Comment.
        /// </summary>
        /// <param name="locale">locale.</param>
        /// <returns>return.</returns>
        public ILocaleMessages GetLocaleMessages(string locale)
        {
            if (locale == null)
            {
                return null;
            }

            ILocaleMessages cache = (ILocaleMessages)this.locales[locale];
            if (cache == null)
            {
                cache = new SingletonCacheLocaleMessages(cacheComponentType, locale);
                this.locales[locale] = cache;
            }

            return cache;
        }
    }
}
