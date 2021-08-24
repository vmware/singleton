/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Release;
using SingletonClient.Implementation.Support.Base;
using System.Collections.Generic;

namespace SingletonClient.Implementation.Support.ByKey
{
    public class SingletonByKeyCacheComponentMessages : SingletonCacheBaseComponentMessages
    {
        private readonly ISingletonByKey _byKey;
        private readonly ISingletonByKeyLocale _byKeyLocale;
        private readonly int _componentIndex;

        private readonly ISingletonComponent _componentObject;

        public SingletonByKeyCacheComponentMessages(ISingletonUseLocale useLocale, string component) :
            base(useLocale.GetRelease(), useLocale.GetLocale(), component, useLocale.IsAsSource())
        {
            _byKey = useLocale.GetRelease().GetSingletonByKey();
            _byKeyLocale = _byKey.GetLocaleItem(useLocale.GetLocale(), useLocale.IsAsSource());
            _componentIndex = _byKey.GetComponentIndex(component);

            _componentObject = useLocale.GetComponents().GetItem(component);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override int GetCount()
        {
            return _byKey.GetKeyCountInComponent(this._componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override ICollection<string> GetKeys()
        {
            return _byKey.GetKeysInComponent(this._componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override string GetString(string key)
        {
            return _byKey.GetString(key, _componentIndex, _byKeyLocale);
        }

        /// <summary>
        /// IComponentMessages
        /// </summary>
        public override void SetString(string key, string message)
        {
            _byKey.SetString(key, _componentObject, _componentIndex, _byKeyLocale, message);
        }
    }
}
