/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonCacheManager : ICacheManager
    {
        private Hashtable _releases = SingletonUtil.NewHashtable();

        public ICacheMessages GetProductCache(string product, string version)
        {
            Hashtable locales = (Hashtable)_releases[product];
            if (locales == null)
            {
                locales = SingletonUtil.NewHashtable();
                _releases[product] = locales;
            }
            ICacheMessages cache = (ICacheMessages)locales[version];
            if (cache == null)
            {
                cache = new SingletonProductCache();
                locales[version] = cache;
            }
            return cache;
        }
    }

    public class SingletonProductCache : ICacheMessages
    {
        private Hashtable _locales = SingletonUtil.NewHashtable();

        public ILanguageMessages GetLanguageMessages(string locale)
        {
            if (locale == null)
            {
                return null;
            }

            ILanguageMessages cache = (ILanguageMessages)_locales[locale];
            if (cache == null)
            {
                cache = new SingletonLanguageCache(locale);
                _locales[locale] = cache;
            }
            return cache;
        }
    }

    public class SingletonLanguageCache : ILanguageMessages
    {
        private string _locale;
        private Hashtable _components = SingletonUtil.NewHashtable();

        public SingletonLanguageCache(string locale)
        {
            _locale = locale;
        }

        public IComponentMessages GetComponentMessages(string component)
        {
            if (component == null)
            {
                return null;
            }

            IComponentMessages cache = (IComponentMessages)_components[component];
            if (cache == null)
            {
                cache = new SingletonComponentCache(_locale, component);
                _components[component] = cache;
            }
            return cache;
        }

        public List<string> GetComponentList()
        {
            List<string> componentList = new List<string>();
            foreach (var key in _components.Keys)
            {
                componentList.Add(key.ToString());
            }
            return componentList;
        }

        public string GetString(string component, string key)
        {
            IComponentMessages componentCache = GetComponentMessages(component);
            return (componentCache == null) ? null : componentCache.GetString(key);
        }
    }

    public class SingletonComponentCache : IComponentMessages
    {
        private string _locale;
        private string _component;
        private Hashtable _messages = SingletonUtil.NewHashtable();

        public SingletonComponentCache(string locale, string component)
        {
            _component = component;
            _locale = locale;
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
    }
}

