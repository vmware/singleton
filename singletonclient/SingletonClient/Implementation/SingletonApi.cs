/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

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

        private string _product;
        private string _version;
        private string _urlService;

        private ISingletonRelease _releaseObject;

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
            string head = string.Format(VipPathHead, _product, _version);
            string path = string.Format(VipGetComponent, locale, component);
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

