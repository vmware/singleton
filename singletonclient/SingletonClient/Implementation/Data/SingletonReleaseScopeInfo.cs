/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;

namespace SingletonClient.Implementation.Release
{
    public interface ISingletonReleaseScopeInfo
    {
        List<string> GetLocales();
        List<string> GetComponents();
        int GetBundleCount();

        void MixLocales(ISingletonReleaseScopeInfo info1, ISingletonReleaseScopeInfo info2);
        void MixComponents(ISingletonReleaseScopeInfo info1, ISingletonReleaseScopeInfo info2);
    }

    public class SingletonReleaseScopeInfo : ISingletonReleaseScopeInfo
    {
        private List<string> _locales = new List<string>();
        private List<string> _components = new List<string>();

        public List<string> GetLocales()
        {
            return _locales;
        }

        public List<string> GetComponents()
        {
            return _components;
        }

        public int GetBundleCount()
        {
            return _locales.Count * _components.Count;
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

        public void MixLocales(ISingletonReleaseScopeInfo info1, ISingletonReleaseScopeInfo info2)
        {
            List<string> latest = new List<string>();
            Update(info1.GetLocales(), latest);
            Update(info2.GetLocales(), latest);

            if (IsDifferent(_locales, latest))
            {
                _locales = latest;
            }
        }

        public void MixComponents(ISingletonReleaseScopeInfo info1, ISingletonReleaseScopeInfo info2)
        {
            List<string> latest = new List<string>();
            Update(info1.GetComponents(), latest);
            Update(info2.GetComponents(), latest);

            if (IsDifferent(_components, latest))
            {
                _components = latest;
            }
        }
    }
}
