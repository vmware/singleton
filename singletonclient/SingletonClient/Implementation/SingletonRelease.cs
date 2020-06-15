/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Helpers;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

namespace SingletonClient.Implementation
{
    public interface ISingletonRelease
    {
        IRelease GetRelease();
        void SetConfig(IConfig config);
        ISingletonConfig GetSingletonConfig();
        ISingletonApi GetApi();
        ICacheMessages GetReleaseMessages();
        IAccessService GetAccessService();
        void Log(LogType logType, string text);
    }

    public class SingletonRelease : ISingletonRelease, ISingletonAccessRemote, 
        IRelease, IReleaseMessages, ITranslation
    {
        private ISingletonConfig _config;
        private ISingletonApi _api;
        private ISingletonUpdate _update;
        private ICacheManager _cacheManager;
        private IAccessService _accessService;

        private ILog _logger;
        private LogType _logLevel;

        private ICacheMessages _productCache;
        private ILocaleMessages _sourceCache;

        private List<string> _localeList = new List<string>();
        private List<string> _componentList = new List<string>();

        private ISingletonAccessTask _task;

        private bool _isLoadedOnStartup = false;

        // key: (string) locale
        // value: (Hashtable) components -> 
        //     key: (string) component
        //     value: (ISingletonComponent) component object
        private Hashtable _localesTable = SingletonUtil.NewHashtable();

        private bool InitRelease()
        {
            if (_productCache != null)
            {
                return true;
            }
            _productCache = _cacheManager.GetReleaseCache(
                _config.GetProduct(), _config.GetVersion());

            if (_config.IsOfflineSupported())
            {
                _sourceCache = _update.LoadOfflineBundle(_config.GetSourceLocale(), true);
            }

            return true;
        }

        private Hashtable GetLocaleComponents(string locale, bool add)
        {
            Hashtable components = (Hashtable)_localesTable[locale];
            if (components == null && add)
            {
                components = SingletonUtil.NewHashtable();
                _localesTable[locale] = components;
            }
            return components;
        }

        private ISingletonComponent GetComponent(
            Hashtable components, string locale, string component, bool add)
        {
            ISingletonComponent obj = (ISingletonComponent)components[component];
            if (obj == null && add)
            {
                obj = new SingletonComponent(this, locale, component);
                components[component] = obj;

                if (_config.IsOfflineSupported())
                {
                    _update.LoadOfflineBundle(locale, true);
                }
            }
            return obj;
        }

        private bool CheckBundleRequest(string locale, string component)
        {
            if (string.IsNullOrEmpty(locale) || string.IsNullOrEmpty(component))
            {
                return false;
            }
            if (_config.IsOnlineSupported() && !_config.IsOfflineSupported())
            {
                if (!_localeList.Contains(locale) || !_componentList.Contains(component))
                {
                    return false;
                }
            }
            return true;
        }

        private ISingletonComponent GetComponent(string locale, string component)
        {
            if (!CheckBundleRequest(locale, component))
            {
                return null;
            }

            Hashtable htComponents = GetLocaleComponents(locale, true);
            ISingletonComponent componentObj = GetComponent(
                htComponents, locale, component, true);
            componentObj.GetAccessTask().CheckTimeSpan();
            return componentObj;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public IRelease GetRelease()
        {
            return this;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public void SetConfig(IConfig config)
        {
            _config = new SingletonConfigWrapper(config);
            _api = new SingletonApi(this);

            string cacheType = _config.GetCacheType();
            ISingletonClientManager client = SingletonClientManager.GetInstance();
            _cacheManager = client.GetCacheManager(cacheType);

            string accessServiceType = _config.GetAccessServiceType();
            _accessService = client.GetAccessService(accessServiceType);

            string loggerName = _config.GetLoggerName();
            _logger = client.GetLogger(loggerName);

            string logType = _config.GetLogLevel();
            _logLevel = (LogType)Enum.Parse(typeof(LogType), logType);

            _update = new SingletonUpdate(this);

            int interval = _config.GetInterval();
            int tryDelay = _config.GetTryDelay();
            _task = new SingletonAccessRemoteTask(this, interval, tryDelay);

            InitRelease();

            _task.CheckTimeSpan();
        }

        public ISingletonConfig GetSingletonConfig()
        {
            return _config;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonApi GetApi()
        {
            return _api;
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
        /// <returns></returns>
        public IAccessService GetAccessService()
        {
            return _accessService;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public void Log(LogType logType, string text)
        {
            if (_logger != null && logType >= this._logLevel)
            {
                _logger.Log(logType, text);
            }
        }

        private void CheckLoadOnStartup()
        {
            List<string> componentLocalList = _config.GetConfig().GetComponentList();

            for (int i = 0; i < _localeList.Count; i++)
            {
                for (int k = 0; k < _componentList.Count; k++)
                {
                    string component = _componentList[k];
                    if (componentLocalList == null || componentLocalList.Contains(component))
                    {
                        ISource sourceObject = this.CreateSource(component, "$", "$");
                        this.GetStringFromMessages(_localeList[i], sourceObject);
                    }
                }
            }
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public void GetDataFromRemote()
        {
            string url = _api.GetLocaleListApi();
            _update.UpdateBriefinfo(url, SingletonConst.KeyLocales, _localeList);

            url = _api.GetComponentListApi();
            _update.UpdateBriefinfo(url, ConfigConst.KeyComponents, _componentList);

            Log(LogType.Info, "get locale list and component list from remote: " +
                _config.GetProduct() + " / " + _config.GetVersion());

            if (!_isLoadedOnStartup && _config.IsLoadOnStartup())
            {
                _isLoadedOnStartup = true;

                Thread th = new Thread(this.CheckLoadOnStartup);
                th.Start();
            }
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public int GetDataCount()
        {
            return _localeList.Count;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public IConfig GetConfig()
        {
            return (_config == null) ? null : _config.GetConfig();
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public IReleaseMessages GetMessages()
        {
            return this;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public ITranslation GetTranslation()
        {
            return this;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public ISource CreateSource(
            string component, string key, string source = null, string comment = null)
        {
            if (component == null || key == null)
            {
                return null;
            }

            if (source == null)
            {
                source = (_sourceCache == null) ? null : _sourceCache.GetString(component, key);
            }
            ISource src = new SingletonSource(component, key, source, comment);
            return src;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public List<string> GetLocaleList()
        {
            return _localeList;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public List<string> GetComponentList()
        {
            return _componentList;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetAllSource()
        {
            return _sourceCache;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetLocaleMessages(string locale)
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(locale);
            return languageMessages;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public Dictionary<string, ILocaleMessages> GetAllLocaleMessages()
        {
            Dictionary<string, ILocaleMessages> langDataDict =
                new Dictionary<string, ILocaleMessages>();
            List<string> localeList = GetLocaleList();
            for (int i = 0; i < localeList.Count; i++)
            {
                string locale = localeList[i];
                ILocaleMessages languageData = GetReleaseMessages().GetLocaleMessages(locale);
                if (languageData != null)
                {
                    langDataDict[locale] = languageData;
                }
            }
            return langDataDict;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string Format(string locale, ISource source, params object[] objects)
        {
            if (source == null)
            {
                return null;
            }
            string text = this.GetString(locale, source);
            if (objects.Length > 0)
            {
                text = string.Format(text, objects);
            }
            return text;
        }

        private string GetBundleMessage(string locale, ISource source)
        {
            String nearLocale = SingletonUtil.NearLocale(locale);

            ISingletonComponent componentData = GetComponent(nearLocale, source.GetComponent());
            return (componentData != null) ? componentData.GetString(source.GetKey()) : null;
        }

        private string GetTranslationMessage(string locale, ISource source, string textSource)
        {
            string text = GetBundleMessage(locale, source);
            if (text == null)
            {
                text = _config.IsSourceLocaleDefault() ? 
                    textSource : GetBundleMessage(_config.GetDefaultLocale(), source);

                if (text == null)
                {
                    text = source.GetSource();
                }
                if (text == null)
                {
                    text = source.GetKey();
                }

                if (!_config.IsProductMode())
                {
                    text = "@" + text;
                }
            }
            return text;
        }

        private string GetStringFromMessages(string locale, ISource sourceObject)
        {
            if (sourceObject == null)
            {
                return null;
            }

            string textSource = GetBundleMessage(_config.GetSourceLocale(), sourceObject);
            if (textSource == null)
            {
                return GetTranslationMessage(locale, sourceObject, null);
            }

            string text = sourceObject.GetSource();
            if (textSource.Equals(text) || text == null)
            {
                return GetTranslationMessage(locale, sourceObject, textSource);
            }
            return text;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(string locale, ISource sourceObject)
        {
            _task.CheckTimeSpan();

            return GetStringFromMessages(locale, sourceObject);
        }

        public string GetString(
            string component, string key, string source = null, string comment = null)
        {
            ISource sourceObject = CreateSource(component, key, source, comment);
            string locale = GetCurrentLocale();
            return GetString(locale, sourceObject);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public bool SetCurrentLocale(string locale)
        {
            CultureHelper.SetCurrentCulture(locale);
            return true;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetCurrentLocale()
        {
            return CultureHelper.GetCurrentCulture();
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetLocaleSupported(string locale)
        {
            return SingletonUtil.NearLocale(locale);
        }

    }
}

