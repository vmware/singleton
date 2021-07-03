/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
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

        protected readonly List<string> _localeList = new List<string>();
        protected readonly List<string> _componentList = new List<string>();

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

        protected bool CheckBundleRequest(ISingletonLocale singletonLocale, string component)
        {
            if (string.IsNullOrEmpty(component))
            {
                return false;
            }

            if (_config.IsOnlineSupported() && !_config.IsOfflineSupported())
            {
                bool inLocaleScope = singletonLocale.IsInLocaleList(_localeList);
                bool inComponentScope = _componentList.Contains(component);
                if (!inLocaleScope || !inComponentScope)
                {
                    return false;
                }
            }
            return true;
        }
    }
}
