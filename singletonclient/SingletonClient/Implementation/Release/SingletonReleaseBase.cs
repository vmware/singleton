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
        protected readonly ISingletonReleaseManager _manager = SingletonReleaseManager.GetInstance();

        protected readonly ISingletonReleaseScopeInfo _infoLocal = new SingletonReleaseScopeInfo();
        protected readonly ISingletonReleaseScopeInfo _infoRemote = new SingletonReleaseScopeInfo();
        protected readonly ISingletonReleaseScopeInfo _infoMix = new SingletonReleaseScopeInfo();

        protected ISingletonConfig _config;
        protected ISingletonApi _api;
        protected ISingletonAccessTask _task;

        protected IReleaseMessages _releaseMessages;
        protected ISingletonRelease _self;

        protected ILog _logger;
        protected LogType _logLevel;

        protected IAccessService _accessService;

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        public IRelease GetRelease()
        {
            return (IRelease)this;
        }

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
        public ILog GetLogger()
        {
            return (ILog)this;
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
        public bool IsInScope(ISingletonLocale singletonLocale, string component, out ISingletonLocale relateLocale)
        {
            relateLocale = singletonLocale;
            if (string.IsNullOrEmpty(component))
            {
                return false;
            }

            relateLocale = singletonLocale.GetRelateLocale(_infoMix.GetLocales());
            bool inComponentScope = _infoMix.GetComponents().Contains(component);
            return (relateLocale != null && inComponentScope);
        }

        /// <summary>
        /// ISingletonRelease
        /// </summary>
        /// <returns></returns>
        public void AddLocalScope(List<string> locales, List<string> components)
        {
            foreach(string locale in locales)
            {
                if (!_infoLocal.GetLocales().Contains(locale))
                {
                    _infoLocal.GetLocales().Add(locale);
                }
            }
            foreach (string component in components)
            {
                if (!_infoLocal.GetComponents().Contains(component))
                {
                    _infoLocal.GetComponents().Add(component);
                }
            }
            UpdateLocaleInfo();
            UpdateComponentInfo();
        }

        /// <summary>
        /// ILog
        /// </summary>
        public void Log(LogType logType, string text)
        {
            if (_logger != null && logType >= _logLevel)
            {
                _logger.Log(logType, text);
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
            return _infoMix.GetLocales();
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public List<string> GetComponentList()
        {
            return _infoMix.GetComponents();
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

        protected void InitForBase(IConfig config, ISingletonAccessRemote accessRemote)
        {
            _self = (ISingletonRelease)this;
            _releaseMessages = _self.GetRelease().GetMessages();

            _config = new SingletonConfigWrapper(_self, config);

            string loggerName = _config.GetLoggerName();
            _logger = _manager.GetLogger(loggerName);

            string logType = _config.GetLogLevel();
            _logLevel = (LogType)Enum.Parse(typeof(LogType), logType);

            _api = new SingletonApi(_self);

            string accessServiceType = _config.GetAccessServiceType();
            _accessService = _manager.GetAccessService(accessServiceType);

            int interval = _config.GetInterval();
            int tryWait = _config.GetTryWait();
            _task = new SingletonAccessRemoteTask(accessRemote, interval, tryWait);
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
