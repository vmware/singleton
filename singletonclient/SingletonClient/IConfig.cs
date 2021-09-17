/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;

namespace SingletonClient
{
    /// <summary>
    /// Interface to get config value
    /// </summary>
    public interface IConfigItem
    {
        /// <summary>
        /// Clone a new object.
        /// </summary>
        IConfigItem Clone();

        /// <summary>
        /// Get string value of a config item.
        /// </summary>
        string GetString();

        /// <summary>
        /// Get string list of a config item.
        /// </summary>
        /// <returns></returns>
        List<string> GetStringList();

        /// <summary>
        /// Get bool value of a config item.
        /// </summary>
        bool GetBool();

        /// <summary>
        /// Get integer value of a config item.
        /// </summary>
        int GetInt();

        /// <summary>
        /// Get key list of the map.
        /// </summary>
        List<string> GetMapKeyList();

        /// <summary>
        /// Get an item from the map.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        IConfigItem GetMapItem(string key);

        /// <summary>
        /// Set item to the map.
        /// </summary>
        /// <param name="key"></param>
        /// <param name="item"></param>
        /// <returns></returns>
        void SetMapItem(string key, IConfigItem item);

        /// <summary>
        /// Get item list.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        List<string> GetArrayItemList(string key);

        /// <summary>
        /// Get an item in its array.
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        /// <returns></returns>
        IConfigItem GetArrayItem(string key, string value);
    }

    /// <summary>
    /// Interface to access configuration.
    /// </summary>
    public interface IConfig
    {
        /// <summary>
        /// Get root config item
        /// </summary>
        IConfigItem GetRoot();

        /// <summary>
        /// Get config item
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        IConfigItem GetItem(string key);

        /// <summary>
        /// Get component list
        /// </summary>
        /// <returns></returns>
        List<string> GetComponentList();

        /// <summary>
        /// Get attribute of a component
        /// </summary>
        /// <param name="component"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        IConfigItem GetComponentAttribute(string component, string key);

        /// <summary>
        /// Get locale list of a component
        /// </summary>
        /// <returns></returns>
        List<string> GetLocaleList(string component);

        /// <summary>
        /// Get attribute of a locale
        /// </summary>
        /// <param name="component"></param>
        /// <param name="locale"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        IConfigItem GetLocaleAttribute(string component, string locale, string key);

        /// <summary>
        /// Read text of a resource by the resource name.
        /// </summary>
        /// <param name="resourceBaseName"></param>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        string ReadResourceText(string resourceBaseName, string resourceName, string locale = null);

        /// <summary>
        /// Read message map by the resource name and locale when it's an internal format.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <param name="format"></param>
        /// <param name="locale"></param>
        /// <returns></returns>
        Hashtable ReadResourceMap(string resourceName, string format, string locale = null);

        /// <summary>
        /// Get resource list.
        /// </summary>
        List<string> GetResourceList(string resourceBaseName);
    }

    public static class ConfigConst
    {
        // Standard keys
        public const string KeyProduct = "product";
        public const string KeyOnlineUrl = "online_service_url";
        public const string KeyOfflineUrl = "offline_resources_base_url";
        public const string KeyVersion = "l10n_version";

        public const string KeyProductMode = "prod_mode";
        public const string KeyPseudo = "pseudo";
        public const string KeyDefaultLocale = "default_locale";

        public const string KeyComponents = "components";
        public const string KeyTemplate = "template";
        public const string KeyComponentTemplate = "component_template";
        public const string KeyComponentEnumerate = "component_enumerate";
        public const string KeyName = "name";
        public const string KeyLocales = "locales";
        public const string KeyLocalesRefer = "locales_refer";
        public const string KeyLanguage = "language_tag";
        public const string KeyOfflinePath = "offline_resources_path";  // path[, format, storetype]
        public const string KeySourcePath = "source_resources_path";  // path[, format, storetype]

        // Resource information used in KeyDefaultResourceFormat, KeyOfflinePath
        public const string FormatProperties = "properties";
        public const string FormatBundle = "bundle";
        public const string FormatResx = "resx";
        public const string StoreTypeInternal = "internal";
        public const string StoreTypeExternal = "external";

        // Keys for extension
        public const string KeyCacheType = "cache_type";
        public const string KeyCacheComponentType = "cache_component_type";
        public const string KeyCacheExpire = "cache_expire";
        public const string KeyEnableExpire = "enable_expire";
        public const string KeyTryWait = "try_wait";
        public const string KeyLoggerType = "logger_type";
        public const string KeyLogLevel = "log_level";
        public const string KeyExternalResourceRoot = "external_resources_root";
        public const string KeyInternalResourceRoot = "internal_resources_root";
        public const string KeyAccessServiceType = "access_service_type";
        public const string KeyDefaultResourceFormat = "default_resource_format";
        public const string KeySourceLocale = "source_locale";
        public const string KeyLoadOnStartup = "load_on_startup";

        // Special values
        public const string CacheByKey = "by_key";

        // Default values
        public const string DefaultType = "default";    // KeyCacheType, KeyLoggerType, KeyAccessServiceType
        public const string DefaultLocale = "en-US";    // KeyDefaultLocale
        public const string DefaultDebugLevel = "Debug";// KeyLogLevel
        public const int DefaultCacheExpire = 60;       // KeyCacheExpire
        public const int DefaultTryWait = 10;           // KeyTryWait

        // Placeholders
        public const string PlaceComponent = "$COMPONENT";
        public const string PlaceLocale = "$LOCALE";
        public const string PlaceLocaleCommon = "$LC";

        // Net errors
        public const string NetTimeout = "[STATUS]Timeout";
        public const string NetFailConnect = "[STATUS]ConnectFailure";
    }
}
