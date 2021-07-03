/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;

namespace SingletonClient.Implementation.Release
{
    public class SingletonReleaseForCache : SingletonReleaseBase
    {
        protected ISingletonUpdate _update;

        protected ICacheMessages _productCache;
        protected ISingletonByKeyRelease _byKeyRelease;

        protected ILocaleMessages _sourceCache;
        protected SingletonUseLocale _useSourceLocale;

        protected SingletonUseLocale _useDefaultLocale;

        // key: (string) locale
        // value: (SingletonUseLocale)
        private readonly Hashtable _localesTable = SingletonUtil.NewHashtable(true);
        private readonly Hashtable _sourceTable = SingletonUtil.NewHashtable(true);

        private readonly Hashtable _component_handled = SingletonUtil.NewHashtable(true);

        protected bool InitForCache()
        {
            if (_productCache != null)
            {
                return true;
            }

            _update = new SingletonUpdate(_self);

            string cacheType = _config.GetCacheType();
            ICacheManager cacheManager = _client.GetCacheManager(cacheType);

            _productCache = cacheManager.GetReleaseCache(
                _config.GetProduct(), _config.GetVersion());

            if (_config.IsCacheByKey())
            {
                _byKeyRelease = new SingletonByKeyRelease(_self, _config.GetSourceLocale(),
                    _config.GetDefaultLocale(), !_config.IsSourceLocaleDefault(), cacheType);
            }

            _useSourceLocale = GetUseLocale(_config.GetSourceLocale(), true);
            if (_config.IsOfflineSupported())
            {
                _sourceCache = _update.LoadOfflineBundle(_useSourceLocale.SingletonLocale, true);
            }

            if (!_config.IsSourceLocaleDefault())
            {
                _useDefaultLocale = GetUseLocale(_config.GetDefaultLocale(), false);
            }

            return true;
        }

        protected SingletonUseLocale GetUseLocale(string locale, bool asSource)
        {
            Hashtable pool = asSource ? _sourceTable : _localesTable;
            SingletonUseLocale useLocale = (SingletonUseLocale)pool[locale];
            if (useLocale == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                useLocale = (SingletonUseLocale)singletonLocale.FindItem(pool, 1);

                if (useLocale == null)
                {
                    useLocale = new SingletonUseLocale(singletonLocale, _config.GetSourceLocale());
                }

                foreach (var one in useLocale.SingletonLocale.GetNearLocaleList())
                {
                    if (pool[one] == null)
                    {
                        pool[one] = useLocale;
                    }
                }
            }
            return useLocale;
        }

        private ISingletonComponent GetOrAddComponent(
            SingletonUseLocale useLocale, string component, IComponentMessages componentMessages)
        {
            ISingletonComponent obj = (ISingletonComponent)useLocale.Components[component];
            if (obj == null)
            {
                obj = new SingletonComponent(_self, componentMessages, useLocale.SingletonLocale, component);
                useLocale.Components[component] = obj;

                if (!_config.IsOnlineSupported())
                {
                    obj.GetDataFromLocal();
                }
            }
            return obj;
        }

        protected ISingletonComponent GetComponent(SingletonUseLocale useLocale, string component, 
            IComponentMessages componentMessages)
        {
            if (!CheckBundleRequest(useLocale.SingletonLocale, component))
            {
                return null;
            }

            ISingletonComponent componentObj = GetOrAddComponent(useLocale, component, componentMessages);
            componentObj.GetAccessTask().CheckTimeSpan();
            return componentObj;
        }

        protected string GetBundleMessage(SingletonUseLocale useLocale, string component, string key)
        {
            if (useLocale.LocaleMessages == null)
            {
                useLocale.LocaleMessages = _releaseMessages.GetLocaleMessages(useLocale.Locale, false);
            }

            if (!string.IsNullOrEmpty(component) && useLocale.Components[component] == null)
            {
                useLocale.Components[component] = GetComponent(useLocale, component, null);
            }

            return useLocale.LocaleMessages.GetString(component, key);
        }

        protected string GetSourceMessage(string component, string key)
        {
            string source = null;
            if (_sourceCache != null)
            {
                source = _sourceCache.GetString(component, key);
            }

            if (source == null)
            {
                source = GetBundleMessage(_useSourceLocale, component, key);
            }

            return source;
        }

        protected string GetTranslationMessage(SingletonAccessObject accessObject, string textSource)
        {
            string text = GetBundleMessage(accessObject.UseLocale, accessObject.Component, accessObject.Key);
            if (text == null)
            {
                text = (_useDefaultLocale == null) ?
                    textSource : GetBundleMessage(_useDefaultLocale, accessObject.Component, accessObject.Key);

                if (text == null)
                {
                    if (this._byKeyRelease == null)
                    {
                        text = accessObject.SourceMessage;
                    }
                    else
                    {
                        text = GetSourceMessage(accessObject.Component, accessObject.Key);
                    }
                }

                if (text == null)
                {
                    text = accessObject.Key;
                }

                if (!_config.IsProductMode())
                {
                    text = "@" + text;
                }
            }
            return text;
        }

        protected string GetStringFromCache(string locale, ISource source)
        {
            if (source == null)
            {
                return null;
            }

            SingletonUseLocale useLocale = this.GetUseLocale(locale, false);
            SingletonAccessObject accessObject = new SingletonAccessObject(useLocale, source);
            if (accessObject.IsSourceLocale())
            {
                return accessObject.SourceMessage;
            }

            if (_byKeyRelease != null)
            {
                if (accessObject.UseLocale.IsSourceLocale)
                {
                    string soureMessage = GetSourceMessage(accessObject.Component, accessObject.Key);
                    if (soureMessage == null)
                    {
                        soureMessage = _config.IsProductMode() ? accessObject.Key : "@" + accessObject.Key;
                    }
                    return soureMessage;
                }

                int componentIndex = this._byKeyRelease.GetComponentIndex(accessObject.Component);
                if (componentIndex >= 0)
                {
                    string combineKey = locale + "_!_" + accessObject.Component;
                    if (_component_handled[combineKey] == null)
                    {
                        _component_handled[combineKey] = true;

                        GetComponent(useLocale, accessObject.Component, null);
                        GetComponent(_useSourceLocale, accessObject.Component, null);
                        if (_useDefaultLocale != null)
                        {
                            GetComponent(_useDefaultLocale, accessObject.Component, null);
                        }
                    }
                }

                ISingletonByKeyLocale byKeyLocale =_byKeyRelease.GetLocaleItem(locale, false);
                string message = _byKeyRelease.GetString(accessObject.Key, componentIndex, byKeyLocale, true);
                if (message == null)
                {
                    message = _config.IsProductMode() ? accessObject.Key : "@" + accessObject.Key;
                }
                return message;
            }

            string textSource = GetBundleMessage(_useSourceLocale, accessObject.Component, accessObject.Key);
            if (textSource == null)
            {
                return GetTranslationMessage(accessObject, null);
            }

            string text = source.GetSource();
            if (textSource.Equals(text) || text == null)
            {
                return GetTranslationMessage(accessObject, textSource);
            }
            return text;
        }
    }
}
