/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    public class SingletonCacheComponentManager: ICacheComponentManager
    {
        public IComponentMessages NewComponentCache(string locale, string component)
        {
            IComponentMessages cache = new SingletonCacheComponentMessages(locale, component);
            return cache;
        }
    }
}
