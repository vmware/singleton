/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyTable<T>
    {
        private readonly T[][] _table;
        private readonly int _max;

        public SingletonByKeyTable(int max)
        {
            _max = max;
            _table = new T[max][];
        }

        public T[] GetPage(int id)
        {
            return _table[id];
        }

        public T[] NewPage(int id)
        {
            T[] array = new T[_max];
            _table[id] = array;
            return array;
        }

        public T GetItem(int pageIndex, int indexInPage)
        {
            T[] array = this.GetPage(pageIndex);
            if (array == null)
            {
                return default(T);
            }
            return array[indexInPage];
        }

        public bool SetItem(int pageIndex, int indexInPage, T item)
        {
            T[] array = this.GetPage(pageIndex);
            if (array == null)
            {
                array = NewPage(pageIndex);
            }
            bool isNew = array[indexInPage] == null;
            array[indexInPage] = item;
            return isNew;
        }

        public T GetItemByOneIndex(int index)
        {
            int pageIndex = index / _max;
            int indexInPage = index % _max;
            return GetItem(pageIndex, indexInPage);
        }

        public bool SetItemByOneIndex(int index, T item)
        {
            int pageIndex = index / _max;
            int indexInPage = index % _max;
            return SetItem(pageIndex, indexInPage, item);
        }
    }
}
