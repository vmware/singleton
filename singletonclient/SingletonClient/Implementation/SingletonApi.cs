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
        string GetSendSourceApi(string component, string key);
    }

    public class SingletonApi : ISingletonApi
    {
        private const string VipPathHead = "/i18n/api/v2/translation/products/{0}/versions/{1}/";
        private const string VipParameter = "pseudo={0}&machineTranslation={1}&checkTranslationStatus=false";
        private const string VipGetComponent = "locales/{0}/components/{1}?";
        private const string VipSendSource = "locales/en-US/components/{0}/keys/{1}?collectSource={2}&";

        private string _product;
        private string _version;
        private string _urlService;

        private string _pseudo;
        private string _machine;
        private string _collect;

        private ISingletonRelease _releaseObject;

        public SingletonApi(ISingletonRelease releaseObject)
        {
            _releaseObject = releaseObject;
            IConfig config = _releaseObject.GetRelease().GetConfig();

            _urlService = config.GetStringValue(ConfigConst.KeyService);
            _product = config.GetStringValue(ConfigConst.KeyProduct);
            _version = config.GetStringValue(ConfigConst.KeyVersion);

            _pseudo = config.GetBoolValue(ConfigConst.KeyPseudo).ToString().ToLower();
            _machine = config.GetBoolValue(ConfigConst.KeyMachine).ToString().ToLower();
            _collect = config.GetBoolValue(ConfigConst.KeyCollect).ToString().ToLower();
        }

        public IConfig GetConfig()
        {
            return _releaseObject.GetRelease().GetConfig();
        }

        public string GetComponentApi(string component, string locale)
        {
            string head = string.Format(VipPathHead, _product, _version);
            string path = string.Format(VipGetComponent, locale, component);
            string para = string.Format(VipParameter, _pseudo, _machine);
            string api = string.Format("{0}{1}{2}{3}", _urlService, head, path, para);
            return api;
        }

        public string GetSendSourceApi(string component, string key)
        {
            string head = string.Format(VipPathHead, _product, _version);
            string path = string.Format(VipSendSource, component, key, _collect);
            string para = string.Format(VipParameter, _pseudo, _machine);
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
    }
}

