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
        void LoadLocalMessage(ISingletonLocale singletonLocale, string component, bool asSource, bool isDirect);
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

        public void LoadLocalMessage(ISingletonLocale singletonLocale, string component, bool asSource, bool isDirect)
        {
            if (singletonLocale == null || !_config.IsOfflineSupported())
            {
                return;
            }

            if (!_config.IsOnlineSupported())
            {
                isDirect = true;
            }

            string locale = singletonLocale.GetOriginalLocale();

            List<string> componentLocalList = _config.GetLocalComponentList();

            ISingletonUseLocale useLocale = _release.GetUseLocale(locale, asSource);

            foreach (string one in componentLocalList)
            {
                if (!string.IsNullOrEmpty(component) && !SingletonUtil.Equals(one, component))
                {
                    continue;
                }

                string combineKey = SingletonUtil.GetCombineKey(locale, one, "direct", isDirect);
                if (_loadedLocalBundles.Contains(combineKey))
                {
                    continue;
                }
                _loadedLocalBundles.SetItem(combineKey, true);

                foreach (string nearLocale in singletonLocale.GetNearLocaleList())
                {
                    IConfigItem sourceConfigItem = _config.GetSourcePathItem(one, locale);
                    IConfigItem offlineConfigItem = _config.GetOfflinePathItem(one, locale);
                    if (sourceConfigItem == null)
                    {
                        sourceConfigItem = offlineConfigItem;
                        offlineConfigItem = null;
                    }

                    bool hasData = LoadLocalBundle(useLocale, sourceConfigItem, nearLocale, one);
                    if (offlineConfigItem != null)
                    {
                        if (isDirect)
                        {
                            LoadLocalBundle(_release.GetRemoteSourceUseLocale(), offlineConfigItem, nearLocale, one);
                        }
                        else
                        {
                            _release.GetRemoteSourceUseLocale().GetComponent(one, true);
                        }
                    }
                    if (hasData)
                    {
                        break;
                    }
                }
            }
        }

        private bool LoadLocalBundle(ISingletonUseLocale useLocale, IConfigItem resConfigItem, string nearLocale, string component)
        {
            string[] parts = new string[3];
            string[] arrayFormat = _config.GetDefaultResourceFormat().Split(',');

            int messageCount = 0;

            List<string> resList = (resConfigItem == null) ?
                new List<String>() { SingletonConst.PlaceNoLocaleDefine } : resConfigItem.GetStringList();

            ISingletonComponent singletonComponent = useLocale.GetComponent(component, false);
            if (singletonComponent == null)
            {
                return false;
            }

            for (int k = 0; k < resList.Count; k++)
            {
                string[] array = resList[k].Split(',');
                for (int m = 0; m < 3; m++)
                {
                    parts[m] = (m < array.Length) ? array[m].Trim() : arrayFormat[m - 1].Trim();
                }

                UpdateMessageFromLocal(singletonComponent,
                    parts[2], parts[0], nearLocale, parts[1]);
            }

            IComponentMessages componentCache = singletonComponent.GetComponentMessages();
            int keyCount = componentCache.GetCount();
            if (keyCount > 0)
            {
                messageCount += keyCount;
            }

            return (messageCount > 0);
        }

        private void UpdateMessageFromInternal(
            ISingletonComponent singletonComponent, string resourceName, string locale, string parserName)
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

            UpdateMessageFromMap(singletonComponent, bundle);
        }

        private void UpdateMessageFromExternal(
            ISingletonComponent singletonComponent, string resourcePath, string locale, string parserName)
        {
            IResourceParser parser = SingletonReleaseManager.GetInstance().GetResourceParser(parserName);
            if (parser == null)
            {
                return;
            }

            if (resourcePath.Contains(SingletonConst.PlaceNoLocaleDefine))
            {
                resourcePath = resourcePath.Replace(SingletonConst.PlaceNoLocaleDefine,
                    singletonComponent.GetComponent());

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
                UpdateMessageFromMap(singletonComponent, bundle);
            }

            if (ConfigConst.FormatBundle.Equals(parserName))
            {
                IComponentMessages componentCache = singletonComponent.GetComponentMessages();
                componentCache.SetResourcePath(path);
                componentCache.SetResourceType(parserName);
            }
        }

        private void UpdateMessageFromLocal(ISingletonComponent singletonComponent,
            string storeType, string resourcePath, string locale, string parserName)
        {
            if (resourcePath == null)
            {
                return;
            }

            resourcePath = resourcePath.Replace(ConfigConst.PlaceComponent, singletonComponent.GetComponent());
            resourcePath = resourcePath.Replace(ConfigConst.PlaceLocale, locale);

            string lc = _config.GetSourceLocale().Equals(locale) ? "" : "_" + locale;
            resourcePath = resourcePath.Replace(ConfigConst.PlaceLocaleCommon, lc);

            if (ConfigConst.StoreTypeInternal.Equals(storeType))
            {
                UpdateMessageFromInternal(singletonComponent, resourcePath, locale, parserName);
            }
            else if (ConfigConst.StoreTypeExternal.Equals(storeType))
            {
                UpdateMessageFromExternal(singletonComponent, resourcePath, locale, parserName);
            }
        }

        private void UpdateMessageFromMap(ISingletonComponent singletonComponent, Hashtable map)
        {
            if (map != null)
            {
                singletonComponent.SetIsLocal(true);
                // follow above
                foreach (string key in map.Keys)
                {
                    string value = map[key].ToString();
                    singletonComponent.SetMessage(key, value);
                }
            }
        }
    }
}
