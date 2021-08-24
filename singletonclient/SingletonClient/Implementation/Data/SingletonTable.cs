/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;

namespace SingletonClient.Implementation.Data
{
    public interface ISingletonTableBase
    {
        object GetObject(string key);
        bool Contains(string key);
        ICollection GetKeys();
        int GetCount();
    }

    public interface ISingletonTable<T> : ISingletonTableBase
    {
        T GetItem(string key);
        void SetItem(string key, T item);
    }

    public class SingletonTable<T> : ISingletonTable<T>
    {
        private readonly Hashtable _table = SingletonUtil.NewHashtable(true);

        public T GetItem(string key)
        {
            return (T)_table[key];
        }

        public void SetItem(string key, T item)
        {
            _table[key] = item;
        }

        public object GetObject(string key)
        {
            return _table[key];
        }

        public bool Contains(string key)
        {
            return _table.Contains(key);
        }

        public ICollection GetKeys()
        {
            return _table.Keys;
        }

        public int GetCount()
        {
            return _table.Count;
        }
    }
}
