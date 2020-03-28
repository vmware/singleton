/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;

namespace SingletonClient
{
    /// <summary>
    /// Interface to access config data.
    /// </summary>
    public interface IConfig
    {
        /// <summary>
        /// Get product name.
        /// </summary>
        /// <returns></returns>
        string GetProduct();

        /// <summary>
        /// Get version of the product.
        /// </summary>
        /// <returns></returns>
        string GetVersion();

        /// <summary>
        /// Get string value of an config item.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        string GetStringValue(string key);

        /// <summary>
        /// Get bool value of an config item.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        bool GetBoolValue(string key);

        /// <summary>
        /// Get integer value of an config item.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        int GetIntValue(string key);

        /// <summary>
        /// Get component list
        /// </summary>
        /// <returns></returns>
        List<string> GetComponentList();

        /// <summary>
        /// Get source list of a component
        /// </summary>
        /// <param name="component"></param>
        /// <returns></returns>
        List<string> GetComponentSourceList(string component);

        /// <summary>
        /// Read text of a resource with the name.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        string ReadResourceText(string resourceName);

        /// <summary>
        /// Read resource and build hash table with the name and a parser object.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <param name="parser"></param>
        /// <returns></returns>
        Hashtable ReadResourceMap(string resourceName, ISourceParser parser);
    }

    public class ConfigConst
    {
        public const string KeyProduct = "productName";
        public const string KeyVersion = "version";
        public const string KeyCacheType = "cacheType";
        public const string KeyAccessServiceType = "accessServiceType";
        public const string KeyService = "singletonServer";
        public const string KeyTryDelay = "tryDelay";
        public const string KeyInterval = "cacheExpiredTime";
        public const string KeyLogger = "logger";
        public const string KeyLogType = "logType";
        public const string KeyPseudo = "pseudo";
        public const string KeyMachine = "machineTranslation";
        public const string KeyCollect = "collectSource";
        public const string KeyComponents = "components";
        public const string KeyComponent = "component";
        public const string KeyResource = "resource";
        public const string KeyName = "name";

        public const string TypeDefault = "default";
    }
}

