/*
 * Copyright 2020 VMware, Inc.
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
        string HttpGet(string url, Hashtable headers);

        /// <summary>
        /// Send a http post request.
        /// </summary>
        /// <param name="url"></param>
        /// <param name="text"></param>
        /// <param name="headers"></param>
        /// <returns></returns>
        string HttpPost(string url, string text, Hashtable headers);
    }

    /// <summary>
    /// Interface for a source parser.
    /// </summary>
    public interface ISourceParser
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
        ICacheMessages GetProductCache(string product, string version);
    }

    /// <summary>
    /// Interface for product data in a cache.
    /// </summary>
    public interface ICacheMessages
    {
        /// <summary>
        /// Get interface to access locale data in a cache.
        /// </summary>
        /// <param name="locale"></param>
        /// <returns></returns>
        ILanguageMessages GetLanguageMessages(string locale);
    }

    /// <summary>
    /// Interface for extending different implementations.
    /// </summary>
    public interface IExtension
    {
        /// <summary>
        /// Set a cache manager with its name.
        /// </summary>
        /// <param name="cacheManager"></param>
        /// <param name="cacheManagerName"></param>
        void SetCacheManager(ICacheManager cacheManager, string cacheManagerName);

        /// <summary>
        /// Set a logger with its name.
        /// </summary>
        /// <param name="logger"></param>
        /// <param name="loggerName"></param>
        void SetLogger(ILog logger, string loggerName);

        /// <summary>
        /// Set a source parser with its name.
        /// </summary>
        /// <param name="parser"></param>
        /// <param name="parserName"></param>
        void SetSourceParser(ISourceParser parser, string parserName);

        /// <summary>
        /// Set service access object with its name. 
        /// </summary>
        /// <param name="accessService"></param>
        /// <param name="accessrName"></param>
        void SetAccessService(IAccessService accessService, string accessrName);
    }
}

