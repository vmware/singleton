/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using System.Collections;

    public class SingletonCacheComponentMessages : IComponentMessages
    {
        private readonly string _locale;
        private readonly string _component;
        private readonly bool _asSource;
        private string _resourcePath;
        private string _resourceType;
        private readonly Hashtable _messages = SingletonUtil.NewHashtable(true);

        public SingletonCacheComponentMessages(string locale, string component, bool asSource)
        {
            _component = component;
            _locale = locale;
            _asSource = asSource;
        }

        public void SetString(string key, string message)
        {
            _messages[key] = message;
        }

        public int GetCount()
        {
            return _messages.Keys.Count;
        }

        public ICollection GetKeys()
        {
            return _messages.Keys;
        }

        public string GetString(string key)
        {
            if (key == null)
            {
                return null;
            }
            string message = (string)_messages[key];
            return message;
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
    }
}
