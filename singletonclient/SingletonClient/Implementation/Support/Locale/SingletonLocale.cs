/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Data;
    using System.Collections.Generic;

    public interface ISingletonLocale
    {
        List<string> GetNearLocaleList();
        bool AddNearLocale(string locale);
        int GetCount();
        string GetNearLocale(int index);
        string GetOriginalLocale();
        bool Compare(ISingletonLocale singletonLocale);
        ISingletonLocale GetRelateLocale(List<string> checkList);
        bool IsLocaleAtStringEnd(string text);
        bool Contains(string locale);
        object FindItem(ISingletonTableBase table, int start);
        void SetItems(ISingletonTable<ILocaleMessages> items, ILocaleMessages item);
    }

    public class SingletonLocale : ISingletonLocale
    {
        private readonly List<string> _localeList = new List<string>();
        private readonly ISingletonTable<bool> _localeTable = new SingletonTable<bool>();

        public SingletonLocale(string locale)
        {
            _localeList.Add(locale);
            _localeTable.SetItem(locale, true);
        }

        public List<string> GetNearLocaleList()
        {
            return _localeList;
        }

        public bool AddNearLocale(string locale)
        {
            if (_localeTable.Contains(locale))
            {
                return false;
            }
            _localeTable.SetItem(locale, true);
            _localeList.Add(locale);
            return true;
        }

        public int GetCount()
        {
            return _localeList.Count;
        }

        public string GetNearLocale(int index)
        {
            if (index < 0 || index >= GetCount())
            {
                return null;
            }

            return _localeList[index];
        }

        public string GetOriginalLocale()
        {
            return GetNearLocale(0);
        }

        public bool Compare(ISingletonLocale singletonLocale)
        {
            if (singletonLocale == null)
            {
                return false;
            }

            foreach (var one in _localeList)
            {
                if (singletonLocale.Contains(one))
                {
                    return true;
                }
            }

            return false;
        }

        public ISingletonLocale GetRelateLocale(List<string> checkList)
        {
            if (checkList == null)
            {
                return null;
            }

            foreach (var one in checkList)
            {
                ISingletonLocale temp = SingletonLocaleUtil.GetSingletonLocale(one);
                if (Compare(temp))
                {
                    return temp;
                }
            }

            return null;
        }

        public bool IsLocaleAtStringEnd(string text)
        {
            for (int i = 0; i < GetCount(); i++)
            {
                if (text.EndsWith("_" + GetNearLocale(i)))
                {
                    return true;
                }
            }

            return false;
        }

        public bool Contains(string locale)
        {
            return _localeTable.Contains(locale);
        }

        public object FindItem(ISingletonTableBase table, int start)
        {
            for (int i = start; i < GetCount(); i++)
            {
                string nearLocale = GetNearLocale(i);
                object item = table.GetObject(nearLocale);
                if (item != null)
                {
                    return item;
                }
            }
            return null;
        }

        public void SetItems(ISingletonTable<ILocaleMessages> items, ILocaleMessages item)
        {
            foreach (string nearLocale in GetNearLocaleList())
            {
                items.SetItem(nearLocale, item);
            }
        }
    }
}
