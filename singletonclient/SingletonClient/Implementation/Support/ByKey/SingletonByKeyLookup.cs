/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyLookup
    {
        public string Key { get; }
        public int ComponentIndex { get; }
        public string Message { get; }

        public SingletonByKeyItem AboveItem { get; set; }
        public SingletonByKeyItem CurrentItem { get; set; }
        public int AddType { get; set; }

        public SingletonByKeyLookup(string key, int componentIndex, string message)
        {
            Key = key;
            ComponentIndex = componentIndex;
            Message = message;
        }
    }
}
