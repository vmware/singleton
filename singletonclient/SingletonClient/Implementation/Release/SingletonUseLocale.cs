/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Data;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;

namespace SingletonClient.Implementation.Release
{
    public interface ISingletonUseLocale
    {
        ISingletonRelease GetRelease();
        ISingletonLocale GetSingletonLocale();
        string GetLocale();
        bool IsSourceLocale();
        bool IsOnlineSupported();
        bool IsAsSource();
        ISingletonTable<ISingletonComponent> GetComponents();
        ISingletonByKeyLocale GetLocaleItem();
        ILocaleMessages GetLocaleCache();
        ISingletonComponent GetComponent(string component, bool useRemote);
        string GetMessage(string component, string key);
    }

    public class SingletonUseLocale : ISingletonUseLocale
    {
        private readonly ISingletonRelease _release;
        private readonly ISingletonLocale _singletonLocale;

        private readonly ISingletonByKeyLocale _localeItem;
        private readonly ILocaleMessages _localeCache;

        private readonly ISingletonTable<ISingletonComponent> _components;

        private readonly string _locale;
        private readonly bool _isSourceLocale;
        private readonly bool _isOnlineSupported;
        private readonly bool _asSource;

        public SingletonUseLocale(ISingletonRelease release, ISingletonLocale singletonLocale, string sourceLocale, bool asSource)
        {
            _release = release;
            _singletonLocale = singletonLocale;
            _locale = singletonLocale.GetOriginalLocale();
            _asSource = asSource;

            ISingletonLocale singletonSourceLocale = SingletonUtil.GetSingletonLocale(sourceLocale);
            _isSourceLocale = singletonSourceLocale.GetNearLocaleList().Contains(_locale);

            ISingletonByKey byKey = _release.GetSingletonByKey();
            if (byKey != null)
            {
                _localeItem = byKey.GetLocaleItem(_locale, asSource);
            }

            _components = new SingletonTable<ISingletonComponent>();

            ICacheMessages productCache = release.GetReleaseMessages();
            _localeCache = productCache.GetLocaleMessages(_locale, asSource);

            _isOnlineSupported = _release.GetSingletonConfig().IsOnlineSupported();
        }

        public ISingletonRelease GetRelease()
        {
            return _release;
        }

        public ISingletonLocale GetSingletonLocale()
        {
            return _singletonLocale;
        }

        public string GetLocale()
        {
            return _locale;
        }

        public bool IsSourceLocale()
        {
            return _isSourceLocale;
        }

        public bool IsOnlineSupported()
        {
            return _isOnlineSupported;
        }

        public bool IsAsSource()
        {
            return _asSource;
        }

        public ISingletonTable<ISingletonComponent> GetComponents()
        {
            return _components;
        }

        public ISingletonByKeyLocale GetLocaleItem()
        {
            return _localeItem;
        }

        public ILocaleMessages GetLocaleCache()
        {
            return _localeCache;
        }

        public ISingletonComponent GetComponent(string component, bool useRemote)
        {
            ISingletonComponent obj = _components.GetItem(component);
            if (obj == null)
            {
                ISingletonLocale relateLocale;
                if (!_release.IsInScope(_singletonLocale, component, out relateLocale))
                {
                    return null;
                }

                obj = new SingletonComponent(_release, relateLocale, component, _asSource, useRemote);

                if (!_isOnlineSupported)
                {
                    obj.GetDataFromLocal();
                }
            }

            if (_isOnlineSupported && useRemote)
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
            if (!string.IsNullOrEmpty(component) && _components.GetObject(component) == null)
            {
                _components.SetItem(component, GetComponent(component, true));
            }

            return _localeCache.GetString(component, key);
        }
    }
}
