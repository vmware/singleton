/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support.Base;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonCacheByKeyComponentMessages : SingletonComponentBaseMessages
    {
        private readonly ISingletonByKeyRelease _byKeyRelease;
        private readonly ISingletonByKeyLocale _byKeyLocale;
        private readonly int _componentIndex;

        public SingletonCacheByKeyComponentMessages(
            ISingletonRelease release, string locale, string component, bool asSource) :
            base(release, locale, component, asSource)
        {
            _byKeyRelease = release.GetSingletonByKeyRelease();
            _componentIndex = _byKeyRelease.GetComponentIndex(this._component);
            _byKeyLocale = _byKeyRelease.GetLocaleItem(this._locale, this._asSource);
        }

        public override int GetCount()
        {
            return _byKeyRelease.GetKeyCountInComponent(this._componentIndex, _byKeyLocale);
        }

        public override ICollection<string> GetKeys()
        {
            return _byKeyRelease.GetKeysInComponent(this._componentIndex, _byKeyLocale);
        }

        public override string GetString(string key)
        {
            return _byKeyRelease.GetString(key, _componentIndex, _byKeyLocale);
        }

        public override void SetString(string key, string message)
        {
            _byKeyRelease.SetString(key, _componentIndex, _byKeyLocale, message);
        }
    }
}