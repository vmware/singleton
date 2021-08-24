/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Data;
using SingletonClient.Implementation.Release;
using SingletonClient.Implementation.Support;
using System.Collections.Generic;
using System.Reflection;

namespace SingletonClient.Implementation
{
    public interface ISingletonReleaseManager
    {
        IConfig LoadConfig(string text);
        IConfig LoadConfig(string resourceBaseName, Assembly assembly, string configResourceName, IConfig outsideConfig = null);
        IConfig GetConfig(string product, string version);
        IRelease GetRelease(IConfig config);
        ICacheManager GetCacheManager(string cacheManagerName);
        ICacheComponentManager GetCacheComponentManager(string cacheComponentManagerName);
        ILog GetLogger(string loggerName);
        IResourceParser GetResourceParser(string parserName);
        IAccessService GetAccessService(string accessServiceName);
    }

    public class SingletonReleaseManager : ISingletonReleaseManager, IExtension
    {
        private static SingletonReleaseManager _instance = new SingletonReleaseManager();

        public static SingletonReleaseManager GetInstance()
        {
            return _instance;
        }

        // key: (string) product name  
        // value: (ISingletonTable<ISingletonRelease>) versions -> 
        //     key: (string) version
        //     value: (ISingletonRelease) release object
        private readonly ISingletonTable<ISingletonTable<ISingletonRelease>> _products =
            new SingletonTable<ISingletonTable<ISingletonRelease>>();

        // key: (string) type name of cache manager
        // value: (ICacheManager) cache manager object
        private readonly ISingletonTable<ICacheManager> _cacheManagers =
            new SingletonTable<ICacheManager>();

        // key: (string) type name of component cache manager
        // value: (ICacheComponentManager) component cache manager object
        private readonly ISingletonTable<ICacheComponentManager> _cacheComponentManagers =
            new SingletonTable<ICacheComponentManager>();

        // key: (string) type name of logger
        // value: (ILog) logger object
        private readonly ISingletonTable<ILog> _loggers =
            new SingletonTable<ILog>();

        // key: (string) type name of parser
        // value: (IResourceParser) parser object
        private readonly ISingletonTable<IResourceParser> _parsers =
            new SingletonTable<IResourceParser>();

        // key: (string) type name of accessing service
        // value: (IAccessService) accessing service object
        private readonly ISingletonTable<IAccessService> _accessServices =
            new SingletonTable<IAccessService>();

        private SingletonReleaseManager()
        {
            ICacheManager cacheManager = new SingletonCacheManager();
            RegisterCacheManager(cacheManager, ConfigConst.DefaultType);
            RegisterCacheManager(cacheManager, ConfigConst.CacheByKey);

            ILog logger = new SingletonLogger();
            RegisterLogger(logger, ConfigConst.DefaultType);

            IResourceParser parser = new SingletonParserProperties();
            RegisterResourceParser(parser, ConfigConst.FormatProperties);

            IResourceParser bundleParser = new SingletonParserBundle();
            RegisterResourceParser(bundleParser, ConfigConst.FormatBundle);

            IAccessService accessService = new SingletonAccessService();
            RegisterAccessService(accessService, ConfigConst.DefaultType);

            LoadFallbackDefine();
        }

        private void LoadFallbackDefine()
        {
            string resName = "Implementation.SingletonRes.Singleton";
            Assembly assembly = typeof(SingletonReleaseManager).Assembly;

            byte[] bytes = SingletonUtil.ReadResource(resName, assembly, "fallback");
            string configText = SingletonUtil.ConvertToText(bytes);

            SingletonLocaleUtil.SetFallbackConfig(configText);
        }

        private ISingletonTable<ISingletonRelease> GetProductVersions(string product, bool add)
        {
            ISingletonTable<ISingletonRelease> versions = _products.GetItem(product);
            if (versions == null && add)
            {
                _products.SetItem(product, new SingletonTable<ISingletonRelease>());
                versions = _products.GetItem(product);
            }
            return versions;
        }

        private ISingletonRelease GetRelease(
            ISingletonTable<ISingletonRelease> versions, string version, bool add)
        {
            if(versions == null || version == null)
            {
                return null;
            }
            ISingletonRelease releaseObject = versions.GetItem(version);
            if (releaseObject == null && add)
            {
                releaseObject = new SingletonRelease();
                versions.SetItem(version, releaseObject);
            }
            return releaseObject;
        }

        private ISingletonRelease GetRelease(string product, string version)
        {
            ISingletonTable<ISingletonRelease> versions = GetProductVersions(product, false);
            if (versions == null)
            {
                return null;
            }
            ISingletonRelease rel = GetRelease(versions, version, false);
            return rel;
        }

        public IConfig LoadConfig(string text)
        {
            SingletonConfig config = new SingletonConfig(null);
            config.SetConfigData(text);
            return config;
        }

        public IConfig LoadConfig(
            string resourceBaseName, Assembly assembly, string configResourceName, IConfig outsideConfig = null)
        {
            SingletonConfig config = new SingletonConfig(assembly);
            string text = config.ReadResourceText(resourceBaseName, configResourceName);
            config.SetConfigData(text);

            if (config.GetRoot() != null && outsideConfig != null)
            {
                IConfigItem outsideRoot = outsideConfig.GetRoot();
                if (outsideRoot != null)
                {
                    List<string> keyList = outsideRoot.GetMapKeyList();
                    foreach(string key in keyList)
                    {
                        config.GetRoot().SetMapItem(key, outsideRoot.GetMapItem(key).Clone());
                    }
                }
            }

            return config;
        }

        public IConfig GetConfig(string product, string version)
        {
            ISingletonRelease releaseObject = GetRelease(product, version);
            return (releaseObject == null) ? null : releaseObject.GetRelease().GetConfig();
        }

        public IRelease GetRelease(IConfig config)
        {
            if (config == null)
            {
                return null;
            }

            ISingletonConfig singletonConfig = new SingletonConfigWrapper(null, config);
            string product = singletonConfig.GetProduct();
            string version = singletonConfig.GetVersion();

            ISingletonTable<ISingletonRelease> versions = GetProductVersions(product, true);
            ISingletonRelease releaseObject = GetRelease(versions, version, true);
            if (releaseObject.GetRelease().GetConfig() == null)
            {
                releaseObject.SetConfig(config);
            }
            return releaseObject.GetRelease();
        }

        public void RegisterLogger(ILog logger, string loggerName)
        {
            _loggers.SetItem(loggerName, logger);
        }

        private object GetItemFromTable(ISingletonTableBase table, string name)
        {
            object found = string.IsNullOrEmpty(name) ? null : table.GetObject(name);
            if (found == null)
            {
                found = table.GetObject(ConfigConst.DefaultType);
            }
            return found;
        }

        public ICacheManager GetCacheManager(string cacheManagerName)
        {
            return (ICacheManager)GetItemFromTable(_cacheManagers, cacheManagerName);
        }

        public ICacheComponentManager GetCacheComponentManager(string cacheComponentManagerName)
        {
            return (ICacheComponentManager)GetItemFromTable(_cacheComponentManagers, cacheComponentManagerName);
        }

        public ILog GetLogger(string loggerName)
        {
            return (ILog)GetItemFromTable(_loggers, loggerName);
        }

        public IResourceParser GetResourceParser(string parserName)
        {
            return (IResourceParser)GetItemFromTable(_parsers, parserName);
        }

        public IAccessService GetAccessService(string accessServiceName)
        {
            return (IAccessService)GetItemFromTable(_accessServices, accessServiceName);
        }

        public void RegisterCacheManager(ICacheManager cacheManager, string cacheManagerName)
        {
            _cacheManagers.SetItem(cacheManagerName, cacheManager);
        }

        public void RegisterCacheComponentManager(ICacheComponentManager cacheComponentManager,
            string cacheComponentManagerName)
        {
            _cacheComponentManagers.SetItem(cacheComponentManagerName, cacheComponentManager);
        }

        public void RegisterAccessService(IAccessService accessService, string accessrName)
        {
            _accessServices.SetItem(accessrName, accessService);
        }

        public void RegisterResourceParser(IResourceParser parser, string parserName)
        {
            _parsers.SetItem(parserName, parser);
        }
    }
}
