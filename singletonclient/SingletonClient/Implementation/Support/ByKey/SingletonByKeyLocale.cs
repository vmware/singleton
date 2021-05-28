/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support.ByKey
{
    public interface ISingletonByKeyLocale
    {
        bool IsSource();
        bool IsSourceLocale();
        int GetKeyCountInComponent(int componentIndex);
        bool GetMessage(int pageIndex, int indexInPage, out string message);
        bool SetMessage(string message, int pageIndex, int indexInPage);
        ISingletonComponent GetSingletonComponent(int componentIndex);
        bool SetSingletonComponent(int componentIndex, ISingletonComponent singletonComponent);
    }

    public class SingletonByKeyLocale : ISingletonByKeyLocale
    {
        private readonly ISingletonByKeyRelease _bykey;
        private readonly bool _asSource;
        private readonly bool _isSourceLocale;

        // Data table
        private readonly SingletonByKeyTable<ISingletonComponent> _components;
        private readonly SingletonByKeyTable<string> _messages;

        public SingletonByKeyLocale(ISingletonByKeyRelease bykey, string locale, bool asSource)
        {
            this._bykey = bykey;
            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            this._asSource = asSource;
            string sourceLocale = _bykey.GetSourceLocale();
            this._isSourceLocale = singletonLocale.Contains(sourceLocale);

            _components = new SingletonByKeyTable<ISingletonComponent> (SingletonByKeyRelease.COMPONENT_PAGE_MAX_SIZE);
            _messages = new SingletonByKeyTable<string>(SingletonByKeyRelease.PAGE_MAX_SIZE);
        }

        /// <summary>
        /// ISingletonByKeyLocale
        /// </summary>
        public bool IsSource()
        {
            return _asSource;
        }

        /// <summary>
        /// ISingletonByKeyLocale
        /// </summary>
        public bool IsSourceLocale()
        {
            return _isSourceLocale;
        }

        /// <summary>
        /// ISingletonByKeyLocale
        /// </summary>
        public int GetKeyCountInComponent(int componentIndex)
        {
            int count = 0;
            for (int i = 0; i < SingletonByKeyRelease.PAGE_MAX_SIZE; i++)
            {
                string[] page = _messages.GetPage(i);
                if (page == null)
                {
                    continue;
                }
                for (int k = 0; k < page.Length; k++)
                {
                    if (page[k] == null)
                    {
                        continue;
                    }
                    SingletonByKeyItem item = _bykey.GetKeyItem(i, k);
                    if (item != null && item.GetComponentIndex() == componentIndex)
                    {
                        count++;
                    }
                }
            }
            return count;
        }

        public bool GetMessage(int pageIndex, int indexInPage, out string message)
        {
            string[] array = _messages.GetPage(pageIndex);
            if (array == null)
            {
                message = null;
                return false;
            }

            message = array[indexInPage];
            return true;
        }

        public bool SetMessage(string message, int pageIndex, int indexInPage)
        {
            string[] array = _messages.GetPage(pageIndex);
            if (array == null)
            {
                array = _messages.NewPage(pageIndex);
            }

            array[indexInPage] = message;
            return true;
        }

        /// <summary>
        /// ISingletonByKeyLocale
        /// </summary>
        public ISingletonComponent GetSingletonComponent(int componentIndex)
        {
            int pageIndex = componentIndex / SingletonByKeyRelease.COMPONENT_PAGE_MAX_SIZE;
            ISingletonComponent[] array = _components.GetPage(pageIndex);
            if (array == null)
            {
                return null;
            }

            int indexInPage = componentIndex % SingletonByKeyRelease.COMPONENT_PAGE_MAX_SIZE;
            return array[indexInPage];
        }

        /// <summary>
        /// ISingletonByKeyLocale
        /// </summary>
        public bool SetSingletonComponent(int componentIndex, ISingletonComponent singletonComponent)
        {
            int pageIndex = componentIndex / SingletonByKeyRelease.COMPONENT_PAGE_MAX_SIZE;
            ISingletonComponent[] array = _components.GetPage(pageIndex);
            if (array == null)
            {
                array = _components.NewPage(pageIndex);
            }

            int indexInPage = componentIndex % SingletonByKeyRelease.COMPONENT_PAGE_MAX_SIZE;
            array[indexInPage] = singletonComponent;
            return true;
        }
    }
}