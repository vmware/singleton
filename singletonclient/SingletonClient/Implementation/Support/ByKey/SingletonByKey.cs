/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using SingletonClient.Implementation.Data;

namespace SingletonClient.Implementation.Support.ByKey
{
    public interface ISingletonByKey
    {
        string GetSourceLocale();
        ISingletonByKeyLocale GetLocaleItem(string locale, bool asSource);
        int GetComponentIndex(string component);
        int GetKeyCountInComponent(int componentIndex, ISingletonByKeyLocale localeItem);
        ICollection<string> GetKeysInComponent(int componentIndex, ISingletonByKeyLocale localeItem);
        string GetString(string key, int componentIndex, ISingletonByKeyLocale localeItem, bool needFallback = false);
        bool SetString(string key, ISingletonComponent componentObject, int componentIndex,
            ISingletonByKeyLocale localeItem, string message);
        SingletonByKeyItem GetKeyItem(int pageIndex, int indexInPage);
    }

    public class SingletonByKey : ISingletonByKey
    {
        public const int PAGE_MAX_SIZE = 1024;
        public const int COMPONENT_PAGE_MAX_SIZE = 128;

        private readonly string _localeSource;
        private readonly string _localeDefault;
        private readonly bool _isDifferent;
        private readonly bool _onlyByKey;
        private readonly bool _isPseudo;
        private readonly SingletonByKeyComponents _compentTable;

        private readonly ConcurrentDictionary<string, SingletonByKeyItem> _keyAttrTable;
        private readonly SingletonByKeyTable<SingletonByKeyItem> _items;

        private readonly ISingletonTable<ISingletonByKeyLocale> _locales;
        private readonly ISingletonTable<ISingletonByKeyLocale> _sources;
        private int _itemCount = 0;

        private ISingletonByKeyLocale _sourceLocal;
        private ISingletonByKeyLocale _sourceRemote;
        private ISingletonByKeyLocale _defaultRemote;

        private readonly object _lockObject = new object();

        public SingletonByKey(ISingletonConfig config, string cacheType)
        {
            _localeSource = config.GetSourceLocale();
            _onlyByKey = string.Compare(ConfigConst.CacheByKey, cacheType, StringComparison.InvariantCultureIgnoreCase) == 0;

            _compentTable = new SingletonByKeyComponents();

            _keyAttrTable = new ConcurrentDictionary<string, SingletonByKeyItem>(StringComparer.InvariantCultureIgnoreCase);
            _items = new SingletonByKeyTable<SingletonByKeyItem>(SingletonByKey.PAGE_MAX_SIZE);

            _locales = new SingletonTable<ISingletonByKeyLocale>();
            _sources = new SingletonTable<ISingletonByKeyLocale>();

            _localeDefault = config.GetDefaultLocale();
            _isDifferent = !config.IsSourceLocaleDefault();

            _isPseudo = config.IsPseudo();
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public string GetSourceLocale()
        {
            return _localeSource;
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public ISingletonByKeyLocale GetLocaleItem(string locale, bool asSource)
        {
            ISingletonTable<ISingletonByKeyLocale> table = asSource ? _sources : _locales;
            ISingletonByKeyLocale item = table.GetItem(locale);
            if (item == null)
            {
                ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                for (int i = 1; i < singletonLocale.GetCount(); i++)
                {
                    string one = singletonLocale.GetNearLocale(i);
                    if (table.GetObject(one) != null)
                    {
                        table.SetItem(locale, table.GetItem(one));
                        return table.GetItem(one);
                    }
                }

                item = new SingletonByKeyLocale(this, locale, asSource);
                for (int i = 0; i < singletonLocale.GetCount(); i++)
                {
                    string one = singletonLocale.GetNearLocale(i);
                    table.SetItem(one, item);
                }
            }
            return item;
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public int GetComponentIndex(string component)
        {
            return _compentTable.GetId(component);
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public int GetKeyCountInComponent(int componentIndex, ISingletonByKeyLocale localeItem)
        {
            return localeItem.GetKeyCountInComponent(componentIndex);
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public ICollection<string> GetKeysInComponent(int componentIndex, ISingletonByKeyLocale localeItem)
        {
            List<string> array = new List<string>();
            if (localeItem != null)
            {
                foreach (var pair in _keyAttrTable)
                {
                    SingletonByKeyItem item = pair.Value;
                    if (item.ComponentIndex == componentIndex)
                    {
                        string message;
                        localeItem.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out message);
                        if (message != null)
                        {
                            array.Add(pair.Key);
                        }
                    }
                }
            }
            return array;
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public string GetString(string key, int componentIndex, ISingletonByKeyLocale localeItem, bool needFallback = false)
        {
            if (componentIndex < 0 && !_onlyByKey)
            {
                return null;
            }

            SingletonByKeyItem item;
            _keyAttrTable.TryGetValue(key, out item);

            if (componentIndex >= 0)
            {
                while (item != null)
                {
                    if (item.ComponentIndex == componentIndex)
                    {
                        break;
                    }
                    item = item.Next;
                }
            }
            if (item == null)
            {
                localeItem.CheckTask(componentIndex, needFallback);
                return null;
            }

            string message = null;
            if (!needFallback)
            {
                if (localeItem.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out message))
                {
                    return message;
                }
                return null;
            }

            if ((item.SourceStatus & 0x01) == 0x01)
            {
                bool success = localeItem.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out message);
                if (success)
                {
                    return message;
                }
            }

            if (_isDifferent)
            {
                if ((item.SourceStatus & 0x01) == 0 &&
                    _sourceLocal.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out message))
                {
                    return message;
                }
                if (_defaultRemote == null)
                {
                    _defaultRemote = this.GetLocaleItem(_localeDefault, false);
                }
                if (_defaultRemote.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out message))
                {
                    return message;
                }
            }

            if (message == null)
            {
                string text;
                if ((item.SourceStatus & 0x04) == 0x04)
                {
                    if (_sourceLocal.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out text))
                    {
                        message = text;
                    }
                }
                else if ((item.SourceStatus & 0x02) == 0x02)
                {
                    bool success = _sourceRemote.GetMessage(
                        componentIndex, item.PageIndex, item.IndexInPage, out text);
                    if (success)
                    {
                        message = text;
                    }
                }
                if (_isPseudo && message != null)
                {
                    message = SingletonUtil.AddPseudo(message);
                }
            }

            return message;
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public bool SetString(string key, ISingletonComponent componentObject, int componentIndex,
            ISingletonByKeyLocale localeItem, string message)
        {
            if (message == null || key == null || localeItem == null)
            {
                return false;
            }
            string text = this.GetString(key, componentIndex, localeItem);
            if (message.Equals(text))
            {
                return false;
            }

            lock (_lockObject)
            {
                text = this.GetString(key, componentIndex, localeItem);
                if (message.Equals(text))
                {
                    return false;
                }

                return this.DoSetString(key, componentObject, componentIndex, localeItem, message);
            }
        }

        /// <summary>
        /// ISingletonByKey
        /// </summary>
        public SingletonByKeyItem GetKeyItem(int pageIndex, int indexInPage)
        {
            SingletonByKeyItem[] array = _items.GetPage(pageIndex);
            if (array == null)
            {
                return null;
            }

            return array[indexInPage];
        }

        private void SetItem(SingletonByKeyItem item, int pageIndex, int indexInPage)
        {
            SingletonByKeyItem[] array = _items.GetPage(pageIndex);
            if (array == null)
            {
                array = _items.NewPage(pageIndex);
            }

            array[indexInPage] = item;
        }

        private int GetAndAddItemCount()
        {
            int count = _itemCount;
            _itemCount++;
            return count;
        }

        private SingletonByKeyItem NewKeyItem(int componentIndex)
        {
            int itemIndex = GetAndAddItemCount();
            SingletonByKeyItem item = new SingletonByKeyItem(componentIndex, itemIndex);
            SetItem(item, item.PageIndex, item.IndexInPage);
            return item;
        }

        private void FindOrAdd(SingletonByKeyLookup lookup)
        {
            SingletonByKeyItem item;
            bool found = _keyAttrTable.TryGetValue(lookup.Key, out item);
            if (!found || item == null)
            {
                lookup.CurrentItem = NewKeyItem(lookup.ComponentIndex);
                lookup.AddType = 1;
                return;
            }

            while (item != null)
            {
                if (item.ComponentIndex == lookup.ComponentIndex)
                {
                    lookup.CurrentItem = item;
                    return;
                }
                lookup.AboveItem = item;
                item = item.Next;
            }

            lookup.CurrentItem = NewKeyItem(lookup.ComponentIndex);
            lookup.AddType = 2;
        }

        private bool DoSetString(string key, ISingletonComponent componentObject, int componentIndex,
            ISingletonByKeyLocale localeItem, string message)
        {
            SingletonByKeyLookup lookup = new SingletonByKeyLookup(key, componentIndex, message);
            this.FindOrAdd(lookup);

            SingletonByKeyItem item = lookup.CurrentItem;
            if (item == null)
            {
                return false;
            }

            bool done = localeItem.SetMessage(message, componentObject, componentIndex, item.PageIndex, item.IndexInPage);
            if (done && localeItem.IsSourceLocale())
            {
                byte status = item.SourceStatus;
                if (localeItem.IsSource())
                {
                    _sourceLocal = localeItem;
                    status |= 0x04;
                }
                else if (localeItem.IsSourceLocale())
                {
                    _sourceRemote = localeItem;
                    status |= 0x02;
                }
                if ((status & 0x06) != 0x06)
                {
                    status |= 0x01;
                }
                else
                {
                    string localSource;
                    _sourceLocal.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out localSource, false);
                    string remoteSource;
                    _sourceRemote.GetMessage(componentIndex, item.PageIndex, item.IndexInPage, out remoteSource, false);
                    if (_isPseudo || String.Equals(localSource, remoteSource))
                    {
                        status |= 0x01;
                    }
                    else
                    {
                        status &= 0x06;
                    }
                }
                item.SourceStatus = status;
            }

            // Finally, it's added in the table after it has been prepared.
            if (lookup.AddType == 1)
            {
                _keyAttrTable[key] = lookup.CurrentItem;
            }
            else if (lookup.AddType == 2)
            {
                lookup.AboveItem.Next = lookup.CurrentItem;
            }
            return done;
        }
    }
}
