/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Release;
using SingletonClient.Implementation.Support;

namespace SingletonClient.Implementation
{
    public interface ISingletonApi
    {
        string GetLocaleListApi();
        string GetComponentListApi();
        string GetComponentApi(string component, string locale, bool pseudo);
        JObject HttpGetJson(string url, Hashtable headers, int timeout, ILog logger);
    }

    public class SingletonApi : ISingletonApi
    {
        private const string VipPathHead = "/i18n/api/v2/translation/products/{0}/versions/{1}/";
        private const string VipGetComponent = "locales/{0}/components/{1}?";
        private const string VipParameter = "pseudo={0}&machineTranslation=false&checkTranslationStatus=false";

        private readonly string _product;
        private readonly string _version;
        private readonly string _urlService;

        private readonly ISingletonRelease _releaseObject;

        private string _status;
        private int _tick;

        public SingletonApi(ISingletonRelease releaseObject)
        {
            _releaseObject = releaseObject;
            ISingletonConfig config = _releaseObject.GetSingletonConfig();

            _urlService = config.GetServiceUrl();
            _product = config.GetProduct();
            _version = config.GetVersion();
        }

        public string GetComponentApi(string component, string locale, bool pseudo)
        {
            List<string> localeList = _releaseObject.GetRelease().GetMessages().GetLocaleList();
            string localeInUse = null;
            if (!SingletonComponent.SourceAlias.Equals(locale))
            {
                for (int i = 0; i < localeList.Count; i++)
                {
                    if (String.Equals(locale, localeList[i], StringComparison.CurrentCultureIgnoreCase))
                    {
                        localeInUse = locale;
                        break;
                    }
                }
                if (localeInUse == null)
                {
                    ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
                    for (int i = 0; i < localeList.Count; i++)
                    {
                        if (singletonLocale.Contains(localeList[i]))
                        {
                            localeInUse = localeList[i];
                            break;
                        }
                    }
                }
            }

            if (localeInUse == null)
            {
                localeInUse = locale;
            }

            string head = string.Format(VipPathHead, _product, _version);
            string path = string.Format(VipGetComponent, localeInUse, component);
            string pseudoText = pseudo ? "true" : "false";
            string para = string.Format(VipParameter, pseudoText);
            string api = string.Format("{0}{1}{2}{3}", _urlService, head, path, para);
            return api;
        }

        public string GetLocaleListApi()
        {
            string head = string.Format(VipPathHead, _product, _version);
            string api = string.Format("{0}{1}localelist", _urlService, head);
            return api;
        }

        public string GetComponentListApi()
        {
            string head = string.Format(VipPathHead, _product, _version);
            string api = string.Format("{0}{1}componentlist", _urlService, head);
            return api;
        }

        public JObject HttpGetJson(string url, Hashtable headers, int timeout, ILog logger)
        {
            if (!string.IsNullOrEmpty(_status) && System.Environment.TickCount - _tick < 60 * 1000)
            {
                return new JObject();
            }

            string status;
            JObject obj = SingletonUtil.HttpGetJson(_releaseObject.GetAccessService(), url, headers,
                timeout, out status, logger);
            if (string.IsNullOrEmpty(status))
            {
                _tick = 0;
                _status = null;
            }
            else
            {
                if (status.Equals(ConfigConst.NetFailConnect) || status.Equals(ConfigConst.NetTimeout))
                {
                    _status = status;
                    _tick = System.Environment.TickCount;
                }
            }
            return obj;
        }
    }
}
