/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient
{
    /// <summary>
    /// Interface for a release of a product and one of its version.
    /// </summary>
    public interface IRelease
    {
        /// <summary>
        /// Get interface of config of the release.
        /// </summary>
        /// <returns></returns>
        IConfig GetConfig();

        /// <summary>
        /// Get interface to access product messages.
        /// </summary>
        /// <returns></returns>
        IProductMessages GetMessages();

        /// <summary>
        /// Get interface to access translations.
        /// </summary>
        /// <returns></returns>
        ITranslation GetTranslation();
    }
}

