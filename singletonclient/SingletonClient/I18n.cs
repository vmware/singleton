/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation;
using System.Reflection;

namespace SingletonClient
{
    /// <summary>
    /// Factory class of Singleton 
    /// </summary>
    public sealed class I18n
    {
        /// <summary>
        /// Load the configuration kept in the assembly's resource.
        /// </summary>
        /// <param name="resourceBaseName">Resource path</param>
        /// <param name="assembly">Assembly object</param>
        /// <param name="configResourceName">Configuration name in the assembly's resource</param>
        /// <returns></returns>
        public static IConfig LoadConfig(
            string resourceBaseName, Assembly assembly, string configResourceName)
        {
            ISingletonClientManager client = SingletonClientManager.GetInstance();
            return client.LoadConfig(resourceBaseName, assembly, configResourceName);
        }

        /// <summary>
        /// Get config object with the product name and its version.
        /// </summary>
        /// <param name="product"></param>
        /// <param name="version"></param>
        /// <returns></returns>
        public static IConfig GetConfig(string product, string version)
        {
            ISingletonClientManager client = SingletonClientManager.GetInstance();
            return client.GetConfig(product, version);
        }

        /// <summary>
        /// Get or create a release object with the config object.
        /// </summary>
        /// <param name="config"></param>
        /// <returns></returns>
        public static IRelease GetRelease(IConfig config)
        {
            ISingletonClientManager client = SingletonClientManager.GetInstance();
            return client.GetRelease(config);
        }

        /// <summary>
        /// Get extension interface.
        /// </summary>
        /// <returns></returns>
        public static IExtension GetExtension()
        {
            SingletonClientManager client = SingletonClientManager.GetInstance();
            return client;
        }
    }
}
