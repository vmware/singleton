/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Release
{
    public class SingletonAccessObject
    {
        public SingletonUseLocale UseLocale { get; }

        protected ISource _source;

        public SingletonAccessObject(SingletonUseLocale useLocale, ISource source)
        {
            UseLocale = useLocale;
            _source = source;
        }

        public string Key
        {
            get { return _source.GetKey(); }
        }

        public string Component
        {
            get { return _source.GetComponent(); }
        }

        public string SourceMessage
        {
            get { return _source.GetSource(); }
        }

        public bool IsSourceLocale()
        {
            return (UseLocale.IsSourceLocale && _source.GetSource() != null);
        }
    }
}
