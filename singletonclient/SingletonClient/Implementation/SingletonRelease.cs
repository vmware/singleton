/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Helpers;
using SingletonClient.Implementation.Support;
using SingletonClient.Implementation.Support.ByKey;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Threading;

namespace SingletonClient.Implementation
{
    public interface ISingletonRelease
    {
        IRelease GetRelease();
        void SetConfig(IConfig config);
        ISingletonConfig GetSingletonConfig();
        ISingletonApi GetApi();
        ISingletonUpdate GetUpdate();
        ICacheMessages GetReleaseMessages();
        ISingletonByKeyRelease GetSingletonByKeyRelease();
        IAccessService GetAccessService();
        void Log(LogType logType, string text);
    }

    public class SingletonUseLocale
    {
        protected string _locale;
        protected bool _isSourceLocale;
        protected ISingletonLocale _singletonLocale;

        private Hashtable _components;
        private ILocaleMessages _localeMessages;

        public SingletonUseLocale(ISingletonLocale singletonLocale, string sourceLocale)
        {
            _singletonLocale = singletonLocale;
            _locale = singletonLocale.GetOriginalLocale();

            ISingletonLocale singletonSourceLocale = SingletonUtil.GetSingletonLocale(sourceLocale);
            _isSourceLocale = singletonSourceLocale.GetNearLocaleList().Contains(_locale);

            _components = SingletonUtil.NewHashtable(true);
        }

        public string Locale
        {
            get { return _locale; }
        }

        public bool IsSourceLocale
        {
            get { return _isSourceLocale; }
        }

        public ISingletonLocale SingletonLocale
        {
            get { return _singletonLocale; }
        }

        public Hashtable Components
        {
            get { return _components; }
        }

        public ILocaleMessages LocaleMessages
        {
            get { return _localeMessages; }
            set { _localeMessages = value; }
        }
    }

    public class SingletonAccessObject
    {
        protected SingletonUseLocale _useLocale;

        protected ISource _source;
        protected short _componentIndex;

        public SingletonAccessObject(SingletonUseLocale useLocale, ISource source)
        {
            _useLocale = useLocale;
            _source = source;
        }

        public string Locale
        {
            get { return _useLocale.Locale; }
        }

        public string Key
        {
            get { return _source.GetKey(); }
        }

        public string Component
        {
            get { return _source.GetComponent(); }
        }

        public ISingletonLocale SingletonLocale
        {
            get { return _useLocale.SingletonLocale; }
        }

        public SingletonUseLocale UseLocale
        {
            get { return _useLocale; }
        }

        public string SourceMessage
        {
            get { return _source.GetSource(); }
        }

        public int ComponentIndex
        {
            get { return _componentIndex; }
        }

        public void PrepareByKey(ISingletonByKeyRelease byKeyRelease)
        {
            _componentIndex = (short)byKeyRelease.GetComponentIndex(_source.GetComponent());
        }

        public bool IsJustSource()
        {
            return (_useLocale.IsSourceLocale && _source.GetSource() != null);
        }
    }

    public class SingletonReleaseBase
    {
        protected ISingletonClientManager _client;
        protected ISingletonRelease _self;
        protected ISingletonConfig _config;
        protected IReleaseMessages _releaseMessages;

        protected ILog _logger;
        protected LogType _logLevel;

        protected ISingletonApi _api;
        protected IAccessService _accessService;
        protected ISingletonAccessTask _task;

        protected readonly List<string> _localeList = new List<string>();
        protected readonly List<string> _componentList = new List<string>();

        protected void InitForBase(ISingletonRelease singletonRelease, IConfig config, ISingletonAccessRemote accessRemote)
        {
            _self = singletonRelease;
            _releaseMessages = _self.GetRelease().GetMessages();

            _config = new SingletonConfigWrapper(config);
            _client = SingletonClientManager.GetInstance();

            string loggerName = _config.GetLoggerName();
            _logger = _client.GetLogger(loggerName);

            string logType = _config.GetLogLevel();
            _logLevel = (LogType)Enum.Parse(typeof(LogType), logType);

            _api = new SingletonApi(_self);

            string accessServiceType = _config.GetAccessServiceType();
            _accessService = _client.GetAccessService(accessServiceType);

            int interval = _config.GetInterval();
            int tryDelay = _config.GetTryDelay();
            _task = new SingletonAccessRemoteTask(accessRemote, interval, tryDelay);
        }

        protected bool CheckBundleRequest(ISingletonLocale singletonLocale, string component)
        {
            if (string.IsNullOrEmpty(component))
            {
                return false;
            }

            if (_config.IsOnlineSupported() && !_config.IsOfflineSupported())
            {
                bool inLocaleScope = singletonLocale.IsInLocaleList(_localeList);
                bool inComponentScope = _componentList.Contains(component);
                if (!inLocaleScope || !inComponentScope)
                {
                    return false;
                }
            }
            return true;
        }
    }

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

        private ICacheManager _cacheManager;

        protected bool InitForCache()
        {
            if (_productCache != null)
            {
                return true;
            }

            _update = new SingletonUpdate(_self);

            string cacheType = _config.GetCacheType();
            _cacheManager = _client.GetCacheManager(cacheType);

            _productCache = _cacheManager.GetReleaseCache(
                _config.GetProduct(), _config.GetVersion());

            if (_config.IsOnlyByKey())
            {
                _byKeyRelease = new SingletonByKeyRelease(_self);
            }

            _useSourceLocale = GetUseLocale(_config.GetSourceLocale());
            if (_config.IsOfflineSupported())
            {
                _sourceCache = _update.LoadOfflineBundle(_useSourceLocale.SingletonLocale, true);
            }

            if (!_config.IsSourceLocaleDefault())
            {
                _useDefaultLocale = GetUseLocale(_config.GetDefaultLocale());
            }

            return true;
        }

        private SingletonUseLocale GetUseLocale(string locale)
        {
            SingletonUseLocale useLocale = (SingletonUseLocale)_localesTable[locale];
            if (useLocale == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                for (int i = 1; i < singletonLocale.GetCount(); i++)
                {
                    string tempLocale = singletonLocale.GetNearLocale(i);
                    useLocale = (SingletonUseLocale)_localesTable[tempLocale];
                    if (useLocale != null)
                    {
                        break;
                    }
                }

                if (useLocale == null)
                {
                    useLocale = new SingletonUseLocale(singletonLocale, _config.GetSourceLocale());
                }
                _localesTable[locale] = useLocale;
            }
            return useLocale;
        }

        private ISingletonComponent GetOrAddComponent(
            SingletonUseLocale useLocale, string component)
        {
            ISingletonComponent obj = (ISingletonComponent)useLocale.Components[component];
            if (obj == null)
            {
                obj = new SingletonComponent(_self, useLocale.SingletonLocale, component);
                useLocale.Components[component] = obj;

                if (!_config.IsOnlineSupported())
                {
                    obj.GetDataFromLocal();
                }
            }
            return obj;
        }

        private ISingletonComponent GetComponent(SingletonUseLocale useLocale, string component)
        {
            if (!CheckBundleRequest(useLocale.SingletonLocale, component))
            {
                return null;
            }

            ISingletonComponent componentObj = GetOrAddComponent(useLocale, component);
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
                useLocale.Components[component] = GetComponent(useLocale, component);
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
                    } else
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

            SingletonUseLocale useLocale = this.GetUseLocale(locale);
            SingletonAccessObject accessObject = new SingletonAccessObject(useLocale, source);
            if (accessObject.IsJustSource())
            {
                return accessObject.SourceMessage;
            }

            if (_byKeyRelease != null)
            {
                accessObject.PrepareByKey(_byKeyRelease);
                return GetTranslationMessage(accessObject, null);
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

    public class SingletonRelease : SingletonReleaseForCache, ISingletonRelease, ISingletonAccessRemote,
        IRelease, IReleaseMessages, ITranslation
    {
        private bool _isLoadedOnStartup = false;

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
            InitForBase(this, config, this);

            if (!InitForCache())
            {
                return;
            }

            if (this._config.IsOnlineSupported())
            {
                GetDataFromRemote();
            }
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
        public ISingletonByKeyRelease GetSingletonByKeyRelease()
        {
            return _byKeyRelease;
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
                    if (componentLocalList.Count == 0 || componentLocalList.Contains(component))
                    {
                        ISource sourceObject = this.CreateSource(component, "$", "$");
                        this.GetStringFromCache(_localeList[i], sourceObject);
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
        public ILocaleMessages GetLocaleMessages(string locale, bool asSource = false)
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(locale, asSource);
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
        public ISource CreateSource(
            string component, string key, string source = null, string comment = null)
        {
            if ((component == null && _byKeyRelease == null) || key == null)
            {
                return null;
            }

            if (source == null && _byKeyRelease == null)
            {
                source = GetSourceMessage(component, key);
            }
            ISource src = new SingletonSource(component, key, source, comment);
            return src;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(string locale, ISource source)
        {
            _task.CheckTimeSpan();

            return GetStringFromCache(locale, source);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
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
        public string Format(string locale, ISource source, params object[] objects)
        {
            if (source == null)
            {
                return null;
            }
            string text = this.GetString(locale, source);
            if (text != null && objects != null && objects.Length > 0)
            {
                try
                {
                    text = string.Format(text, objects);
                }
                catch (FormatException)
                {
                    string[] strs = Regex.Split(text, "{([0-9]+)}");
                    int maxPlaceHolderIndex = -1;
                    for (int i = 1; i < strs.Length; i += 2)
                    {
                        int temp = Convert.ToInt32(strs[i]);
                        if (temp > maxPlaceHolderIndex)
                        {
                            maxPlaceHolderIndex = temp;
                        }
                    }
                    if (maxPlaceHolderIndex >= 0)
                    {
                        object[] objectsExt = new object[maxPlaceHolderIndex + 1];

                        for (int i = 0; i < maxPlaceHolderIndex + 1; i++)
                        {
                            if (i < objects.Length)
                            {
                                objectsExt[i] = objects[i];
                            }
                            else
                            {
                                objectsExt[i] = "{" + i + "}";
                            }
                        }
                        text = string.Format(text, objectsExt);
                    }
                }
            }
            return text;
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
        public List<string> GetLocaleSupported(string locale)
        {
            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            if (singletonLocale == null)
            {
                return SingletonUtil.GetEmptyStringList();
            }
            return singletonLocale.GetNearLocaleList();
        }
    }
}

