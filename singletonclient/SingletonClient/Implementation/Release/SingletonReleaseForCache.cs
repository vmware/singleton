/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Data;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;

namespace SingletonClient.Implementation.Release
{
    public class SingletonReleaseForCache : SingletonReleaseBase
    {
        protected ISingletonUpdate _update;

        protected ICacheMessages _productCache;
        protected ISingletonByKey _byKey;

        protected ISingletonUseLocale _useSourceLocale;
        protected ISingletonUseLocale _useSourceRemote;
        protected ISingletonUseLocale _useDefaultLocale;

        private readonly ISingletonTable<ISingletonUseLocale> _remotePool = new SingletonTable<ISingletonUseLocale>();
        private readonly ISingletonTable<ISingletonUseLocale> _sourcePool = new SingletonTable<ISingletonUseLocale>();

        private readonly ISingletonTable<bool> _bundleHandled = new SingletonTable<bool>();

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonUpdate GetUpdate()
        {
            return _update;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ICacheMessages GetReleaseMessages()
        {
            return _productCache;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonByKey GetSingletonByKey()
        {
            return _byKey;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonUseLocale GetUseLocale(string locale, bool asSource)
        {
            ISingletonTable<ISingletonUseLocale> pool = asSource ? _sourcePool : _remotePool;
            ISingletonUseLocale useLocale = pool.GetItem(locale);
            if (useLocale == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                useLocale = (ISingletonUseLocale)singletonLocale.FindItem(pool, 1);

                if (useLocale == null)
                {
                    useLocale = new SingletonUseLocale(_self, singletonLocale, _config.GetSourceLocale(), asSource);
                }

                foreach (var one in useLocale.GetSingletonLocale().GetNearLocaleList())
                {
                    if (!pool.Contains(one))
                    {
                        pool.SetItem(one, useLocale);
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
            ICacheManager cacheManager = _manager.GetCacheManager(cacheType);

            _productCache = cacheManager.GetReleaseCache(
                _config.GetProduct(), _config.GetVersion());

            if (_config.IsCacheByKey())
            {
                _byKey = new SingletonByKey(_config, cacheType);
            }

            _useSourceLocale = GetUseLocale(_config.GetSourceLocale(), true);
            if (_config.IsOfflineSupported())
            {
                _update.LoadLocalMessage(_useSourceLocale.GetSingletonLocale(), null, true);
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

        protected string GetSourceMessage(string component, string key)
        {
            string source;
            if (_byKey != null)
            {
                int componentIndex = _byKey.GetComponentIndex(component);
                source = _byKey.GetString(key, componentIndex, _useSourceLocale.GetLocaleItem(), false);
                if (source == null)
                {
                    if (componentIndex >= 0 && _config.IsOnlineSupported())
                    {
                        ISingletonComponent singletonComponent = _useSourceRemote.GetComponent(component, true);
                        singletonComponent.GetAccessTask().Check();
                    }
                    source = _byKey.GetString(key, componentIndex, _useSourceRemote.GetLocaleItem(), false);
                }
            }
            else
            {
                source = _useSourceLocale.GetMessage(component, key);
                if (source == null && _config.IsOnlineSupported())
                {
                    source = _useSourceRemote.GetMessage(component, key);
                }
            }

            if (source == null)
            {
                source = AdjustMessage(key);
            }
            return source;
        }

        protected string GetRemoteMessage(ISingletonUseLocale useLocale, ISource source, string sourceInCode)
        {
            string text = useLocale.GetMessage(source.GetComponent(), source.GetKey());
            if (text == null)
            {
                text = (_useDefaultLocale == null) ?
                    sourceInCode : _useDefaultLocale.GetMessage(source.GetComponent(), source.GetKey());

                if (text == null)
                {
                    if (this._byKey == null)
                    {
                        text = source.GetSource();
                    }
                    else
                    {
                        text = GetSourceMessage(source.GetComponent(), source.GetKey());
                    }
                }

                text = AdjustMessage(source.GetKey(), text);
            }
            return text;
        }

        protected string GetRawMessage(string locale, ISource source)
        {
            if (source == null)
            {
                return null;
            }

            ISingletonUseLocale useLocale = this.GetUseLocale(locale, false);
            if (useLocale.IsSourceLocale())
            {
                if (source.GetSource() != null)
                {
                    return source.GetSource();
                }

                return this.GetSourceMessage(source.GetComponent(), source.GetKey());
            }

            string text = this.GetSourceMessage(source.GetComponent(), source.GetKey());
            if (source.GetSource() != null && text != null && text != source.GetSource())
            {
                return source.GetSource();
            }

            if (useLocale.IsSourceLocale() && source.GetSource() != null)
            {
                return source.GetSource();
            }

            if (_byKey != null)
            {
                int componentIndex = this._byKey.GetComponentIndex(source.GetComponent());
                if (componentIndex >= 0)
                {
                    string combineKey = SingletonUtil.GetCombineKey(locale, source.GetComponent());
                    if (!_bundleHandled.Contains(combineKey))
                    {
                        if (_config.IsOnlineSupported())
                        {
                            _useSourceRemote.GetComponent(source.GetComponent(), true);
                        }
                        ISingletonComponent componentObj = useLocale.GetComponent(source.GetComponent(), true);
                        if (componentObj != null)
                        {
                            _bundleHandled.SetItem(combineKey, true);
                        }
                        if (_useDefaultLocale != null)
                        {
                            _useDefaultLocale.GetComponent(source.GetComponent(), true);
                        }
                    }
                }

                ISingletonByKeyLocale byKeyLocale = _byKey.GetLocaleItem(locale, false);
                string message = _byKey.GetString(source.GetKey(), componentIndex, byKeyLocale, true);
                if (message == null)
                {
                    message = AdjustMessage(source.GetKey());
                }
                return message;
            }

            string textSource = _useSourceLocale.GetMessage(source.GetComponent(), source.GetKey());
            if (textSource == null)
            {
                return GetRemoteMessage(useLocale, source, null);
            }

            text = source.GetSource();
            if (textSource.Equals(text) || text == null)
            {
                return GetRemoteMessage(useLocale, source, textSource);
            }
            return text;
        }
    }
}
