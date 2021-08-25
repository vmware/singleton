/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Data;
    using SingletonClient.Implementation.Release;

    /// <summary>
    /// Comment.
    /// </summary>
    public class SingletonCacheReleaseMessages : ICacheMessages
    {
        private readonly ISingletonRelease release;

        // ILocaleMessages of bundles
        private readonly ISingletonTable<ILocaleMessages> locales = new SingletonTable<ILocaleMessages>();
        // ILocaleMessages of local source
        private readonly ISingletonTable<ILocaleMessages> sources = new SingletonTable<ILocaleMessages>();

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheReleaseMessages(ISingletonRelease release)
        {
            this.release = release;
        }

        /// <summary>
        /// ICacheMessages
        /// </summary>
        /// <param name="locale">locale.</param>
        /// <param name="asSource">asSource.</param>
        /// <returns>ILocaleMessages.</returns>
        public ILocaleMessages GetLocaleMessages(string locale, bool asSource = false)
        {
            ISingletonTable<ILocaleMessages> table = asSource ? sources : locales;
            if (locale == null)
            {
                return null;
            }

            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            ILocaleMessages cache = (ILocaleMessages)singletonLocale.FindItem(table, 0);
            if (cache != null)
            {
                return cache;
            }

            cache = new SingletonCacheLocaleMessages(release, locale, asSource);
            singletonLocale.SetItems(table, cache);

            return cache;
        }
    }
}
