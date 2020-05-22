/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using System.Collections;

namespace SingletonClient.Implementation.Support
{
    public sealed class SingletonParserBundle : IResourceParser
    {
        private Hashtable kvTable = new Hashtable();

        public Hashtable Parse(string text)
        {
            JObject data = SingletonUtil.ConvertToDict(text);
            if (data != null)
            {
                data = data.Value<JObject>(SingletonConst.KeyMessages);
            }
            if (data != null)
            {
                foreach (var item in data)
                {
                    kvTable[item.Key] = item.Value.ToString();
                }
            }
            return kvTable;
        }
    }
}
