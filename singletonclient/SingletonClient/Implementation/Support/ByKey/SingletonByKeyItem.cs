/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyItem
    {
        public short ComponentIndex { get; }
        public short PageIndex { get; }
        public short IndexInPage { get; }
        public byte SourceStatus { get; set; }
        public SingletonByKeyItem Next { get; set; }

        public SingletonByKeyItem(int componentIndex, int itemIndex)
        {
            this.ComponentIndex = (short)componentIndex;

            int value = itemIndex / SingletonByKey.PAGE_MAX_SIZE;
            this.PageIndex = (short)value;

            value = itemIndex % SingletonByKey.PAGE_MAX_SIZE;
            this.IndexInPage = (short)value;

            SourceStatus = 0x01;
        }
    }
}
