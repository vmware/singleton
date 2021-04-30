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
        private readonly ISingletonRelease _release;
        private string _locale;
        private ISingletonLocale _singletonLocale;
        private bool _asSource;
        private bool _isSourceLocale;

        // Data table
        private SingletonByKeyTable<ISingletonComponent> _components;
        private SingletonByKeyTable<string> _messages;

        public SingletonByKeyLocale(ISingletonRelease release, string locale, bool asSource)
        {
            _release = release;
            this._locale = locale;
            _singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            this._asSource = asSource;
            string sourceLocale = _release.GetSingletonConfig().GetSourceLocale();
            this._isSourceLocale = _singletonLocale.Contains(sourceLocale);

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
            ISingletonByKeyRelease byKeyRelease = _release.GetSingletonByKeyRelease();

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
                    SingletonByKeyItem item = byKeyRelease.GetKeyItem(i, k);
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