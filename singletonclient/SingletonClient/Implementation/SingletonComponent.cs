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
        string GetComponent();

        string GetString(string key);

        ISingletonAccessTask GetAccessTask();

        ISingletonAccessRemote GetAccessRemote();

        void GetDataFromLocal();

        int GetDataCount();

        IComponentMessages GetComponentMessages();

        void SetMessage(string key, string message);

        void SetIsLocal(bool isLocal);
    }

    public class SingletonComponent : ISingletonComponent, ISingletonAccessRemote
    {
        public const string SourceAlias = "latest";

        private readonly ISingletonRelease _release;
        private readonly ISingletonLocale _singletonLocale;
        private readonly IComponentMessages _componentCache;

        private readonly string _localeUse;
        private readonly string _component;
        private readonly bool _asSource;
        private readonly bool _fromRemote;
        private readonly bool _pseudo;
        private readonly bool _isSourceLocale;

        private readonly ISingletonAccessTask _task;

        private string _etag;
        private bool _localHandled = false;
        private bool _isLocal;

        public SingletonComponent(ISingletonRelease release,
            ISingletonLocale singletonLocale, string component, bool asSource, bool fromRemote)
        {
            _release = release;
            _singletonLocale = singletonLocale;
            string locale = _singletonLocale.GetOriginalLocale();
            _localeUse = locale;
            _component = component;
            _asSource = asSource;
            _fromRemote = fromRemote;
            _pseudo = _release.GetSingletonConfig().IsPseudo();

            ISingletonConfig config = _release.GetSingletonConfig();
            int interval = config.GetInterval();
            int tryWait = config.GetTryWait();

            _task = asSource ? null : new SingletonAccessRemoteTask(this, interval, tryWait);
            _isLocal = true;

            ISingletonUseLocale useLocale = _release.GetUseLocale(locale, asSource);
            _isSourceLocale = useLocale.IsSourceLocale();
            if (_isSourceLocale)
            {
                if (_pseudo)
                {
                    _pseudo = false;
                    _localeUse = SourceAlias;
                }
                if (_asSource && _fromRemote)
                {
                    _localeUse = SourceAlias;
                }
            }
            // Must be next
            useLocale.GetComponents().SetItem(component, this);

            _componentCache = useLocale.GetLocaleCache().GetComponentMessages(_component);
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
            string adr = _release.GetApi().GetComponentApi(_component, _localeUse, _pseudo);
            Hashtable headers = SingletonUtil.NewHashtable(false);
            if (_etag != null)
            {
                headers[SingletonConst.HeaderRequestEtag] = _etag;
            }
            JObject obj = _release.GetApi().HttpGetJson(adr, headers,
                _release.GetSingletonConfig().GetTryWait(), _release.GetLogger());
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

                SetIsLocal(false);
                // follow above
                foreach (var item in messages)
                {
                    SetMessage(item.Key.ToString(), item.Value.ToString());
                }

                _localHandled = true;
            }

            GetDataFromLocal();
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
        public string GetComponent()
        {
            return _component;
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
        public void SetMessage(string key, string message)
        {
            if (_isLocal && _pseudo && !_isSourceLocale)
            {
                message = SingletonUtil.AddPseudo(message);
            }
            if (!_isLocal || !_pseudo || _isSourceLocale)
            {
                _componentCache.SetString(key, message);
            }
        }

        /// <summary>
        /// ISingletonComponent
        /// </summary>
        public void SetIsLocal(bool isLocal)
        {
            _isLocal = isLocal;
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
                _release.GetUpdate().LoadLocalMessage(_singletonLocale, _component, _asSource, _fromRemote);
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
        public ISingletonAccessRemote GetAccessRemote()
        {
            return this;
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
