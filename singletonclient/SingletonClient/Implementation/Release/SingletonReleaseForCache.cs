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
                _update.LoadOfflineMessage(_useSourceLocale.SingletonLocale, true);
            }
            _useSourceRemote = GetUseLocale(_config.GetSourceLocale(), false);

            if (!_config.IsSourceLocaleDefault())
            {
                _useDefaultLocale = GetUseLocale(_config.GetDefaultLocale(), false);
            }

            return true;
        }

        protected SingletonUseLocale GetUseLocale(string locale, bool asSource)
        {
            Hashtable pool = asSource ? _sourcePool : _remotePool;
            SingletonUseLocale useLocale = (SingletonUseLocale)pool[locale];
            if (useLocale == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                useLocale = (SingletonUseLocale)singletonLocale.FindItem(pool, 1);

                if (useLocale == null)
                {
                    useLocale = new SingletonUseLocale(singletonLocale, _config.GetSourceLocale(), asSource, this._byKey);
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
                obj = new SingletonComponent(_self, componentMessages, useLocale.SingletonLocale, component, useLocale.AsSource);
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
            ISingletonAccessTask task = componentObj.GetAccessTask();
            if (task != null)
            {
                task.Check();
            }
            return componentObj;
        }

        protected string GetMessage(SingletonUseLocale useLocale, string component, string key)
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
                source = GetMessage(_useSourceLocale, component, key);
            }

            if (source == null)
            {
                source = GetMessage(_useSourceRemote, component, key);
            }

            if (source == null)
            {
                source = AdjustMessage(key);
            }
            return source;
        }

        protected string GetRemote(SingletonAccessObject accessObject, string sourceInCode)
        {
            string text = GetMessage(accessObject.UseLocale, accessObject.Component, accessObject.Key);
            if (text == null)
            {
                text = (_useDefaultLocale == null) ?
                    sourceInCode : GetMessage(_useDefaultLocale, accessObject.Component, accessObject.Key);

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
                    string combineKey = locale + "_!_" + accessObject.Component;
                    if (_componentHandled[combineKey] == null)
                    {
                        GetComponent(_useSourceLocale, accessObject.Component, null);
                        ISingletonComponent componentObj = GetComponent(useLocale, accessObject.Component, null);
                        if (componentObj != null)
                        {
                            _componentHandled[combineKey] = true;
                        }
                        if (_useDefaultLocale != null)
                        {
                            GetComponent(_useDefaultLocale, accessObject.Component, null);
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

            string textSource = GetMessage(_useSourceLocale, accessObject.Component, accessObject.Key);
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
