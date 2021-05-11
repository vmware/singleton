/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using System.Collections;
    using SingletonClient.Implementation.Support.Base;

    public class SingletonCacheComponentMessages : SingletonComponentBaseMessages
    {
        private readonly Hashtable _messages = SingletonUtil.NewHashtable(true);

        public SingletonCacheComponentMessages(
            ISingletonRelease release, string locale, string component, bool asSource):
            base(release, locale, component, asSource)
        {
        }

        public override void SetString(string key, string message)
        {
            _messages[key] = message;
        }

        public override int GetCount()
        {
            return _messages.Keys.Count;
        }

        public override ICollection GetKeys()
        {
            return _messages.Keys;
        }

        public override string GetString(string key)
        {
            if (key == null)
            {
                return null;
            }
            string message = (string)_messages[key];
            return message;
        }
    }
}
