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
        bool IsInLocaleList(List<string> checkList);
        bool IsLocaleAtStringEnd(string text);
        bool Contains(string locale);
        object FindItem(Hashtable items, int start);
        void SetItems(Hashtable items, object item);
    }

    public class SingletonLocale : ISingletonLocale
    {
        private readonly List<string> localeList = new List<string>();

        public SingletonLocale(string locale)
        {
            localeList.Add(locale);
        }

        public List<string> GetNearLocaleList()
        {
            return localeList;
        }

        public bool AddNearLocale(string locale)
        {
            if (localeList.Contains(locale))
            {
                return false;
            }
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

            return this.IsInLocaleList(singletonLocale.GetNearLocaleList());
        }

        public bool IsInLocaleList(List<string> checkList)
        {
            if (checkList == null)
            {
                return false;
            }

            for (int i = 0; i < GetCount(); i++)
            {
                if (checkList.Contains(GetNearLocale(i)))
                {
                    return true;
                }
            }

            return false;
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
            return localeList.Contains(locale);
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