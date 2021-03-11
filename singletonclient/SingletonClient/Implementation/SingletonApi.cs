/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using SingletonClient.Implementation.Support;

namespace SingletonClient.Implementation
{
    public interface ISingletonApi
    {
        IConfig GetConfig();
        string GetLocaleListApi();
        string GetComponentListApi();
        string GetComponentApi(string component, string locale);
    }

    public class SingletonApi : ISingletonApi
    {
        private const string VipPathHead = "/i18n/api/v2/translation/products/{0}/versions/{1}/";
        private const string VipParameter = "pseudo=false&machineTranslation=false&checkTranslationStatus=false";
        private const string VipGetComponent = "locales/{0}/components/{1}?";
        private const string VipSendSource = "locales/en-US/components/{0}/keys/{1}?collectSource={2}&";

        private readonly string _product;
        private readonly string _version;
        private readonly string _urlService;

        private readonly ISingletonRelease _releaseObject;

        public SingletonApi(ISingletonRelease releaseObject)
        {
            _releaseObject = releaseObject;
            ISingletonConfig config = _releaseObject.GetSingletonConfig();

            _urlService = config.GetServiceUrl();
            _product = config.GetProduct();
            _version = config.GetVersion();
        }

        public IConfig GetConfig()
        {
            return _releaseObject.GetRelease().GetConfig();
        }

        public string GetComponentApi(string component, string locale)
        {
            List<string> localeList = _releaseObject.GetRelease().GetMessages().GetLocaleList();
            string localeInUse = null;
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

                if (localeInUse == null)
                {
                    localeInUse = locale;
                }
            }

            string head = string.Format(VipPathHead, _product, _version);
            string path = string.Format(VipGetComponent, localeInUse, component);
            string api = string.Format("{0}{1}{2}{3}", _urlService, head, path, VipParameter);
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
    }
}

