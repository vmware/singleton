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
    }
}
