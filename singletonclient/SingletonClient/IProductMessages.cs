/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;

namespace SingletonClient
{
    /// <summary>
    /// Interface to access messages of a locale.
    /// </summary>
    public interface ILanguageMessages
    {
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
        ICollection GetKeys();

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
    }

    /// <summary>
    /// Interface to access product messages.
    /// </summary>
    public interface IProductMessages
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
        /// Get interface to access all source messages.
        /// </summary>
        /// <returns></returns>
        ILanguageMessages GetAllSource();

        /// <summary>
        /// Get interface to access messages of a locale.
        /// </summary>
        /// <param name="locale"></param>
        /// <returns></returns>
        ILanguageMessages GetTranslation(string locale);

        /// <summary>
        /// Get a group of translation messages of different locales. 
        /// </summary>
        /// <returns></returns>
        Dictionary<string, ILanguageMessages> GetAllTranslation();
    }
}

