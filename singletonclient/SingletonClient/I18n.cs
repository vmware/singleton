/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation;
using System.Collections.Generic;
using System.Reflection;

namespace SingletonClient
{
    /// <summary>
    /// Factory class of Singleton 
    /// </summary>
    public static class I18N
    {
        /// <summary>
        /// Load the configuration from text.
        /// </summary>
        /// <param name="text">Configuration</param>
        /// <returns></returns>
        public static IConfig LoadConfigFromText(string text)
        {
            ISingletonReleaseManager manager = SingletonReleaseManager.GetInstance();
            return manager.LoadConfig(text);
        }

        /// <summary>
        /// Load the configuration kept in the assembly's resource.
        /// </summary>
        /// <param name="resourceBaseName">Resource path</param>
        /// <param name="assembly">Assembly object</param>
        /// <param name="configResourceName">Configuration name in the assembly's resource</param>
        /// <returns></returns>
        public static IConfig LoadConfig(
            string resourceBaseName, Assembly assembly, string configResourceName, IConfig outsideConfig = null)
        {
            ISingletonReleaseManager manager = SingletonReleaseManager.GetInstance();
            return manager.LoadConfig(resourceBaseName, assembly, configResourceName, outsideConfig);
        }

        /// <summary>
        /// Get config object with the product name and its version.
        /// </summary>
        /// <param name="product"></param>
        /// <param name="version"></param>
        /// <returns></returns>
        public static IConfig GetConfig(string product, string version)
        {
            ISingletonReleaseManager manager = SingletonReleaseManager.GetInstance();
            return manager.GetConfig(product, version);
        }

        /// <summary>
        /// Get or create a release object with the config object.
        /// </summary>
        /// <param name="config"></param>
        /// <returns></returns>
        public static IRelease GetRelease(IConfig config)
        {
            ISingletonReleaseManager manager = SingletonReleaseManager.GetInstance();
            return manager.GetRelease(config);
        }

        /// <summary>
        /// Get extension interface.
        /// </summary>
        /// <returns></returns>
        public static IExtension GetExtension()
        {
            IExtension extension = SingletonReleaseManager.GetInstance();
            return extension;
        }
    }
}
