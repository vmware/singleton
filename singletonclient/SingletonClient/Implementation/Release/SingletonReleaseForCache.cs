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

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonUseLocale GetSourceUseLocale()
        {
            return this._useSourceLocale;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonUseLocale GetRemoteSourceUseLocale()
        {
            return this._useSourceRemote;
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

            _useSourceRemote = GetUseLocale(_config.GetSourceLocale(), false);
            _useSourceLocale = GetUseLocale(_config.GetSourceLocale(), true);
            if (_config.IsOfflineSupported())
            {
                _update.LoadLocalMessage(_useSourceLocale.GetSingletonLocale(), null, true, false);
            }

            if (!_config.IsSourceLocaleDefault())
            {
                _useDefaultLocale = GetUseLocale(_config.GetDefaultLocale(), false);
            }

            return true;
        }

        protected string CheckWithKey(string message, ISource source)
        {
            if (message == null)
            {
                if (source.GetSource() != null)
                {
                    message = source.GetSource();
                    if (_config.IsPseudo())
                    {
                        message = SingletonUtil.AddPseudo(message);
                    }
                }
                else
                {
                    message = source.GetKey();
                }

                if (!_config.IsProductMode())
                {
                    message = "@" + message;
                }
            }
            return message;
        }

        protected string GetSourceMessage(ISource source)
        {
            string sourceText;
            if (_byKey != null)
            {
                int componentIndex = _byKey.GetComponentIndex(source.GetComponent());
                sourceText = _byKey.GetString(source.GetKey(), componentIndex, _useSourceLocale.GetLocaleItem(), false);
                if (sourceText == null)
                {
                    if (componentIndex >= 0 && _config.IsOnlineSupported())
                    {
                        ISingletonComponent singletonComponent = _useSourceRemote.GetComponent(source.GetComponent(), true);
                        ISingletonAccessTask accessTask = singletonComponent != null ? singletonComponent.GetAccessTask() : null;
                        if (accessTask != null)
                        {
                            accessTask.Check();
                        }
                    }
                    sourceText = _byKey.GetString(source.GetKey(), componentIndex, _useSourceRemote.GetLocaleItem(), false);
                }
            }
            else
            {
                sourceText = _useSourceLocale.GetMessage(source.GetComponent(), source.GetKey());
                if (sourceText == null && _config.IsOnlineSupported())
                {
                    sourceText = _useSourceRemote.GetMessage(source.GetComponent(), source.GetKey());
                }
            }

            return CheckWithKey(sourceText, source);
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
                        text = GetSourceMessage(source);
                    }
                }
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

                return this.GetSourceMessage(source);
            }

            if (!_config.IsPseudo())
            {
                string soureText = this.GetSourceMessage(source);
                if (source.GetSource() != null && soureText != null && soureText != source.GetSource())
                {
                    return source.GetSource();
                }
            }

            if (_byKey != null)
            {
                int componentIndex = this._byKey.GetComponentIndex(source.GetComponent());
                if (componentIndex >= 0)
                {
                    string combineKey = SingletonUtil.GetCombineKey(locale, source.GetComponent(), "handle", "bundle");
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
                return CheckWithKey(message, source);
            }

            string text = _useSourceLocale.GetMessage(source.GetComponent(), source.GetKey());
            if (text == null)
            {
                text = GetRemoteMessage(useLocale, source, null);
            }

            string sourceInCode = source.GetSource();
            if (sourceInCode.Equals(text) || text == null)
            {
                text = GetRemoteMessage(useLocale, source, sourceInCode);
            }
            return CheckWithKey(text, source);
        }
    }
}
