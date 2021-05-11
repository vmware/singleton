﻿/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections;
using SingletonClient.Implementation.Support.Base;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonCacheByKeyComponentMessages : SingletonComponentBaseMessages
    {
        private ISingletonByKeyRelease _byKeyRelease;
        private ISingletonByKeyLocale _byKeyLocale;
        private int _componentIndex;

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

        public override ICollection GetKeys()
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