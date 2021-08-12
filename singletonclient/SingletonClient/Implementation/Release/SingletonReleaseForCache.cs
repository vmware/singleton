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
        protected ISingletonByKey _byKey;

        protected SingletonUseLocale _useSourceLocale;
        protected SingletonUseLocale _useSourceRemote;
        protected SingletonUseLocale _useDefaultLocale;

        // key: (string) locale
        // value: (SingletonUseLocale)
        private readonly Hashtable _remotePool = SingletonUtil.NewHashtable(true);
        private readonly Hashtable _sourcePool = SingletonUtil.NewHashtable(true);

        private readonly Hashtable _componentHandled = SingletonUtil.NewHashtable(true);

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public SingletonUseLocale GetUseLocale(string locale, bool asSource)
        {
            Hashtable pool = asSource ? _sourcePool : _remotePool;
            SingletonUseLocale useLocale = (SingletonUseLocale)pool[locale];
            if (useLocale == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                useLocale = (SingletonUseLocale)singletonLocale.FindItem(pool, 1);

                if (useLocale == null)
                {
                    useLocale = new SingletonUseLocale(_self, singletonLocale, _config.GetSourceLocale(), asSource);
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
                _byKey = new SingletonByKey(_config.GetSourceLocale(),
                    _config.GetDefaultLocale(), !_config.IsSourceLocaleDefault(), cacheType);
            }

            _useSourceLocale = GetUseLocale(_config.GetSourceLocale(), true);
            if (_config.IsOfflineSupported())
            {
                _update.LoadOfflineMessage(_useSourceLocale.SingletonLocale, null, true);
            }
            _useSourceRemote = GetUseLocale(_config.GetSourceLocale(), false);

            if (!_config.IsSourceLocaleDefault())
            {
                _useDefaultLocale = GetUseLocale(_config.GetDefaultLocale(), false);
            }

            return true;
        }

        protected string AdjustMessage(string key, string message = null)
        {
            if (message == null)
            {
                message = key;
            }

            if (!_config.IsProductMode())
            {
                message = "@" + message;
            }

            return message;
        }

        protected string GetSource(string component, string key)
        {
            string source;
            if (_byKey != null)
            {
                int componentIndex = _byKey.GetComponentIndex(component);
                source = _byKey.GetString(key, componentIndex, _useSourceLocale.LocaleItem, false);
                if (source != null)
                {
                    return source;
                }
            }
            else
            {
                source = _useSourceLocale.GetMessage(component, key);
            }

            if (source == null)
            {
                source = _useSourceLocale.GetMessage(component, key);
            }

            if (source == null)
            {
                source = AdjustMessage(key);
            }
            return source;
        }

        protected string GetRemote(SingletonAccessObject accessObject, string sourceInCode)
        {
            string text = accessObject.UseLocale.GetMessage(accessObject.Component, accessObject.Key);
            if (text == null)
            {
                text = (_useDefaultLocale == null) ?
                    sourceInCode : _useDefaultLocale.GetMessage(accessObject.Component, accessObject.Key);

                if (text == null)
                {
                    if (this._byKey == null)
                    {
                        text = accessObject.SourceMessage;
                    }
                    else
                    {
                        text = GetSource(accessObject.Component, accessObject.Key);
                    }
                }

                text = AdjustMessage(accessObject.Key, text);
            }
            return text;
        }

        protected string GetRaw(string locale, ISource source)
        {
            if (source == null)
            {
                return null;
            }

            SingletonUseLocale useLocale = this.GetUseLocale(locale, false);
            if (useLocale.IsSourceLocale)
            {
                if (source.GetSource() != null)
                {
                    return source.GetSource();
                }

                return this.GetSource(source.GetComponent(), source.GetKey());
            }

            string text = this.GetSource(source.GetComponent(), source.GetKey());
            if (source.GetSource() != null && text != null && text != source.GetSource())
            {
                return source.GetSource();
            }

            SingletonAccessObject accessObject = new SingletonAccessObject(useLocale, source);
            if (accessObject.IsSourceLocale())
            {
                return accessObject.SourceMessage;
            }

            if (_byKey != null)
            {
                int componentIndex = this._byKey.GetComponentIndex(accessObject.Component);
                if (componentIndex >= 0)
                {
                    string combineKey = SingletonUtil.GetCombineKey(locale, accessObject.Component);
                    if (_componentHandled[combineKey] == null)
                    {
                        _useSourceLocale.GetComponent(accessObject.Component, true);
                        ISingletonComponent componentObj = useLocale.GetComponent(accessObject.Component, true);
                        if (componentObj != null)
                        {
                            _componentHandled[combineKey] = true;
                        }
                        if (_useDefaultLocale != null)
                        {
                            _useDefaultLocale.GetComponent(accessObject.Component, true);
                        }
                    }
                }

                ISingletonByKeyLocale byKeyLocale = _byKey.GetLocaleItem(locale, false);
                string message = _byKey.GetString(accessObject.Key, componentIndex, byKeyLocale, true);
                if (message == null)
                {
                    message = AdjustMessage(accessObject.Key);
                }
                return message;
            }

            string textSource = _useSourceLocale.GetMessage(accessObject.Component, accessObject.Key);
            if (textSource == null)
            {
                return GetRemote(accessObject, null);
            }

            text = source.GetSource();
            if (textSource.Equals(text) || text == null)
            {
                return GetRemote(accessObject, textSource);
            }
            return text;
        }
    }
}
