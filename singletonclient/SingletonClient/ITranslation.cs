/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;

namespace SingletonClient
{
    /// <summary>
    /// Interface to access translations.
    /// </summary>
    public interface ITranslation
    {
        /// <summary>
        /// Create a source object
        /// </summary>
        /// <param name="component"></param>
        /// <param name="key"></param>
        /// <param name="source"></param>
        /// <param name="comment"></param>
        /// <returns></returns>
        ISource CreateSource(
            string component, string key, string source = null, string comment = null);

        /// <summary>
        /// Get translation of the locale for the source object.
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="source"></param>
        /// <returns></returns>
        string GetString(string locale, ISource source);

        /// <summary>
        /// Get translation.
        /// </summary>
        /// <param name="component"></param>
        /// <param name="key"></param>
        /// <param name="source"></param>
        /// <param name="comment"></param>
        /// <returns></returns>
        string GetString(
            string component, string key, string source = null, string comment = null);

        /// <summary>
        /// Get translation after formatting.
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="source"></param>
        /// <param name="objects"></param>
        /// <returns></returns>
        string Format(string locale, ISource source, params object[] objects);

        /// <summary>
        /// Set locale of current thread.
        /// </summary>
        /// <param name="locale"></param>
        /// <returns></returns>
        bool SetCurrentLocale(string locale);

        /// <summary>
        /// Get locale of current thread.
        /// </summary>
        /// <returns></returns>
        string GetCurrentLocale();

        /// <summary>
        /// Send a group of sources.
        /// </summary>
        /// <param name="sourceList"></param>
        /// <returns></returns>
        bool SendSource(List<ISource> sourceList);
    }
}

