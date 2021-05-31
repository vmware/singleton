/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using System.Text.RegularExpressions;
using YamlDotNet.RepresentationModel;

namespace SingletonClient.Implementation
{
    public interface ISingletonConfig
    {
        IConfig GetConfig();

        string GetProduct();

        string GetVersion();

        string GetReleaseName();

        string GetServiceUrl();

        string GetExternalResourceRoot();

        string GetInternalResourceRoot();

        bool IsProductMode();

        string GetDefaultLocale();

        string GetSourceLocale();

        bool IsSourceLocaleDefault();

        IConfigItem GetOfflinePathItem(string component, string locale);

        string GetCacheType();

        string GetCacheComponentType();

        string GetAccessServiceType();

        string GetLoggerName();

        string GetLogLevel();

        string GetDefaultResourceFormat();

        int GetInterval();

        int GetTryDelay();

        bool IsOnlineSupported();

        bool IsOfflineSupported();

        bool IsLoadOnStartup();

        bool IsCacheByKey();

        List<string> GetExternalComponentList();

        List<string> GetExternalLocaleList(string component);
    }

    public class SingletonConfigItem : IConfigItem
    {
        private readonly YamlNode _node;

        public SingletonConfigItem(object node)
        {
            _node = (YamlNode)node;
        }

        private string GetNodeString(YamlNode node, string key)
        {
            YamlNode n = (node == null || string.IsNullOrEmpty(key)) ? node : node[key];
            return (n == null) ? null : n.ToString();
        }

        public string GetString()
        {
            return GetNodeString(_node, null);
        }

        public List<string> GetStringList()
        {
            if (_node == null)
            {
                return SingletonUtil.GetEmptyStringList();
            }
            List<string> itemList = new List<string>();

            YamlSequenceNode v = (YamlSequenceNode)_node;
            foreach (var entry in v.Children)
            {
                YamlScalarNode one = (YamlScalarNode)entry;
                itemList.Add(one.Value);
            }

            return itemList;
        }

        public bool GetBool()
        {
            string text = GetString();
            if (text == null)
            {
                return false;
            }
            return "true".Equals(text);
        }

        public int GetInt()
        {
            string text = GetString();
            return (text == null) ? 0 : Convert.ToInt32(text);
        }

        public IConfigItem GetMapItem(string key)
        {
            YamlMappingNode node = (YamlMappingNode)_node;
            if (node != null)
            {
                foreach (var item in node.Children)
                {
                    if (item.Key.ToString().Equals(key))
                    {
                        IConfigItem configItem = new SingletonConfigItem(item.Value);
                        return configItem;
                    }
                }

            }
            return null;
        }

        public List<string> GetArrayItemList(string key)
        {
            List<string> itemList = new List<string>();

            if (_node != null && !string.IsNullOrEmpty(key))
            {
                YamlSequenceNode v = (YamlSequenceNode)_node;

                foreach (var entry in v.Children)
                {
                    string value = GetNodeString(entry, key);
                    if (!string.IsNullOrEmpty(value) && !itemList.Contains(value))
                    {
                        itemList.Add(value);
                    }
                }
            }
            return itemList;
        }

        public IConfigItem GetArrayItem(string key, string value)
        {
            if (string.IsNullOrEmpty(key) || string.IsNullOrEmpty(value))
            {
                return null;
            }
            if (_node == null)
            {
                return null;
            }
            YamlSequenceNode v = (YamlSequenceNode)_node;

            foreach (var entry in v.Children)
            {
                string text = GetNodeString(entry, key);
                if (value.Equals(text))
                {
                    IConfigItem configItem = new SingletonConfigItem(entry);
                    return configItem;
                }
            }

            return null;
        }
    }

    public class SingletonConfig : IConfig
    {
        private IConfigItem _root;

        private readonly Assembly _resourceAssembly;

        private class SingletonConfigReach
        {
            public IConfigItem _componentItem;
            public IConfigItem _templateItem;
            public IConfigItem _localesItem;
        }

        public SingletonConfig(Assembly resourceAssembly)
        {
            _resourceAssembly = resourceAssembly;
        }

        private void ReadConfig(string configText)
        {
            _root = new SingletonConfigItem(SingletonUtil.GetYamlRoot(configText));
        }

        /// <summary>
        /// Set configuration data
        /// </summary>
        /// <param name="text"></param>
        public void SetConfigData(string text)
        {
            // To get rid of comments.
            var strs = Regex.Split(text, "(#.*)");

            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 0; i < strs.Length; i+=2)
            {
                stringBuilder.Append(strs[i]);
            }

            ReadConfig(stringBuilder.ToString());
        }

        public IConfigItem GetItem(string key)
        {
            return (_root == null) ? null : _root.GetMapItem(key);
        }

        public List<string> GetComponentList()
        {
            IConfigItem configItem = GetItem(ConfigConst.KeyComponents);
            if (configItem == null)
            {
                return SingletonUtil.GetEmptyStringList();
            }
            return configItem.GetArrayItemList(ConfigConst.KeyName);
        }

        private IConfigItem GetComponentArrayItem(string component)
        {
            IConfigItem componentsItem = GetItem(ConfigConst.KeyComponents);
            IConfigItem componentItem = (componentsItem == null) ? null : componentsItem.GetArrayItem(
                ConfigConst.KeyName, component);
            return componentItem;
        }

        public IConfigItem GetComponentAttribute(string component, string key)
        {
            IConfigItem componentItem = GetComponentArrayItem(component);
            return (componentItem == null) ? null : componentItem.GetMapItem(key);
        }

        private SingletonConfigReach GetLocalesItem(string component)
        {
            SingletonConfigReach reach = new SingletonConfigReach();

            reach._componentItem = GetComponentArrayItem(component);
            if (reach._componentItem == null)
            {
                return reach;
            }

            reach._localesItem = reach._componentItem.GetMapItem(ConfigConst.KeyLocales);
            if (reach._localesItem != null)
            {
                return reach;
            }

            IConfigItem templateItem = reach._componentItem.GetMapItem(ConfigConst.KeyTemplate);
            string templateName = (templateItem == null) ? ConfigConst.KeyComponentTemplate : templateItem.GetString();
            reach._templateItem = GetItem(templateName);
            if (reach._templateItem == null)
            {
                return reach;
            }

            reach._localesItem = reach._templateItem.GetMapItem(ConfigConst.KeyLocales);
            if (reach._localesItem != null)
            {
                return reach;
            }

            IConfigItem localesReferItem = reach._templateItem.GetMapItem(ConfigConst.KeyLocalesRefer);
            if (localesReferItem != null)
            {
                string localesReferName = localesReferItem.GetString();
                reach._localesItem = this.GetItem(localesReferName);
            }

            return reach;
        }

        public List<string> GetLocaleList(string component)
        {
            SingletonConfigReach reach = GetLocalesItem(component);
            if (reach._localesItem == null)
            {
                return new List<string>();
            }
            return reach._localesItem.GetArrayItemList(ConfigConst.KeyLanguage);
        }

        public IConfigItem GetLocaleAttribute(string component, string locale, string key)
        {
            SingletonConfigReach reach = GetLocalesItem(component);
            IConfigItem localeItem = (reach._localesItem == null) ? null :
                reach._localesItem.GetArrayItem(ConfigConst.KeyLanguage, locale);
            if (localeItem != null)
            {
                IConfigItem item = localeItem.GetMapItem(key);
                if (item == null && ConfigConst.KeyOfflinePath.Equals(key) && reach._templateItem != null)
                {
                    item = reach._templateItem.GetMapItem(ConfigConst.KeyOfflinePath);
                }
                return item;
            }
            return null;
        }

        /// <summary>
        /// Read resource as a text.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        public string ReadResourceText(string resourceBaseName, string resourceName)
        {
            Byte[] bytes = SingletonUtil.ReadResource(
                resourceBaseName, _resourceAssembly, resourceName);
            string text = SingletonUtil.ConvertToText(bytes);
            return text;
        }

        /// <summary>
        /// Read a map from resource by a parser.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <param name="parser"></param>
        /// <returns></returns>
        public Hashtable ReadResourceMap(string resourceName, string format, string locale)
        {
            if (string.IsNullOrEmpty(resourceName) || string.IsNullOrEmpty(format))
            {
                return SingletonUtil.NewHashtable(true);
            }
            if (locale == null)
            {
                locale = ConfigConst.DefaultLocale;
            }

            if (ConfigConst.FormatResx.Equals(format))
            {
                return SingletonUtil.ReadResourceMap(resourceName, locale, _resourceAssembly);
            }

            return SingletonUtil.NewHashtable(true);
        }
    }

    public class SingletonConfigWrapper: ISingletonConfig
    {
        private readonly IConfig _config;

        private readonly string _internalRoot;
        private readonly string _serviceUrl;
        private readonly string _defaultResourceFormat;

        private readonly string _product;
        private readonly string _version;

        private readonly string _bundleRoot;
        private readonly List<string> _externalComponentList = new List<string>();

        private readonly bool _isProductMode;
        private readonly bool _isOnlineSupported;
        private readonly bool _isOfflineSupported;
        private readonly bool _isSourceDefault;
        private readonly bool _isLoadOnStartup;
        private readonly bool _isCacheByKey;

        public SingletonConfigWrapper(IConfig config)
        {
            _config = config;

            // Get product name and l10n version
            _product = GetTextWithDefault(ConfigConst.KeyProduct, null);
            _version = GetTextWithDefault(ConfigConst.KeyVersion, null);

            // Check if it is in product mode
            IConfigItem configItem = _config.GetItem(ConfigConst.KeyProductMode);
            _isProductMode = (configItem == null) || configItem.GetBool();

            // Get online url
            _serviceUrl = GetTextWithDefault(ConfigConst.KeyOnlineUrl, null);
            _isOnlineSupported = !string.IsNullOrEmpty(GetServiceUrl());

            // Check offline config
            string offlineUrl = GetTextWithDefault(ConfigConst.KeyOfflineUrl, null);
            if (offlineUrl != null)
            {
                // Handle default resource format
                configItem = _config.GetItem(ConfigConst.KeyDefaultResourceFormat);
                string resourceFormat = (configItem != null && configItem.GetString().Length > 0) ?
                    configItem.GetString() + "," + ConfigConst.StoreTypeInternal : "resx, internal";
                string[] parts = Regex.Split(resourceFormat.Trim(), "[\\s]*\\,[\\s]*");
                bool isDefaultResourceInternal = parts[1].Equals(ConfigConst.StoreTypeInternal);
                _defaultResourceFormat = parts[0] + "," + parts[1];

                // Get internal resource root
                _internalRoot = isDefaultResourceInternal ?
                    offlineUrl : GetTextWithDefault(ConfigConst.KeyInternalResourceRoot, null);

                // Get external resource root
                string external = isDefaultResourceInternal ?
                    GetTextWithDefault(ConfigConst.KeyExternalResourceRoot, null) : offlineUrl;
                if (external != null)
                {
                    _bundleRoot = external.Replace("file:///", "").Replace("://", "\x01");
                    _bundleRoot = (_bundleRoot + "/").Replace("//", "/");

                    BuildExternalComponentList(external);
                    _bundleRoot = _bundleRoot.Replace("\x01", "://");
                }
            }
            _isOfflineSupported = !string.IsNullOrEmpty(offlineUrl);

            // Get default locale and source locale
            ISingletonLocale defaultLocale = SingletonUtil.GetSingletonLocale(GetDefaultLocale());
            ISingletonLocale sourceLocale = SingletonUtil.GetSingletonLocale(GetSourceLocale());
            _isSourceDefault = defaultLocale.Compare(sourceLocale);

            // Get if messages need to be prepared during startup
            configItem = _config.GetItem(ConfigConst.KeyLoadOnStartup);
            _isLoadOnStartup = (configItem != null) && configItem.GetBool();

            // Get if key is unique whatever component is
            _isCacheByKey = ConfigConst.CacheByKey.Equals(this.GetCacheType(),
                StringComparison.InvariantCultureIgnoreCase) || ConfigConst.DefaultType.Equals(this.GetCacheType(),
                StringComparison.InvariantCultureIgnoreCase);
        }

        private void BuildExternalComponentList(string external)
        {
            if (string.IsNullOrEmpty(_bundleRoot) || !external.StartsWith("file:"))
            {
                return;
            }

            DirectoryInfo dir = new DirectoryInfo(_bundleRoot);
            try
            {
                DirectoryInfo[] dirinfo = dir.GetDirectories();

                _externalComponentList.Clear();
                for (int i = 0; i < dirinfo.Length; i++)
                {
                    _externalComponentList.Add(dirinfo[i].Name);
                }
            }
            catch(Exception e)
            {
                SingletonUtil.HandleException(e);
            }
        }

        public IConfig GetConfig()
        {
            return _config;
        }

        public string GetProduct()
        {
            return _product;
        }

        public string GetVersion()
        {
            return _version;
        }

        public string GetReleaseName()
        {
            string product = GetProduct();
            string version = GetVersion();

            string key = product + "^" + version;
            return key;
        }

        public string GetServiceUrl()
        {
            return _serviceUrl;
        }

        private string GetTextWithDefault(string key, string defaultText)
        {
            IConfigItem configItem = (_config == null) ? null : _config.GetItem(key);
            string text = (configItem == null) ? null : configItem.GetString();
            if (string.IsNullOrEmpty(text))
            {
                text = defaultText;
            }
            return text;
        }

        public bool IsProductMode()
        {
            return _isProductMode;
        }

        public string GetDefaultLocale()
        {
            return GetTextWithDefault(ConfigConst.KeyDefaultLocale, ConfigConst.DefaultLocale);
        }

        public string GetSourceLocale()
        {
            return GetTextWithDefault(ConfigConst.KeySourceLocale, GetDefaultLocale());
        }

        public bool IsSourceLocaleDefault()
        {
            return _isSourceDefault;
        }

        private string GetLocaleInUse(string component, string locale)
        {
            List<string> localeNames = _config.GetLocaleList(component);
            if (localeNames == null || localeNames.Contains(locale))
            {
                return locale;
            }

            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            for (int i=0; i<localeNames.Count; i++)
            {
                ISingletonLocale oneLocale = SingletonUtil.GetSingletonLocale(localeNames[i]);
                if (oneLocale.Compare(singletonLocale))
                {
                    return localeNames[i];
                }
            }

            return locale;
        }

        public IConfigItem GetOfflinePathItem(string component, string locale)
        {
            string localeInUse = GetLocaleInUse(component, locale);
            IConfigItem configItem = _config.GetLocaleAttribute(
                component, localeInUse, ConfigConst.KeyOfflinePath);

            return configItem;
        }

        public string GetCacheType()
        {
            return GetTextWithDefault(ConfigConst.KeyCacheType, ConfigConst.DefaultType);
        }

        public string GetCacheComponentType()
        {
            return GetTextWithDefault(ConfigConst.KeyCacheComponentType, ConfigConst.DefaultType);
        }

        public string GetAccessServiceType()
        {
            return GetTextWithDefault(ConfigConst.KeyAccessServiceType, ConfigConst.DefaultType);
        }

        public string GetLoggerName()
        {
            return GetTextWithDefault(ConfigConst.KeyLoggerType, ConfigConst.DefaultType);
        }

        public string GetLogLevel()
        {
            return GetTextWithDefault(ConfigConst.KeyLogLevel, ConfigConst.DefaultDebugLevel);
        }

        public int GetInterval()
        {
            IConfigItem configItem = _config.GetItem(ConfigConst.KeyCacheExpire);
            return (configItem == null) ? ConfigConst.DefaultCacheExpire : configItem.GetInt();
        }

        public int GetTryDelay()
        {
            IConfigItem configItem = _config.GetItem(ConfigConst.KeyTryDelay);
            return (configItem == null) ? ConfigConst.DefaultTryDelay : configItem.GetInt();
        }

        public string GetExternalResourceRoot()
        {
            return _bundleRoot;
        }

        public string GetInternalResourceRoot()
        {
            return _internalRoot;
        }

        public string GetDefaultResourceFormat()
        {
            return _defaultResourceFormat;
        }

        public bool IsOnlineSupported()
        {
            return _isOnlineSupported;
        }

        public bool IsOfflineSupported()
        {
            return _isOfflineSupported;
        }

        public bool IsLoadOnStartup()
        {
            return _isLoadOnStartup;
        }

        public bool IsCacheByKey()
        {
            return _isCacheByKey;
        }

        public List<string> GetExternalComponentList()
        {
            return this._externalComponentList;
        }

        public List<string> GetExternalLocaleList(string component)
        {
            if (this._externalComponentList.Count == 0)
            {
                return SingletonUtil.GetEmptyStringList();
            }

            DirectoryInfo dir = new DirectoryInfo(_bundleRoot + component);
            FileInfo[] finfo = dir.GetFiles();

            List<string> localeList = new List<string>();
            for (int i = 0; i < finfo.Length; i++)
            {
                string name = finfo[i].Name;
                if (name.StartsWith("messages_"))
                {
                    name = name.Substring("messages_".Length);
                    string[] parts = name.Split('.');
                    localeList.Add(parts[0]);
                }
            }

            return localeList;
        }
    }
}

