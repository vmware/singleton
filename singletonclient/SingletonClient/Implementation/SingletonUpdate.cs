/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Data;
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
        void UpdateBriefInfo(string url, string infoName, List<string> infoList);

        /// <summary>
        /// Load offline bundle to its cache.
        /// </summary>
        /// <param name="singletonLocale"></param>
        /// <param name="component"></param>
        /// <param name="asSource"></param>
        /// <returns></returns>
        ILocaleMessages LoadLocalMessage(ISingletonLocale singletonLocale, string component, bool asSource = false);
    }

    public class SingletonUpdate : ISingletonUpdate
    {
        protected readonly ISingletonRelease _release;
        protected readonly ISingletonConfig _config;
        protected readonly int _tryWait;

        private readonly ISingletonTable<bool> _loadedLocalBundles = new SingletonTable<bool>();

        public SingletonUpdate(ISingletonRelease release)
        {
            _release = release;
            _config = release.GetSingletonConfig();
            _tryWait = _config.GetTryWait();
        }

        public void UpdateBriefInfo(string url, string infoName, List<string> infoList)
        {
            Hashtable headers = SingletonUtil.NewHashtable(false);
            JObject obj = _release.GetApi().HttpGetJson(url, headers,
                _tryWait, _release.GetLogger());

            if (SingletonUtil.CheckResponseValid(obj, headers) == ResponseStatus.Messages)
            {
                JObject result = obj.Value<JObject>(SingletonConst.KeyResult);
                JObject data = result.Value<JObject>(SingletonConst.KeyData);
                JArray ar = data.Value<JArray>(infoName);
                SingletonUtil.SetListFromJson(infoList, ar);
            }
        }

        public ILocaleMessages LoadLocalMessage(ISingletonLocale singletonLocale, string component, bool asSource = false)
        {
            string locale = singletonLocale.GetOriginalLocale();
            string keyLocaleScope = SingletonUtil.GetCombineKey(locale, null);
            string keyBundle = SingletonUtil.GetCombineKey(locale, component);
            if (_loadedLocalBundles.Contains(keyLocaleScope) || _loadedLocalBundles.Contains(keyBundle))
            {
                return null;
            }
            _loadedLocalBundles.SetItem(keyBundle, true);

            ILocaleMessages languageMessages = null;

            for (int i = 0; i < singletonLocale.GetCount(); i++)
            {
                string nearLocale = singletonLocale.GetNearLocale(i);
                languageMessages = LoadLocalLocaleMessage(locale, nearLocale, component, asSource);
                if (languageMessages != null)
                {
                    break;
                }
            }
            return languageMessages;
        }

        private void UpdateMessageFromInternal(
            IComponentMessages componentCache, string resourceName, string locale, string parserName)
        {
            if (resourceName.Contains(SingletonConst.PlaceNoLocaleDefine))
            {
                return;
            }

            Hashtable bundle;

            IResourceParser parser = SingletonReleaseManager.GetInstance().GetResourceParser(parserName);
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
            IResourceParser parser = SingletonReleaseManager.GetInstance().GetResourceParser(parserName);
            if (parser == null)
            {
                return;
            }

            if (resourcePath.Contains(SingletonConst.PlaceNoLocaleDefine))
            {
                resourcePath = resourcePath.Replace(SingletonConst.PlaceNoLocaleDefine,
                    componentCache.GetComponent());

                string lc = _config.GetSourceLocale().Equals(locale) ? "" : "_" + locale;

                if (ConfigConst.FormatBundle.Equals(parserName))
                {
                    resourcePath += "/messages" + lc + ".json";
                }
                else if (ConfigConst.FormatProperties.Equals(parserName))
                {
                    resourcePath += "/messages" + lc + ".properties";
                }
            }

            string path = _config.GetExternalResourceRoot() + resourcePath;
            string statusConnection;
            string text = path.StartsWith("http") ? _release.GetAccessService().HttpGet(path, null,
                _tryWait, out statusConnection, null) : SingletonUtil.ReadTextFile(path);

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

        private void UpdateMessageFromLocal(IComponentMessages componentCache,
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

        private void UpdateMessageFromMap(IComponentMessages componentCache, Hashtable map)
        {
            if (map != null)
            {
                foreach (string key in map.Keys)
                {
                    componentCache.SetString(key, map[key].ToString());
                }
            }
        }

        private ILocaleMessages LoadLocalLocaleMessage(string locale, string nearLocale, string component, bool asSource)
        {
            string[] parts = new string[3];
            string[] arrayFormat = _config.GetDefaultResourceFormat().Split(',');

            List<string> componentList = _config.GetLocalComponentList();

            int messageCount = 0;

            ISingletonUseLocale useLocale = _release.GetUseLocale(locale, asSource);

            for (int i = 0; componentList != null && i < componentList.Count; i++)
            {
                string one = componentList[i];
                if (!string.IsNullOrEmpty(component) && !one.Equals(component, StringComparison.OrdinalIgnoreCase))
                {
                    continue;
                }

                IConfigItem resConfigItem = _config.GetOfflinePathItem(one, locale);
                List<string> resList = (resConfigItem == null) ?
                    new List<String>() { SingletonConst.PlaceNoLocaleDefine } : resConfigItem.GetStringList();

                ISingletonComponent singletonComponent = useLocale.GetComponent(one, false);
                if (singletonComponent == null)
                {
                    continue;
                }
                IComponentMessages componentCache = singletonComponent.GetComponentMessages();
                for (int k = 0; k < resList.Count; k++)
                {
                    string[] array = resList[k].Split(',');
                    for (int m = 0; m < 3; m++)
                    {
                        parts[m] = (m < array.Length) ? array[m].Trim() : arrayFormat[m - 1].Trim();
                    }

                    UpdateMessageFromLocal(componentCache,
                        parts[2], parts[0], nearLocale, parts[1]);
                }

                int keyCount = componentCache.GetCount();
                if (keyCount > 0)
                {
                    messageCount += keyCount;
                }
            }

            return (messageCount == 0) ? null : useLocale.GetLocaleCache();
        }
    }
}
