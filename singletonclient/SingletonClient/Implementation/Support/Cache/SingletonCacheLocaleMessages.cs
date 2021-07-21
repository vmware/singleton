/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Support.ByKey;
    using System.Collections;
    using System.Collections.Generic;

    /// <summary>
    /// Comment.
    /// </summary>
    public class SingletonCacheLocaleMessages : ILocaleMessages
    {
        private readonly ISingletonRelease release;
        private readonly ISingletonConfig singletonConfig;
        private readonly string cacheComponentType;
        private readonly string locale;
        private readonly bool asSource;
        private readonly Hashtable components = SingletonUtil.NewHashtable(true);

        private readonly ISingletonByKey byKey;
        private ISingletonByKeyLocale byKeyLocale;

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheLocaleMessages(ISingletonRelease release, string locale, bool asSource)
        {
            this.release = release;
            this.singletonConfig = release.GetSingletonConfig();
            this.cacheComponentType = release.GetSingletonConfig().GetCacheComponentType();
            this.locale = locale;
            this.asSource = asSource;

            this.byKey = this.release.GetSingletonByKey();
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <returns>return.</returns>
        public string GetLocale()
        {
            return locale;
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <returns>list of string.</returns>
        public List<string> GetComponentList()
        {
            List<string> componentList = new List<string>();
            foreach (var key in this.components.Keys)
            {
                componentList.Add(key.ToString());
            }

            return componentList;
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <param name="component">component.</param>
        /// <returns>return.</returns>
        public IComponentMessages GetComponentMessages(string component)
        {
            if (component == null)
            {
                return null;
            }

            IComponentMessages cache = (IComponentMessages)this.components[component];
            if (cache == null)
            {
                ISingletonClientManager singletonClientManager = SingletonClientManager.GetInstance();
                if (singletonConfig.IsCacheByKey())
                {
                    cache = new SingletonByKeyCacheComponentMessages(release, this.locale, component, this.asSource);
                }
                else
                {
                    ICacheComponentManager cacheComponentManager = singletonClientManager.GetCacheComponentManager(
                        this.cacheComponentType);

                    cache = cacheComponentManager.NewComponentCache(this.locale, component, this.asSource);
                }
                this.components[component] = cache;
            }

            return cache;
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <param name="component">component.</param>
        /// <param name="key">key.</param>
        /// <returns>string.</returns>
        public string GetString(string component, string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                return null;
            }

            if (this.byKeyLocale != null)
            {
                int componentIndex = this.byKey.GetComponentIndex(component);
                return this.byKey.GetString(key, componentIndex, this.byKeyLocale);
            }

            if (string.IsNullOrEmpty(component))
            {
                return null;
            }

            IComponentMessages componentCache = this.GetComponentMessages(component);
            if (this.byKey != null)
            {
                this.byKeyLocale = byKey.GetLocaleItem(this.locale, this.asSource);
            }
            return (componentCache == null) ? null : componentCache.GetString(key);
        }
    }
}
