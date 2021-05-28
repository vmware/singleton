/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.Base
{
    using System.Collections;

    public abstract class SingletonComponentBaseMessages : IComponentMessages
    {
        protected readonly ISingletonRelease release;
        protected readonly string _locale;
        protected readonly string _component;
        protected readonly bool _asSource;
        protected string _resourcePath;
        protected string _resourceType;

        protected SingletonComponentBaseMessages(
            ISingletonRelease release, string locale, string component, bool asSource)
        {
            this.release = release;
            _component = component;
            _locale = locale;
            _asSource = asSource;
        }

        public string GetLocale()
        {
            return _locale;
        }

        public string GetComponent()
        {
            return _component;
        }

        public void SetResourcePath(string resourcePath)
        {
            _resourcePath = resourcePath;
        }

        public string GetResourcePath()
        {
            return _resourcePath;
        }

        public void SetResourceType(string resourceType)
        {
            _resourceType = resourceType;
        }

        public string GetResourceType()
        {
            return _resourceType;
        }

        public abstract void SetString(string key, string message);
        public abstract string GetString(string key);
        public abstract ICollection GetKeys();
        public abstract int GetCount();
    }
}
