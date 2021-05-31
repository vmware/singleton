/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonCacheManager : ICacheManager
    {
        private readonly Hashtable _releases = SingletonUtil.NewHashtable(true);

        public ICacheMessages GetReleaseCache(string product, string version)
        {
            Hashtable versions = (Hashtable)_releases[product];
            if (versions == null)
            {
                versions = SingletonUtil.NewHashtable(true);
                _releases[product] = versions;
            }
            ICacheMessages cache = (ICacheMessages)versions[version];
            if (cache == null)
            {
                IConfig config = SingletonClientManager.GetInstance().GetConfig(product, version);
                ISingletonRelease release = (ISingletonRelease)SingletonClientManager.GetInstance().GetRelease(config);

                cache = new SingletonCacheReleaseMessages(release);
                versions[version] = cache;
            }
            return cache;
        }
    }
}
