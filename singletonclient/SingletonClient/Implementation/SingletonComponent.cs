/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Support;
using System.Collections;
using System.Text.RegularExpressions;
using static SingletonClient.Implementation.SingletonUtil;

namespace SingletonClient.Implementation
{
    public interface ISingletonComponent
    {
        string GetString(string key);

        ISingletonAccessTask GetAccessTask();

        void GetDataFromLocal();

        int GetDataCount();
    }

    public class SingletonComponent : ISingletonComponent, ISingletonAccessRemote
    {
        private readonly ISingletonRelease _releaseObject;
        private readonly IComponentMessages _componentCache;

        private readonly ISingletonLocale _singletonLocale;
        private readonly string _locale;
        private readonly string _component;

        private string _etag;
        private bool _localHandled = false;

        private readonly ISingletonAccessTask _task;

        public SingletonComponent(ISingletonRelease releaseObject, ISingletonLocale singletonLocale, string component)
        {
            _releaseObject = releaseObject;
            _singletonLocale = singletonLocale;
            _locale = _singletonLocale.GetOriginalLocale();
            _component = component;

            ISingletonConfig config = releaseObject.GetSingletonConfig();
            int interval = config.GetInterval();
            int tryDelay = config.GetTryDelay();

            _task = new SingletonAccessRemoteTask(this, interval, tryDelay);

            ICacheMessages productCache = releaseObject.GetReleaseMessages();
            ILocaleMessages langCache = productCache.GetLocaleMessages(_locale);
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
            ResponseStatus status = SingletonUtil.CheckResponseValid(obj, headers);
            if (status == ResponseStatus.Messages)
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
            else if (status == ResponseStatus.NetFail || status == ResponseStatus.NoMessages)
            {
                GetDataFromLocal();
            }
        }

        public void GetDataFromLocal()
        {
            if (_localHandled)
            {
                return;
            }
            _localHandled = true;

            ISingletonConfig config = _releaseObject.GetSingletonConfig();
            if (config.IsOfflineSupported())
            {
                _releaseObject.GetUpdate().LoadOfflineBundle(_singletonLocale);
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


