/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient
{
    /// <summary>
    /// Interface to access a source object.
    /// </summary>
    public interface ISource
    {
        /// <summary>
        /// Get component name.
        /// </summary>
        /// <returns></returns>
        string GetComponent();

        /// <summary>
        /// Get key string.
        /// </summary>
        /// <returns></returns>
        string GetKey();

        /// <summary>
        /// Get source string.
        /// </summary>
        /// <returns></returns>
        string GetSource();

        /// <summary>
        /// Get comment.
        /// </summary>
        /// <returns></returns>
        string GetComment();
    }
}

