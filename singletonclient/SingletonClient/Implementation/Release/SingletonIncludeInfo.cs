/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support;
using System.Collections;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Release
{
    public class SingletonIncludeInfo
    {
        public List<string> Locales { get; set; }
        public Hashtable ExtLocales { get; set; }
        public List<string> Components { get; set; }
        public int BundleCount
        {
            get { return Locales.Count * Components.Count; }
        }

        public SingletonIncludeInfo()
        {
            Locales = new List<string>();
            Components = new List<string>();
        }

        private void Update(List<string> source, List<string> target)
        {
            foreach (string one in source)
            {
                if (!target.Contains(one))
                {
                    target.Add(one);
                }
            }
        }

        private bool IsDifferent(List<string> source, List<string> target)
        {
            if (source.Count != target.Count)
            {
                return true;
            }

            for (int i = 0; i < source.Count; i++)
            {
                if (source[i] != target[i])
                {
                    return true;
                }
            }

            return false;
        }

        public void MixLocales(SingletonIncludeInfo info1, SingletonIncludeInfo info2)
        {
            List<string> latest = new List<string>();
            Update(info1.Locales, latest);
            Update(info2.Locales, latest);

            if (IsDifferent(Locales, latest))
            {
                Locales = latest;
                Hashtable extLocales = SingletonUtil.NewHashtable(true);
                for (int i = 0; i < Locales.Count; i++)
                {
                    ISingletonLocale temp = SingletonLocaleUtil.GetSingletonLocale(Locales[i]);
                    for(int k=0; k<temp.GetCount(); k++)
                    {
                        if (extLocales[temp.GetNearLocale(k)] == null)
                        {
                            extLocales[temp.GetNearLocale(k)] = temp;
                        }
                    }
                }
            }
        }

        public void MixComponents(SingletonIncludeInfo info1, SingletonIncludeInfo info2)
        {
            List<string> latest = new List<string>();
            Update(info1.Components, latest);
            Update(info2.Components, latest);

            if (IsDifferent(Components, latest))
            {
                Components = latest;
            }
        }
    }
}
