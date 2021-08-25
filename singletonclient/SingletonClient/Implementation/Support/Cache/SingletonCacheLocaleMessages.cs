/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Data;
    using SingletonClient.Implementation.Release;
    using SingletonClient.Implementation.Support.ByKey;
    using System.Collections.Generic;

    /// <summary>
    /// Comment.
    /// </summary>
    public class SingletonCacheLocaleMessages : ILocaleMessages
    {
        private readonly ISingletonRelease _release;
        private readonly ISingletonConfig _singletonConfig;
        private readonly string _cacheComponentType;
        private readonly string _locale;
        private readonly bool _asSource;
        private readonly ISingletonTable<IComponentMessages> _components = new SingletonTable<IComponentMessages>();

        private readonly ISingletonByKey _byKey;
        private ISingletonByKeyLocale _byKeyLocale;

        /// <summary>
        /// Initializes.
        /// </summary>
        /// <param name="cacheComponentType">cacheComponentType.</param>
        /// <param name="locale">locale.</param>
        public SingletonCacheLocaleMessages(ISingletonRelease release, string locale, bool asSource)
        {
            _release = release;
            _singletonConfig = release.GetSingletonConfig();
            _cacheComponentType = release.GetSingletonConfig().GetCacheComponentType();
            _locale = locale;
            _asSource = asSource;

            _byKey = _release.GetSingletonByKey();
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <returns>return.</returns>
        public string GetLocale()
        {
            return _locale;
        }

        /// <summary>
        /// ILocaleMessages
        /// </summary>
        /// <returns>list of string.</returns>
        public List<string> GetComponentList()
        {
            List<string> componentList = new List<string>();
            foreach (var key in _components.GetKeys())
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

            IComponentMessages cache = _components.GetItem(component);
            if (cache == null)
            {
                if (_singletonConfig.IsCacheByKey())
                {
                    ISingletonUseLocale useLocale = _release.GetUseLocale(_locale, _asSource);
                    cache = new SingletonByKeyCacheComponentMessages(useLocale, component);
                }
                else
                {
                    ISingletonReleaseManager singletonReleaseManager = SingletonReleaseManager.GetInstance();
                    ICacheComponentManager cacheComponentManager = singletonReleaseManager.GetCacheComponentManager(
                        _cacheComponentType);

                    cache = cacheComponentManager.NewComponentCache(_locale, component, _asSource);
                }
                _components.SetItem(component, cache);
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

            if (_byKeyLocale != null)
            {
                int componentIndex = _byKey.GetComponentIndex(component);
                return _byKey.GetString(key, componentIndex, _byKeyLocale);
            }

            if (string.IsNullOrEmpty(component))
            {
                return null;
            }

            IComponentMessages componentCache = this.GetComponentMessages(component);
            if (_byKey != null)
            {
                _byKeyLocale = _byKey.GetLocaleItem(_locale, _asSource);
            }
            return (componentCache == null) ? null : componentCache.GetString(key);
        }
    }
}
