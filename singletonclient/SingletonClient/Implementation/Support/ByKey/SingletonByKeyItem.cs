/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyItem
    {
        private readonly short _componentIndex;
        private readonly short _pageIndex;
        private readonly short _indexInPage;
        private byte _sourceStatus;
        private SingletonByKeyItem _next;

        public SingletonByKeyItem(int componentIndex, int itemIndex)
        {
            this._componentIndex = (short)componentIndex;

            int value = itemIndex / SingletonByKeyRelease.PAGE_MAX_SIZE;
            this._pageIndex = (short)value;

            value = itemIndex % SingletonByKeyRelease.PAGE_MAX_SIZE;
            this._indexInPage = (short)value;

            _sourceStatus = 0x01;
        }

        public int GetComponentIndex()
        {
            return this._componentIndex;
        }

        public int GetPageIndex()
        {
            return this._pageIndex;
        }

        public int GetIndexInPage()
        {
            return this._indexInPage;
        }

        public SingletonByKeyItem GetNext()
        {
            return _next;
        }

        public void SetNext(SingletonByKeyItem next)
        {
            this._next = next;
        }

        public byte GetSourceStatus()
        {
            return _sourceStatus;
        }

        public void SetSourceStatus(byte status)
        {
            _sourceStatus = status;
        }
    }
}
