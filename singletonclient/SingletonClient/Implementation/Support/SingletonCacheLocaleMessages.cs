/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using System.Collections;
    using System.Collections.Generic;

    /// <summary>
    /// Comment.
    /// </summary>
    public class SingletonCacheLocaleMessages : ILocaleMessages
    {
        private readonly string cacheComponentType;
        private readonly string locale;
        private readonly Hashtable components = SingletonUtil.NewHashtable(true);

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheLocaleMessages(string cacheComponentType, string locale)
        {
            this.cacheComponentType = cacheComponentType;
            this.locale = locale;
        }

        /// <summary>
        /// Get locale.
        /// </summary>
        /// <returns>return.</returns>
        public string GetLocale()
        {
            return locale;
        }

        /// <summary>
        /// Comment.
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
                ICacheComponentManager cacheComponentManager = singletonClientManager.GetCacheComponentManager(
                    this.cacheComponentType);

                cache = cacheComponentManager.NewComponentCache(this.locale, component);
                this.components[component] = cache;
            }

            return cache;
        }

        /// <summary>
        /// Comment.
        /// </summary>
        /// <returns>return.</returns>
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
        /// Comment.
        /// </summary>
        /// <param name="component">component.</param>
        /// <param name="key">key.</param>
        /// <returns>return.</returns>
        public string GetString(string component, string key)
        {
            IComponentMessages componentCache = this.GetComponentMessages(component);
            return (componentCache == null) ? null : componentCache.GetString(key);
        }
    }
}
