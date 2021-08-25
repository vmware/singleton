/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.Base
{
    using SingletonClient.Implementation.Release;
    using System.Collections.Generic;

    public abstract class SingletonCacheBaseComponentMessages : IComponentMessages
    {
        protected readonly ISingletonRelease _release;
        protected readonly string _locale;
        protected readonly string _component;
        protected readonly bool _asSource;

        protected string _resourcePath;
        protected string _resourceType;

        protected SingletonCacheBaseComponentMessages(
            ISingletonRelease release, string locale, string component, bool asSource)
        {
            _release = release;
            _component = component;
            _locale = locale;
            _asSource = asSource;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public string GetLocale()
        {
            return _locale;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public string GetComponent()
        {
            return _component;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public void SetResourcePath(string resourcePath)
        {
            _resourcePath = resourcePath;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public string GetResourcePath()
        {
            return _resourcePath;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public void SetResourceType(string resourceType)
        {
            _resourceType = resourceType;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public string GetResourceType()
        {
            return _resourceType;
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public abstract void SetString(string key, string message);

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public abstract string GetString(string key);

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public abstract ICollection<string> GetKeys();

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public abstract int GetCount();
    }
}
