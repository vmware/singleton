/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyComponents
    {
        private readonly List<string> _componentTable;
        private readonly Hashtable _componentIndexTable;

        public SingletonByKeyComponents()
        {
            _componentTable = new List<string>();
            _componentIndexTable = Hashtable.Synchronized(new Hashtable(StringComparer.OrdinalIgnoreCase));
        }

        public int GetId(string component)
        {
            if (string.IsNullOrEmpty(component))
            {
                return -1;
            }

            object componentIndex = _componentIndexTable[component];
            if (componentIndex != null)
            {
                return (int)componentIndex;
            }

            int index = _componentIndexTable.Count;
            _componentTable.Add(component);
            _componentIndexTable[component] = index;
            return index;
        }

        public string GetName(int id)
        {
            if (id < 0 || id >= _componentTable.Count)
            {
                return null;
            }

            return _componentTable[id];
        }
    }
}
