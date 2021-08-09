/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Release;
using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Collections.Generic;
using static SingletonClient.Implementation.SingletonUtil;

namespace SingletonClient.Implementation
{
    public interface ISingletonUpdate
    {
        /// <summary>
        /// Update brief information about components or locales.
        /// </summary>
        /// <param name="url"></param>
        /// <param name="infoName"></param>
        /// <param name="infoList"></param>
        void UpdateBriefinfo(string url, string infoName, List<string> infoList);

        /// <summary>
        /// Update bundle from offline storage.
        /// </summary>
        /// <param name="componentCache"></param>
        /// <param name="storeType"></param>
        /// <param name="resourcePath"></param>
        /// <param name="locale"></param>
        /// <param name="parserName"></param>
        void UpdateMessageFromOffline(IComponentMessages componentCache,
            string storeType, string resourcePath, string locale, string parserName);

        /// <summary>
        /// Update bundle from key-value map.
        /// </summary>
        /// <param name="componentCache"></param>
        /// <param name="map"></param>
        void UpdateMessageFromMap(IComponentMessages componentCache, Hashtable map);

        /// <summary>
        /// Load offline bundle to its cache.
        /// </summary>
        /// <param name="singletonLocale"></param>
        /// <param name="asSource"></param>
        /// <returns></returns>
        ILocaleMessages LoadOfflineMessage(ISingletonLocale singletonLocale, bool asSource = false);
    }

    public class SingletonUpdate : ISingletonUpdate
    {
        protected ISingletonRelease _release;
        protected ISingletonConfig _config;
        protected int _tryDelay;

        private readonly List<string> _usedOfflineLocales = new List<string>();
        private List<string> _localComponentList = null;

        public SingletonUpdate(ISingletonRelease release)
        {
            _release = release;
            _config = release.GetSingletonConfig();
            _tryDelay = _config.GetTryDelay();
        }

        public void UpdateBriefinfo(string url, string infoName, List<string> infoList)
        {
            Hashtable headers = SingletonUtil.NewHashtable(false);
            JObject obj = _release.GetApi().HttpGetJson(url, headers,
                _tryDelay, _release.GetLogger());

            if (SingletonUtil.CheckResponseValid(obj, headers) == ResponseStatus.Messages)
            {
                JObject result = obj.Value<JObject>(SingletonConst.KeyResult);
                JObject data = result.Value<JObject>(SingletonConst.KeyData);
                JArray ar = data.Value<JArray>(infoName);
                SingletonUtil.SetListFromJson(infoList, ar);
            }
        }

        private void UpdateMessageFromInternal(
            IComponentMessages componentCache, string resourceName, string locale, string parserName)
        {
            if (resourceName.Contains(SingletonConst.PlaceNoLocaleDefine))
            {
                return;
            }

            Hashtable bundle;

            IResourceParser parser = SingletonClientManager.GetInstance().GetResourceParser(parserName);
            if (parser == null)
            {
                string resourceRoot = _config.GetInternalResourceRoot();
                if (!string.IsNullOrEmpty(resourceRoot))
                {
                    resourceName = resourceRoot + "." + resourceName;
                }
                bundle = _config.GetConfig().ReadResourceMap(resourceName, parserName, locale);
            }
            else
            {
                string resourceRoot = _config.GetInternalResourceRoot();
                string text = _config.GetConfig().ReadResourceText(resourceRoot, resourceName);
                bundle = parser.Parse(text);
            }

            UpdateMessageFromMap(componentCache, bundle);
        }

        private void UpdateMessageFromExternal(
            IComponentMessages componentCache, string resourcePath, string locale, string parserName)
        {
            IResourceParser parser = SingletonClientManager.GetInstance().GetResourceParser(parserName);
            if (parser == null)
            {
                return;
            }

            resourcePath = resourcePath.Replace(SingletonConst.PlaceNoLocaleDefine,
                componentCache.GetComponent());

            if (ConfigConst.FormatBundle.Equals(parserName))
            {
                resourcePath += "/messages_" + locale + ".json";
            }
            else if (ConfigConst.FormatProperties.Equals(parserName))
            {
                resourcePath += "/messages_" + locale + ".properties";
            }

            string path = _config.GetExternalResourceRoot() + resourcePath;
            string statusConnection;
            string text = path.StartsWith("http") ? _release.GetAccessService().HttpGet(path, null,
                _tryDelay, out statusConnection, null) : SingletonUtil.ReadTextFile(path);

            if (text != null)
            {
                Hashtable bundle = parser.Parse(text);
                UpdateMessageFromMap(componentCache, bundle);
            }

            if (ConfigConst.FormatBundle.Equals(parserName))
            {
                componentCache.SetResourcePath(path);
                componentCache.SetResourceType(parserName);
            }
        }

        public void UpdateMessageFromOffline(IComponentMessages componentCache,
            string storeType, string resourcePath, string locale, string parserName)
        {
            if (resourcePath == null)
            {
                return;
            }

            resourcePath = resourcePath.Replace(ConfigConst.PlaceComponent, componentCache.GetComponent());
            resourcePath = resourcePath.Replace(ConfigConst.PlaceLocale, locale);

            string lc = _config.GetSourceLocale().Equals(locale) ? "" : "_" + locale;
            resourcePath = resourcePath.Replace(ConfigConst.PlaceLocaleCommon, lc);

            if (ConfigConst.StoreTypeInternal.Equals(storeType))
            {
                UpdateMessageFromInternal(componentCache, resourcePath, locale, parserName);
            }
            else if (ConfigConst.StoreTypeExternal.Equals(storeType))
            {
                UpdateMessageFromExternal(componentCache, resourcePath, locale, parserName);
            }
        }

        public void UpdateMessageFromMap(IComponentMessages componentCache, Hashtable map)
        {
            if (map != null)
            {
                foreach (string key in map.Keys)
                {
                    componentCache.SetString(key, map[key].ToString());
                }
            }
        }

        public ILocaleMessages LoadOfflineMessage(ISingletonLocale singletonLocale, bool asSource = false)
        {
            string locale = singletonLocale.GetOriginalLocale();
            if (_usedOfflineLocales.Contains(locale))
            {
                return null;
            }
            _usedOfflineLocales.Add(locale);

            ILocaleMessages languageMessages = null;

            for (int i = 0; i < singletonLocale.GetCount(); i++)
            {
                string nearLocale = singletonLocale.GetNearLocale(i);
                languageMessages = LoadOfflineLocaleMessage(locale, nearLocale, asSource);
                if (languageMessages != null)
                {
                    break;
                }
            }
            return languageMessages;
        }

        private List<string> GetLocalComponentList()
        {
            if (_localComponentList != null)
            {
                return _localComponentList;
            }

            _localComponentList = _config.GetConfig().GetComponentList();
            bool fromExternal = (_localComponentList.Count == 0);
            if (fromExternal)
            {
                _localComponentList = _config.GetExternalComponentList();
            }

            List<string> releaseComponents = _release.GetRelease().GetMessages().GetComponentList();
            SingletonUtil.UpdateListFromAnotherList(releaseComponents, _localComponentList);

            List<string> releaseLocales = _release.GetRelease().GetMessages().GetLocaleList();
            for (int i = 0; i < _localComponentList.Count; i++)
            {
                List<string> componentLocaleList = fromExternal ? _config.GetExternalLocaleList(_localComponentList[i]) :
                    _config.GetConfig().GetLocaleList(_localComponentList[i]);
                SingletonUtil.UpdateListFromAnotherList(releaseLocales, componentLocaleList);
            }

            return _localComponentList;
        }

        private ILocaleMessages LoadOfflineLocaleMessage(string locale, string nearLocale, bool asSource)
        {
            string[] parts = new string[3];
            string[] arrayFormat = _config.GetDefaultResourceFormat().Split(',');

            List<string> componentList = GetLocalComponentList();

            int messageCount = 0;

            SingletonUseLocale useLocale = _release.GetUseLocale(locale, asSource);

            for (int i = 0; componentList != null && i < componentList.Count; i++)
            {
                string component = componentList[i];
                _release.AddLocalScope(locale, component);

                IConfigItem resConfigItem = _config.GetOfflinePathItem(component, locale);
                List<string> resList = (resConfigItem == null) ?
                    new List<String>() { SingletonConst.PlaceNoLocaleDefine } : resConfigItem.GetStringList();

                ISingletonComponent singletonComponent = useLocale.GetComponent(component);
                IComponentMessages componentCache = singletonComponent.GetComponentMessages();
                for (int k = 0; k < resList.Count; k++)
                {
                    string[] array = resList[k].Split(',');
                    for (int m = 0; m < 3; m++)
                    {
                        parts[m] = (m < array.Length) ? array[m].Trim() : arrayFormat[m - 1].Trim();
                    }

                    UpdateMessageFromOffline(componentCache,
                        parts[2], parts[0], nearLocale, parts[1]);
                }

                int keyCount = componentCache.GetCount();
                if (keyCount > 0)
                {
                    messageCount += keyCount;
                    _release.AddLocalScope(locale, component);
                }
            }

            return (messageCount == 0) ? null : useLocale.LocaleCache;
        }
    }
}
