/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    public class SingletonCacheComponentManager: ICacheComponentManager
    {
        public IComponentMessages NewComponentCache(string locale, string component, bool asSource = false)
        {
            IComponentMessages cache = new SingletonCacheComponentMessages(locale, component, asSource);
            return cache;
        }
    }
}
