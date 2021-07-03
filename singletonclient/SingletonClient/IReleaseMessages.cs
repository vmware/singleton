/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;

namespace SingletonClient
{
    /// <summary>
    /// Interface for product data in a cache.
    /// </summary>
    public interface ICacheMessages
    {
        /// <summary>
        /// Get interface to access locale messages in a cache.
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="asSource">Stored as source messages.</param>
        /// <returns>ILocaleMessages</returns>
        ILocaleMessages GetLocaleMessages(string locale, bool asSource = false);
    }

    /// <summary>
    /// Interface to access messages of a locale.
    /// </summary>
    public interface ILocaleMessages
    {
        /// <summary>
        /// Get locale.
        /// </summary>
        /// <returns></returns>
        string GetLocale();

        /// <summary>
        /// Get component name list.
        /// </summary>
        /// <returns></returns>
        List<string> GetComponentList();

        /// <summary>
        /// Get interface to access messages of a component.
        /// </summary>
        /// <param name="component"></param>
        /// <returns></returns>
        IComponentMessages GetComponentMessages(string component);

        /// <summary>
        /// Get message of a key in a component.
        /// </summary>
        /// <param name="component"></param>
        /// <param name="key"></param>
        /// <returns></returns>
        string GetString(string component, string key);
    }

    /// <summary>
    /// Interface to access messages of a component of a locale.
    /// </summary>
    public interface IComponentMessages
    {
        /// <summary>
        /// Set message in a component for a key.
        /// </summary>
        /// <param name="key"></param>
        /// <param name="message"></param>
        void SetString(string key, string message);

        /// <summary>
        /// Get message of the key.
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        string GetString(string key);

        /// <summary>
        /// Get key collection.
        /// </summary>
        /// <returns></returns>
        ICollection<string> GetKeys();

        /// <summary>
        /// Get item count.
        /// </summary>
        /// <returns></returns>
        int GetCount();

        /// <summary>
        /// Get locale.
        /// </summary>
        /// <returns></returns>
        string GetLocale();

        /// <summary>
        /// Get component name.
        /// </summary>
        /// <returns></returns>
        string GetComponent();

        /// <summary>
        /// Set resource path of the messages.
        /// </summary>
        /// <returns></returns>
        void SetResourcePath(string resourcePath);

        /// <summary>
        /// Get resource path of the messages.
        /// </summary>
        /// <returns></returns>
        string GetResourcePath();

        /// <summary>
        /// Set resource type of the messages.
        /// </summary>
        /// <returns></returns>
        void SetResourceType(string resourceType);

        /// <summary>
        /// Get resource type of the messages.
        /// </summary>
        /// <returns></returns>
        string GetResourceType();
    }

    /// <summary>
    /// Interface to access product messages.
    /// </summary>
    public interface IReleaseMessages
    {
        /// <summary>
        /// Get locale list.
        /// </summary>
        /// <returns></returns>
        List<string> GetLocaleList();

        /// <summary>
        /// Get component name list.
        /// </summary>
        /// <returns></returns>
        List<string> GetComponentList();

        /// <summary>
        /// Get interface to access messages of a locale.
        /// </summary>
        /// <param name="locale"></param>
        /// <returns></returns>
        ILocaleMessages GetLocaleMessages(string locale, bool asSource = false);

        /// <summary>
        /// Get a group of translation messages of different locales. 
        /// </summary>
        /// <returns></returns>
        Dictionary<string, ILocaleMessages> GetAllLocaleMessages();
    }
}
