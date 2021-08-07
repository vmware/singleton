/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;

namespace SingletonClient.Implementation.Release
{
    public class SingletonIncludeInfo
    {
        public List<string> Locales { get; set; }
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

            for (int i=0; i<source.Count; i++)
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
