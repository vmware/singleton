/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Data;
using SingletonClient.Implementation.Release;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonCacheManager : ICacheManager
    {
        private readonly ISingletonTable<ISingletonTable<ICacheMessages>> _releases =
            new SingletonTable<ISingletonTable<ICacheMessages>>();

        /// <summary>
        /// ICacheManager
        /// </summary>
        public ICacheMessages GetReleaseCache(string product, string version)
        {
            ISingletonTable<ICacheMessages> versions = _releases.GetItem(product);
            if (versions == null)
            {
                versions = new SingletonTable<ICacheMessages>();
                _releases.SetItem(product, versions);
            }
            ICacheMessages cache = versions.GetItem(version);
            if (cache == null)
            {
                IConfig config = SingletonReleaseManager.GetInstance().GetConfig(product, version);
                ISingletonRelease release = (ISingletonRelease)SingletonReleaseManager.GetInstance().GetRelease(config);

                cache = new SingletonCacheReleaseMessages(release);
                versions.SetItem(version, cache);
            }
            return cache;
        }
    }
}
