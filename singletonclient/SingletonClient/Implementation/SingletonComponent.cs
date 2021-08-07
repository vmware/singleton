/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Release;
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

        IComponentMessages GetComponentMessages();
    }

    public class SingletonComponent : ISingletonComponent, ISingletonAccessRemote
    {
        private readonly ISingletonRelease _release;

        private readonly ISingletonLocale _singletonLocale;
        private readonly string _locale;
        private readonly string _component;
        private readonly IComponentMessages _componentCache;

        private readonly ISingletonAccessTask _task;

        private string _etag;
        private bool _localHandled = false;

        public SingletonComponent(ISingletonRelease release,
            ISingletonLocale singletonLocale, string component, bool asSource)
        {
            _release = release;
            _singletonLocale = singletonLocale;
            _locale = _singletonLocale.GetOriginalLocale();
            _component = component;

            ISingletonConfig config = _release.GetSingletonConfig();
            int interval = config.GetInterval();
            int tryDelay = config.GetTryDelay();

            _task = asSource ? null : new SingletonAccessRemoteTask(this, interval, tryDelay);

            SingletonUseLocale useLocale = _release.GetUseLocale(_locale, asSource);
            // Must be next
            useLocale.Components[component] = this;

            _componentCache = useLocale.LocaleCache.GetComponentMessages(_component);
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public ISingletonConfig GetSingletonConfig()
        {
            return _release.GetSingletonConfig();
        }

        /// <summary>
        /// ISingletonAccessRemote
        /// </summary>
        public void GetDataFromRemote()
        {
            string adr = _release.GetApi().GetComponentApi(_component, _locale);
            Hashtable headers = SingletonUtil.NewHashtable(false);
            if (_etag != null)
            {
                headers[SingletonConst.HeaderRequestEtag] = _etag;
            }
            JObject obj = SingletonUtil.HttpGetJson(_release.GetAccessService(), adr, headers,
                _release.GetSingletonConfig().GetTryDelay(), _release.GetLogger());
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

        /// <summary>
        /// ISingletonAccessRemote
        /// ISingletonComponent
        /// </summary>
        public int GetDataCount()
        {
            return _componentCache.GetCount();
        }

        /// <summary>
        /// ISingletonComponent
        /// </summary>
        public IComponentMessages GetComponentMessages()
        {
            return _componentCache;
        }

        /// <summary>
        /// ISingletonComponent
        /// </summary>
        public void GetDataFromLocal()
        {
            if (_localHandled)
            {
                return;
            }
            _localHandled = true;

            ISingletonConfig config = _release.GetSingletonConfig();
            if (config.IsOfflineSupported())
            {
                _release.GetUpdate().LoadOfflineMessage(_singletonLocale);
            }
        }

        /// <summary>
        /// ISingletonComponent
        /// </summary>
        public ISingletonAccessTask GetAccessTask()
        {
            return _task;
        }

        /// <summary>
        /// ISingletonComponent
        /// </summary>
        public string GetString(string key)
        {
            string message = _componentCache.GetString(key);
            return message;
        }
    }
}


