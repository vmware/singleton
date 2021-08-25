/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;
using SingletonClient.Implementation.Data;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyComponents
    {
        private readonly List<string> _componentTable = new List<string>();
        private readonly ISingletonTable<int> _componentIndexTable = new SingletonTable<int>();

        public int GetId(string component)
        {
            if (string.IsNullOrEmpty(component))
            {
                return -1;
            }

            object componentIndex = _componentIndexTable.GetObject(component);
            if (componentIndex != null)
            {
                return (int)componentIndex;
            }

            int index = _componentIndexTable.GetCount();
            _componentTable.Add(component);
            _componentIndexTable.SetItem(component, index);
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
