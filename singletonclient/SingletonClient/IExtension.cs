/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;

namespace SingletonClient
{
    public enum LogType
    {
        Debug,
        Info,
        Warning,
        Error,
        None
    }

    /// <summary>
    /// Interface for log function provider.
    /// </summary>
    public interface ILog
    {
        /// <summary>
        /// Log text with a type.
        /// </summary>
        /// <param name="logType"></param>
        /// <param name="text"></param>
        void Log(LogType logType, string text);
    }

    /// <summary>
    /// Interface for service access provider.
    /// </summary>
    public interface IAccessService
    {
        /// <summary>
        /// Send a http get request.
        /// </summary>
        /// <param name="url"></param>
        /// <param name="headers"></param>
        /// <returns></returns>
        string HttpGet(string url, Hashtable headers, int timeout, out string status, ILog logger = null);

        /// <summary>
        /// Send a http post request.
        /// </summary>
        /// <param name="url"></param>
        /// <param name="text"></param>
        /// <param name="headers"></param>
        /// <returns></returns>
        string HttpPost(string url, string text, Hashtable headers, int timeout, out string status, ILog logger = null);
    }

    /// <summary>
    /// Interface for a resource parser.
    /// </summary>
    public interface IResourceParser
    {
        /// <summary>
        /// Parse the text into a hash table.
        /// </summary>
        /// <param name="text"></param>
        /// <returns></returns>
        Hashtable Parse(string text);
    }

    /// <summary>
    /// Interface for a cache manager of a cache implementation.
    /// </summary>
    public interface ICacheManager
    {
        /// <summary>
        /// Get interface to access product data in a cache.
        /// </summary>
        /// <param name="product"></param>
        /// <param name="version"></param>
        /// <returns></returns>
        ICacheMessages GetReleaseCache(string product, string version);
    }

    /// <summary>
    /// Interface for a component cache manager.
    /// </summary>
    public interface ICacheComponentManager
    {
        /// <summary>
        /// Create a component cache object.
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="component"></param>
        /// <param name="asSource"></param>
        /// <returns></returns>
        IComponentMessages NewComponentCache(string locale, string component, bool asSource = false);
    }

    /// <summary>
    /// Interface for extending different implementations.
    /// </summary>
    public interface IExtension
    {
        /// <summary>
        /// Register a cache manager with its name.
        /// </summary>
        /// <param name="cacheManager"></param>
        /// <param name="cacheManagerName"></param>
        void RegisterCacheManager(ICacheManager cacheManager, string cacheManagerName);

        /// <summary>
        /// Register a component cache manager with its name.
        /// </summary>
        /// <param name="cacheComponentManager"></param>
        /// <param name="cachComponenteManagerName"></param>
        void RegisterCacheComponentManager(ICacheComponentManager cacheComponentManager,
            string cacheComponentManagerName);

        /// <summary>
        /// Register a logger with its name.
        /// </summary>
        /// <param name="logger"></param>
        /// <param name="loggerName"></param>
        void RegisterLogger(ILog logger, string loggerName);

        /// <summary>
        /// Register a resource parser with its name.
        /// </summary>
        /// <param name="parser"></param>
        /// <param name="parserName"></param>
        void RegisterResourceParser(IResourceParser parser, string parserName);

        /// <summary>
        /// Register service access object with its name.
        /// </summary>
        /// <param name="accessService"></param>
        /// <param name="accessrName"></param>
        void RegisterAccessService(IAccessService accessService, string accessrName);
    }
}

