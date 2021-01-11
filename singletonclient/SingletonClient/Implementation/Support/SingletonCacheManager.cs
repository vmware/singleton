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
                ISingletonConfig singletonConfig = new SingletonConfigWrapper(config);
                string cacheComponentType = singletonConfig.GetCacheComponentType();
                cache = new SingletonCacheReleaseMessages(cacheComponentType);
                versions[version] = cache;
            }
            return cache;
        }
    }
}
