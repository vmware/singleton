/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using SingletonClient.Implementation.Helpers;
using SingletonClient.Implementation.Support;

namespace SingletonClient.Implementation.Release
{
    public class SingletonReleaseBase
    {
        protected ISingletonClientManager _client;
        protected ISingletonRelease _self;
        protected IReleaseMessages _releaseMessages;

        protected ILog _logger;
        protected LogType _logLevel;

        protected ISingletonConfig _config;

        protected ISingletonApi _api;
        protected IAccessService _accessService;
        protected ISingletonAccessTask _task;

        protected readonly SingletonIncludeInfo _infoLocal = new SingletonIncludeInfo();
        protected readonly SingletonIncludeInfo _infoRemote = new SingletonIncludeInfo();
        protected readonly SingletonIncludeInfo _infoMix = new SingletonIncludeInfo();

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonConfig GetSingletonConfig()
        {
            return _config;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public ISingletonApi GetApi()
        {
            return _api;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        /// <returns></returns>
        public IAccessService GetAccessService()
        {
            return _accessService;
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        /// <returns></returns>
        public bool IsInScope(ISingletonLocale singletonLocale, string component)
        {
            if (string.IsNullOrEmpty(component))
            {
                return false;
            }

            bool inLocaleScope = singletonLocale.IsInLocaleList(_infoMix.Locales);
            bool inComponentScope = _infoMix.Components.Contains(component);
            return (inLocaleScope && inComponentScope);
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        /// <returns></returns>
        public void AddLocalScope(string locale, string component)
        {
            if (!_infoLocal.Locales.Contains(locale))
            {
                _infoLocal.Locales.Add(locale);
                UpdateLocaleInfo();
            }
            if (!_infoLocal.Components.Contains(component))
            {
                _infoLocal.Components.Add(component);
                UpdateComponentInfo();
            }
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public IConfig GetConfig()
        {
            return (_config == null) ? null : _config.GetConfig();
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public List<string> GetLocaleList()
        {
            return _infoMix.Locales;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public List<string> GetComponentList()
        {
            return _infoMix.Components;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public bool SetCurrentLocale(string locale)
        {
            CultureHelper.SetCurrentCulture(locale);
            return true;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetCurrentLocale()
        {
            return CultureHelper.GetCurrentCulture();
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public List<string> GetLocaleSupported(string locale)
        {
            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(locale);
            if (singletonLocale == null)
            {
                return SingletonUtil.GetEmptyStringList();
            }
            return singletonLocale.GetNearLocaleList();
        }

        protected void InitForBase(ISingletonRelease singletonRelease, IConfig config, ISingletonAccessRemote accessRemote)
        {
            _self = singletonRelease;
            _releaseMessages = _self.GetRelease().GetMessages();

            _config = new SingletonConfigWrapper(config);
            _client = SingletonClientManager.GetInstance();

            string loggerName = _config.GetLoggerName();
            _logger = _client.GetLogger(loggerName);

            string logType = _config.GetLogLevel();
            _logLevel = (LogType)Enum.Parse(typeof(LogType), logType);

            _api = new SingletonApi(_self);

            string accessServiceType = _config.GetAccessServiceType();
            _accessService = _client.GetAccessService(accessServiceType);

            int interval = _config.GetInterval();
            int tryDelay = _config.GetTryDelay();
            _task = new SingletonAccessRemoteTask(accessRemote, interval, tryDelay);
        }

        protected void UpdateLocaleInfo()
        {
            _infoMix.MixLocales(_infoLocal, _infoRemote);
        }

        protected void UpdateComponentInfo()
        {
            _infoMix.MixComponents(_infoLocal, _infoRemote);
        }
    }
}
