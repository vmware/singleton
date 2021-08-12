/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using System.Collections;
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
        object FindItem(Hashtable items, int start);
        void SetItems(Hashtable items, object item);
    }

    public class SingletonLocale : ISingletonLocale
    {
        private readonly List<string> localeList = new List<string>();
        private readonly Hashtable localeMap = SingletonUtil.NewHashtable(true);

        public SingletonLocale(string locale)
        {
            localeList.Add(locale);
            localeMap[locale] = true;
        }

        public List<string> GetNearLocaleList()
        {
            return localeList;
        }

        public bool AddNearLocale(string locale)
        {
            if (localeMap.ContainsKey(locale))
            {
                return false;
            }
            localeMap[locale] = true;
            localeList.Add(locale);
            return true;
        }

        public int GetCount()
        {
            return localeList.Count;
        }

        public string GetNearLocale(int index)
        {
            if (index < 0 || index >= GetCount())
            {
                return null;
            }

            return localeList[index];
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

            foreach (var one in localeList)
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
            return localeMap.ContainsKey(locale);
        }

        public object FindItem(Hashtable items, int start)
        {
            for (int i = start; i < GetCount(); i++)
            {
                string nearLocale = GetNearLocale(i);
                object item = items[nearLocale];
                if (item != null)
                {
                    return item;
                }
            }
            return null;
        }

        public void SetItems(Hashtable items, object item)
        {
            for (int i = 0; i < GetCount(); i++)
            {
                string nearLocale = GetNearLocale(i);
                items[nearLocale] = item;
            }
        }
    }
}