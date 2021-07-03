/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support.Base;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyCacheComponentMessages : SingletonCacheBaseComponentMessages
    {
        private readonly ISingletonByKeyRelease _byKeyRelease;
        private readonly ISingletonByKeyLocale _byKeyLocale;
        private readonly int _componentIndex;

        private readonly ISingletonComponent _componentObject;

        public SingletonByKeyCacheComponentMessages(
            ISingletonRelease release, string locale, string component, bool asSource) :
            base(release, locale, component, asSource)
        {
            _byKeyRelease = release.GetSingletonByKeyRelease();
            _componentIndex = _byKeyRelease.GetComponentIndex(this._component);
            _byKeyLocale = _byKeyRelease.GetLocaleItem(this._locale, this._asSource);
            _componentObject = release.GetComponentObject(this, _locale, _component, _asSource);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override int GetCount()
        {
            return _byKeyRelease.GetKeyCountInComponent(this._componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override ICollection<string> GetKeys()
        {
            return _byKeyRelease.GetKeysInComponent(this._componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override string GetString(string key)
        {
            return _byKeyRelease.GetString(key, _componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override void SetString(string key, string message)
        {
            _byKeyRelease.SetString(key, _componentObject, _componentIndex, _byKeyLocale, message);
        }
    }
}