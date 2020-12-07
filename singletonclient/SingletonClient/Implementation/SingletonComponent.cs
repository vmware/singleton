/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using System.Collections;
using System.Text.RegularExpressions;

namespace SingletonClient.Implementation
{
    public interface ISingletonComponent
    {
        string GetString(string key);

        ISingletonAccessTask GetAccessTask();

        int GetDataCount();
    }

    public class SingletonComponent : ISingletonComponent, ISingletonAccessRemote
    {
        private readonly ISingletonRelease _releaseObject;
        private readonly IComponentMessages _componentCache;

        private readonly string _locale;
        private readonly string _component;

        private string _etag;

        private readonly ISingletonAccessTask _task;

        public SingletonComponent(ISingletonRelease releaseObject, string locale, string component)
        {
            _releaseObject = releaseObject;
            _locale = locale;
            _component = component;

            ISingletonConfig config = releaseObject.GetSingletonConfig();
            int interval = config.GetInterval();
            int tryDelay = config.GetTryDelay();

            _task = new SingletonAccessRemoteTask(this, interval, tryDelay);

            ICacheMessages productCache = releaseObject.GetReleaseMessages();
            ILocaleMessages langCache = productCache.GetLocaleMessages(locale);
            _componentCache = langCache.GetComponentMessages(component);
        }

        public ISingletonConfig GetSingletonConfig()
        {
            return _releaseObject.GetSingletonConfig();
        }

        public void GetDataFromRemote()
        {
            string adr = _releaseObject.GetApi().GetComponentApi(_component, _locale);
            Hashtable headers = SingletonUtil.NewHashtable(false);
            if (_etag != null)
            {
                headers[SingletonConst.HeaderRequestEtag] = _etag;
            }
            JObject obj = SingletonUtil.HttpGetJson(_releaseObject.GetAccessService(), adr, headers);
            if (SingletonUtil.CheckResponseValid(obj, headers))
            {
                _etag = (string)headers[SingletonConst.HeaderEtag];
                string cacheControl = (string)headers[SingletonConst.HeaderCacheControl];
                if (cacheControl != null)
                {
                    string[] parts = Regex.Split(cacheControl, "max\\-age[ ]*\\=[ ]*([0-9]*)[ ]*");
                    if (parts.Length == 3)
                    {
                        int maxAge = int.Parse(parts[1]);
                        if (maxAge > 0)
                        {
                            _task.SetInterval(maxAge);
                        }
                    }
                }

                JObject result = obj.Value<JObject>(SingletonConst.KeyResult);
                JObject data = result.Value<JObject>(SingletonConst.KeyData);
                JObject messages = data.Value<JObject>(SingletonConst.KeyMessages);

                foreach (var item in messages)
                {
                    _componentCache.SetString(item.Key.ToString(), item.Value.ToString());
                }
            }
        }

        public int GetDataCount()
        {
            return _componentCache.GetCount();
        }

        public ISingletonAccessTask GetAccessTask()
        {
            return _task;
        }

        public string GetString(string key)
        {
            string message = _componentCache.GetString(key);
            return message;
        }
    }
}


