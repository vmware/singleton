/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Concurrent;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support.ByKey
{
    public interface ISingletonByKeyRelease
    {
        ISingletonByKeyLocale GetLocaleItem(string locale, bool asSource);
        int GetComponentIndex(string component);
        int GetKeyCountInComponent(int componentIndex, ISingletonByKeyLocale localeItem);
        ICollection GetKeysInComponent(int componentIndex, ISingletonByKeyLocale localeItem);
        string GetString(string key, int componentIndex, ISingletonByKeyLocale localeItem);
        bool SetString(string key, int componentIndex, ISingletonByKeyLocale localeItem, string message);
        SingletonByKeyItem GetKeyItem(int pageIndex, int indexInPage);
    }

    public class SingletonLookup
    {
        protected string _key;
        protected int _componentIndex;
        protected string _message;
        protected bool _add;

        protected SingletonByKeyItem _aboveItem;
        protected SingletonByKeyItem _currentItem;

        public SingletonLookup(string key, int componentIndex, string message)
        {
            _key = key;
            _componentIndex = componentIndex;
            _message = message;
            _add = false;
        }

        public string Key
        {
            get { return _key; }
        }

        public int ComponentIndex
        {
            get { return _componentIndex; }
        }

        public string Message
        {
            get { return _message; }
        }

        public SingletonByKeyItem AboveItem
        {
            get { return _aboveItem; }
            set { _aboveItem = value; }
        }

        public SingletonByKeyItem CurrentItem
        {
            get { return _currentItem; }
            set { _currentItem = value; }
        }

        public bool Add
        {
            get { return _add; }
            set { _add = value; }
        }
    }

    public class SingletonByKeyRelease : ISingletonByKeyRelease
    {
        public const int PAGE_MAX_SIZE = 1024;
        public const int COMPONENT_PAGE_MAX_SIZE = 128;

        private readonly ISingletonRelease _release;
        private readonly SingletonByKeyComponents _compentTable;

        private readonly ConcurrentDictionary<string, SingletonByKeyItem> _keyAttrTable;
        private readonly SingletonByKeyTable<SingletonByKeyItem> _items;

        private readonly Hashtable _locales;
        private readonly Hashtable _sources;
        private int _itemCount = 0;

        private ISingletonByKeyLocale _sourceLocal;
        private ISingletonByKeyLocale _sourceRemote;

        private object _lockObject = new object();

        public SingletonByKeyRelease(ISingletonRelease release)
        {
            _release = release;

            _compentTable = new SingletonByKeyComponents();

            _keyAttrTable = new ConcurrentDictionary<string, SingletonByKeyItem>(StringComparer.InvariantCultureIgnoreCase);
            _items = new SingletonByKeyTable<SingletonByKeyItem>(SingletonByKeyRelease.PAGE_MAX_SIZE);

            _locales = SingletonUtil.NewHashtable(true);
            _sources = SingletonUtil.NewHashtable(true);
        }

        public bool SetItem(SingletonByKeyItem item, int pageIndex, int indexInPage)
        {
            SingletonByKeyItem[] array = _items.GetPage(pageIndex);
            if (array == null)
            {
                array = _items.NewPage(pageIndex);
            }

            array[indexInPage] = item;
            return true;
        }

        public int GetAndAddItemCount()
        {
            int count = _itemCount;
            _itemCount++;
            return count;
        }

        /// <summary>
        /// ISingletonByKeyRelease
        /// </summary>
        public ISingletonByKeyLocale GetLocaleItem(string locale, bool asSource)
        {
            Hashtable table = asSource ? _sources : _locales;
            ISingletonByKeyLocale item = (ISingletonByKeyLocale)table[locale];
            if (item == null)
            {
                item = new SingletonByKeyLocale(_release, locale, asSource);
                table[locale] = item;
            }
            return item;
        }

        /// <summary>
        /// ISingletonByKeyRelease
        /// </summary>
        public int GetComponentIndex(string component)
        {
            return _compentTable.GetId(component);
        }

        /// <summary>
        /// ISingletonByKeyRelease
        /// </summary>
        public int GetKeyCountInComponent(int componentIndex, ISingletonByKeyLocale localeItem)
        {
            int count = 0;
            if (localeItem != null)
            {
                foreach (var pair in _keyAttrTable)
                {
                    SingletonByKeyItem item = pair.Value;
                    if (item.GetComponentIndex() == componentIndex)
                    {
                        string message;
                        localeItem.GetMessage(item.GetPageIndex(), item.GetIndexInPage(), out message);
                        if (message != null)
                        {
                            count++;
                        }
                    }
                }
            }
            return count;
        }

        /// <summary>
        /// ISingletonByKeyRelease
        /// </summary>
        public ICollection GetKeysInComponent(int componentIndex, ISingletonByKeyLocale localeItem)
        {
            List<string> array = new List<string>();
            if (localeItem != null)
            {
                foreach (var pair in _keyAttrTable)
                {
                    SingletonByKeyItem item = pair.Value;
                    if (item.GetComponentIndex() == componentIndex)
                    {
                        string message;
                        localeItem.GetMessage(item.GetPageIndex(), item.GetIndexInPage(), out message);
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
        /// ISingletonByKeyRelease
        /// </summary>
        public string GetString(string key, int componentIndex, ISingletonByKeyLocale localeItem)
        {
            SingletonByKeyItem item;
            _keyAttrTable.TryGetValue(key, out item);

            if (componentIndex >= 0)
            {
                while (item != null)
                {
                    if (item.GetComponentIndex() == componentIndex)
                    {
                        break;
                    }
                    item = item.GetNext();
                }
            }
            if (item == null)
            {
                return null;
            }

            string message;
            bool pageNotNull = localeItem.GetMessage(item.GetPageIndex(), item.GetIndexInPage(), out message);
            if (pageNotNull)
            {
                return message;
            }
            return null;
        }

        private SingletonByKeyItem NewKeyItem(int componentIndex)
        {
            int itemIndex = GetAndAddItemCount();
            SingletonByKeyItem item = new SingletonByKeyItem(componentIndex, itemIndex);
            SetItem(item, item.GetPageIndex(), item.GetIndexInPage());
            return item;
        }

        private void FindOrAdd(SingletonLookup lookup)
        {
            SingletonByKeyItem item = null;
            if (lookup.AboveItem == null)
            {
                bool found = _keyAttrTable.TryGetValue(lookup.Key, out item);
                if (!found || item == null)
                {
                    lookup.CurrentItem = NewKeyItem(lookup.ComponentIndex);
                    lookup.Add = true;
                    return;
                }

                if (item.GetComponentIndex() == lookup.ComponentIndex)
                {
                    lookup.CurrentItem = item;
                    return;
                }

                lookup.AboveItem = item;
            }

            SingletonByKeyItem next = lookup.AboveItem.GetNext();
            if (next == null)
            {
                item = NewKeyItem(lookup.ComponentIndex);
                lookup.CurrentItem = item;
                lookup.Add = true;
                return;
            } 
                
            if (next.GetComponentIndex() == lookup.ComponentIndex)
            {
                lookup.CurrentItem = next;
                return;
            }

            lookup.AboveItem = next;
            this.FindOrAdd(lookup);
        }

        private bool DoSetString(string key, int componentIndex, ISingletonByKeyLocale localeItem, string message)
        {
            SingletonLookup lookup = new SingletonLookup(key, componentIndex, message);
            this.FindOrAdd(lookup);

            SingletonByKeyItem item = lookup.CurrentItem;
            if (item == null)
            {
                return false;
            }

            bool done = localeItem.SetMessage(message, item.GetPageIndex(), item.GetIndexInPage());
            if (done && localeItem.IsSourceLocale())
            {
                byte status = item.GetSourceStatus();
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
                    _sourceLocal.GetMessage(item.GetPageIndex(), item.GetIndexInPage(), out localSource);
                    string remoteSource;
                    _sourceRemote.GetMessage(item.GetPageIndex(), item.GetIndexInPage(), out remoteSource);
                    if (String.Equals(localSource, remoteSource))
                    {
                        status |= 0x01;
                    }
                    else
                    {
                        status &= 0x06;
                    }
                }
                item.SetSourceStatus(status);
            }

            // Finally, it's added in the table after it has been prepared.
            if (lookup.Add)
            {
                if (lookup.AboveItem == null)
                {
                    _keyAttrTable[key] = lookup.CurrentItem;
                } else
                {
                    lookup.AboveItem.SetNext(lookup.CurrentItem);
                }
            }
            return done;
        }

        /// <summary>
        /// ISingletonByKeyRelease
        /// </summary>
        public bool SetString(string key, int componentIndex, ISingletonByKeyLocale localeItem, string message)
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

                return this.DoSetString(key, componentIndex, localeItem, message);
            }
        }

        /// <summary>
        /// ISingletonByKeyRelease
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
    }
}
