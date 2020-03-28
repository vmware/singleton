/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Helpers;
using System;
using System.Collections;
using System.Collections.Generic;

namespace SingletonClient.Implementation
{
    public interface ISingletonRelease
    {
        IRelease GetRelease();
        void SetConfig(IConfig config);
        ISingletonApi GetApi();
        ICacheMessages GetProductMessages();
        IAccessService GetAccessService();
        void Log(LogType logType, string text);
    }

    public class SingletonRelease : ISingletonRelease, ISingletonAccessRemote, 
        IRelease, IProductMessages, ITranslation
    {
        private IConfig _config;
        private ISingletonApi _api;
        private ICacheManager _cacheManager;
        private IAccessService _accessService;

        private ILog _logger;
        private LogType _logLevel;

        private ICacheMessages _productCache;
        private ILanguageMessages _sourceCache;

        private List<string> _localeList = new List<string>();
        private List<string> _componentList = new List<string>();

        private SingletonAccessRemoteTask _task;

        // key: (string) locale
        // value: (Hashtable) components -> 
        //     key: (string) component
        //     value: (ISingletonComponent) component object
        private Hashtable _localesTable = SingletonUtil.NewHashtable();

        private void UpdateList(List<string> strList, JArray ja)
        {
            strList.Clear();
            foreach (var one in ja)
            {
                strList.Add(one.ToString());
            }
        }

        private void UpdateBriefinfo(
            string url, string infoName, List<string> infoList)
        {
            Hashtable headers = SingletonUtil.NewHashtable();
            JObject obj = SingletonUtil.HttpGetJson(_accessService, url, headers);

            if (SingletonUtil.CheckResponseValid(obj, headers))
            {
                JObject result = obj.Value<JObject>(SingletonConst.KeyResult);
                JObject data = result.Value<JObject>(SingletonConst.KeyData);
                JArray ar = data.Value<JArray>(infoName);
                UpdateList(infoList, ar);
            }
        }

        private bool InitRelease()
        {
            if (_productCache != null)
            {
                return true;
            }
            _productCache = _cacheManager.GetProductCache(
                _config.GetProduct(), _config.GetVersion());

            _sourceCache = _productCache.GetLanguageMessages(SingletonConst.LocaleSource);

            List<string> componentList = _config.GetComponentList();
            for (int i = 0; i < componentList.Count; i++)
            {
                List<string> resList = _config.GetComponentSourceList(componentList[i]);
                IComponentMessages componentCache = _sourceCache.GetComponentMessages(componentList[i]);
                for (int k = 0; k < resList.Count; k++)
                {
                    string[] array = resList[k].Replace(" ", "").Split(',');
                    string parserName = array.Length > 1 ? array[1] : ConfigConst.TypeDefault;
                    ISourceParser parser = SingletonClientManager.GetInstance().GetSourceParser(parserName);
                    if (parser != null)
                    {
                        Hashtable bundle = _config.ReadResourceMap(array[0], parser);
                        foreach (string key in bundle.Keys)
                        {
                            componentCache.SetString(key, bundle[key].ToString());
                        }
                    }
                }
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
            }
            return obj;
        }

        private ISingletonComponent GetComponent(string locale, string component)
        {
            if (!_localeList.Contains(locale) || !_componentList.Contains(component))
            {
                return null;
            }

            Hashtable htComponents = GetLocaleComponents(locale, true);
            ISingletonComponent componentObj = GetComponent(
                htComponents, locale, component, true);
            componentObj.CheckStatus();
            return componentObj;
        }

        private bool SendSource(ISource src)
        {
            string collect = _config.GetStringValue(ConfigConst.KeyCollect);
            if (collect == null)
            {
                return false;
            }

            string url = _api.GetSendSourceApi(src.GetComponent(), src.GetKey());
            JObject obj = SingletonUtil.HttpPost(_accessService, url, src.GetSource(), null);
            if (SingletonUtil.CheckResponseValid(obj, null))
            {
                return true;
            }
            return false;
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
            _config = config;
            _api = new SingletonApi(this);

            string cacheType = config.GetStringValue(ConfigConst.KeyCacheType);
            SingletonClientManager client = SingletonClientManager.GetInstance();
            _cacheManager = client.GetCacheManager(cacheType);

            string accessServiceType = config.GetStringValue(ConfigConst.KeyAccessServiceType);
            _accessService = client.GetAccessService(accessServiceType);

            string loggerName = config.GetStringValue(ConfigConst.KeyLogger);
            _logger = client.GetLogger(loggerName);

            string logType = config.GetStringValue(ConfigConst.KeyLogType);
            _logLevel = (LogType)Enum.Parse(typeof(LogType), logType);

            int interval = config.GetIntValue(ConfigConst.KeyInterval);
            int tryDelay = config.GetIntValue(ConfigConst.KeyTryDelay);
            _task = new SingletonAccessRemoteTask(this, interval, tryDelay);

            _task.CheckStatus();

            InitRelease();
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
        public ICacheMessages GetProductMessages()
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

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public void GetDataFromRemote()
        {
            string url = _api.GetLocaleListApi();
            UpdateBriefinfo(url, SingletonConst.KeyLocales, _localeList);

            url = _api.GetComponentListApi();
            UpdateBriefinfo(url, ConfigConst.KeyComponents, _componentList);

            Log(LogType.Info, "get locale list and component list from remote: " +
                _api.GetConfig().GetProduct() + " / " + _api.GetConfig().GetVersion());
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
            return _config;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public IProductMessages GetMessages()
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
        /// IRelease
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
                source = _sourceCache.GetString(component, key);

            }
            ISource src = new SingletonSource(component, key, source, comment);
            return src;
        }

        /// <summary>
        /// IProductMessages
        /// </summary>
        public List<string> GetLocaleList()
        {
            return _localeList;
        }

        /// <summary>
        /// IProductMessages
        /// </summary>
        public List<string> GetComponentList()
        {
            return _componentList;
        }

        /// <summary>
        /// IProductMessages
        /// </summary>
        public ILanguageMessages GetAllSource()
        {
            return _sourceCache;
        }

        /// <summary>
        /// IProductMessages
        /// </summary>
        public ILanguageMessages GetTranslation(string locale)
        {
            return GetProductMessages().GetLanguageMessages(locale);
        }

        /// <summary>
        /// IProductMessages
        /// </summary>
        public Dictionary<string, ILanguageMessages> GetAllTranslation()
        {
            Dictionary<string, ILanguageMessages> langDataDict =
                new Dictionary<string, ILanguageMessages>();
            List<string> localeList = GetLocaleList();
            for (int i = 0; i < localeList.Count; i++)
            {
                string locale = localeList[i];
                ILanguageMessages languageData = GetProductMessages().GetLanguageMessages(locale);
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

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(string locale, ISource source)
        {
            locale = SingletonUtil.NearLocale(locale);

            _task.CheckStatus();
            if (source == null)
            {
                return null;
            }

            ISingletonComponent componentSource = GetComponent(SingletonConst.LocaleEnglish, source.GetComponent());
            ISingletonComponent componentTranslation = GetComponent(locale, source.GetComponent());

            string translation = source.GetSource();
            if (_config.GetBoolValue(ConfigConst.KeyPseudo))
            {
                translation = "@@" + source.GetSource() + "@@";
            }
            if (componentSource != null && componentTranslation != null)
            {
                string strSource = componentSource.GetString(source.GetKey());
                string strFound = componentTranslation.GetString(source.GetKey());
                if (strSource == source.GetSource() && strFound != null)
                {
                    translation = strFound;
                }
            }

            SendSource(source);
            return translation;
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
        public bool SendSource(List<ISource> sourceList)
        {
            bool status = true;
            for (int i = 0; i < sourceList.Count; i++)
            {
                if (!SendSource(sourceList[i]))
                {
                    status = false;
                }
            }
            return status;
        }
    }
}

