using I18NClient.Net.Abstractions.Plugable.Cache;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Abstractions.Resource;
using I18NClient.Net.Caches;
using I18NClient.Net.Culture;
using I18NClient.Net.Loaders;
using I18NClient.Net.Loggers;
using I18NClient.Net.Options;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reflection;

namespace I18NClient.Net.Resources.Tests
{
    [TestClass()]
    public class ResourceServiceTests
    {
        private static IRemoteLoader s_singltonRemoteLoader;

        private static ILocalLoader s_localLoader;

        private static ILoadingStrategy s_loadingStrategy;

        private static ICultureService s_cultureService;

        private static ILogger s_logger;

        private static IComponentMessageCacheManager s_componentMessageCacheManager;

        private static IProductInfoCacheManager s_productInfoCacheManager;

        private static AbstractI18NClientOptions s_options;

        private static ILanguageCodeConvertor s_languageCodeConvertor;

        private static IResourceService s_resourceService;

        [TestInitialize]
        public void InitResourceService()
        {
            s_options = new I18NClientOptions()
            {
                ProductName = "AWConsole",
                Version = "1.0",
                BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                SupportedLanguages = new List<string>
                { "en-US", "cs", "da", "de", "es", "fr", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sv", "zh-CN", "zh-TW" }
            };

            s_languageCodeConvertor = new LanguageCodeConvertor();
            s_logger = new TraceLogger();
            s_cultureService = new CultureService(s_options, s_logger);
            s_componentMessageCacheManager = new ComponentMessageCacheManager();
            s_productInfoCacheManager = new ProductInfoCacheManager();
        }

        [TestMethod()]
        public void TryInitTranslationsByProductAsyncTest_RemoteStrategy_ShouldSuccess()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var success = s_resourceService.TryInitTranslationsByProductAsync().GetAwaiter().GetResult();
            int totalKeys = 0;
            int totalComponents = 0;

            s_componentMessageCacheManager.GetCacheNames().ToList().ForEach(cacheName =>
            {
                s_componentMessageCacheManager.TryGetCache(cacheName, out IMemoryCache<string, string> cache);
                totalKeys = totalKeys + cache?.CacheKeys.Count ?? 0;
                totalComponents++;
            });

            Trace.WriteLine($"Total components: {totalComponents}");
            Trace.WriteLine($"Total key/value pairs: {totalKeys}");
            Assert.AreEqual(true, success);
        }

        [TestMethod()]
        public void GetTranslationsByComponentAsyncTest_LocalStrategy_ShouldIgnoreCase()
        {
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new LocalLoadingStrategy(s_localLoader, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var component = s_resourceService.GetTranslationsByComponentAsync("Accounts", "zh-CN").GetAwaiter().GetResult();
            Assert.IsTrue(component.Messages.TryGetValue("AadAsIdpAttributeRequired", out _));
            Assert.IsTrue(component.Messages.TryGetValue("aadAsIdpattributeRequired", out _));
        }

        [TestMethod()]
        public void GetTranslationsByComponentAsyncTest_RemoteStrategy_ShouldIgnoreCase()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var component = s_resourceService.GetTranslationsByComponentAsync("Accounts", "zh-CN").GetAwaiter().GetResult();
            Assert.IsTrue(component.Messages.TryGetValue("AadAsIdpAttributeRequired", out _));
            Assert.IsTrue(component.Messages.TryGetValue("aadAsIdpattributeRequired", out _));
        }

        [TestMethod()]
        public void GetTranslationsByComponentAsyncTest_RemoteStrategyToGetAll_ShouldAllSuccess()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var supportedComponentList = s_resourceService.GetSupportedComponentListAsync().GetAwaiter().GetResult();
            var supportedLanguageList = s_resourceService.GetSupportedLanguageListAsync().GetAwaiter().GetResult();

            foreach (var comoponent in supportedComponentList)
            {
                foreach (var language in supportedLanguageList)
                {
                    Stopwatch stopWatch = new Stopwatch();
                    stopWatch.Start();
                    var loadedComponent = s_resourceService.GetTranslationsByComponentAsync(comoponent, language).GetAwaiter().GetResult();
                    stopWatch.Stop();
                    TimeSpan ts = stopWatch.Elapsed;
                    Trace.WriteLine($"{comoponent}/{language} messages resolved in {ts.TotalMilliseconds}");
                    Assert.IsTrue(loadedComponent.Messages.Any());
                }
            }
        }

        [TestMethod()]
        public void GetTranslationsByComponentAsyncTest_LocalStrategy_ShouldNotSupportUnregisteredLanguage()
        {
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new LocalLoadingStrategy(s_localLoader, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var component = s_resourceService.GetTranslationsByComponentAsync("Accounts", "tr").GetAwaiter().GetResult();
            Assert.IsFalse(component.Messages.TryGetValue("AadAsIdpAttributeRequired", out _));
        }

        [TestMethod()]
        public void GetTranslationsByComponentAsyncTest_LocalStrategy_ShouldSupportRegisteredLanguage()
        {
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new LocalLoadingStrategy(s_localLoader, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var component = s_resourceService.GetTranslationsByComponentAsync("Accounts", "de").GetAwaiter().GetResult();
            Assert.IsTrue(component.Messages.TryGetValue("AadAsIdpAttributeRequired", out _));
        }

        [TestMethod()]
        public void GetSupportedLanguageListAsyncTest_RemoteStrategy_ShouldLanguageCodeConverted()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var supportedLanguageList = s_resourceService.GetSupportedLanguageListAsync().GetAwaiter().GetResult();
            Assert.IsTrue(supportedLanguageList.Contains("zh-CN"));
            Assert.IsTrue(supportedLanguageList.Contains("zh-TW"));
            Assert.IsTrue(supportedLanguageList.Contains("en-US"));
        }

        [TestMethod()]
        public void GetTranslationByKeyAsyncTest_RemoteNormalKey_ShouldTranslated()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var translation = s_resourceService.GetTranslationByKeyAsync("Accounts", "AadAsIdpAttributeRequired", "zh-CN").GetAwaiter().GetResult();
            Assert.IsNotNull(translation);
        }

        [TestMethod()]
        public void GetTranslationByKeyAsyncTest_RemoteNormalKeyCaseDiff_ShouldTranslated()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var translation = s_resourceService.GetTranslationByKeyAsync("Accounts", "aadasIdpattributeRequired", "zh-CN").GetAwaiter().GetResult();
            Assert.IsNotNull(translation);
        }

        [TestMethod()]
        public void GetTranslationByKeyAsyncTest_LocalNormalKey_ShouldTranslated()
        {
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new LocalLoadingStrategy(s_localLoader, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var translation = s_resourceService.GetTranslationByKeyAsync("Accounts", "AadAsIdpAttributeRequired", "zh-CN").GetAwaiter().GetResult();
            Assert.IsNotNull(translation);
        }

        [TestMethod()]
        public void GetTranslationByKeyAsyncTest_LocalNormalKeyCaseDiff_ShouldTranslated()
        {
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new LocalLoadingStrategy(s_localLoader, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var translation = s_resourceService.GetTranslationByKeyAsync("Accounts", "aadasIdpattributeRequired", "zh-CN").GetAwaiter().GetResult();
            Assert.IsNotNull(translation);
        }

        [TestMethod()]
        public void GetTranslationByKeyAsyncTest_MissingKey_ShouldReturnNull()
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(s_options, s_languageCodeConvertor, s_logger);
            s_localLoader = new ResxLocalLoader(s_options, s_logger, Assembly.GetExecutingAssembly(), s_cultureService);
            s_loadingStrategy = new RemoteLoadingStrategy(s_localLoader, s_singltonRemoteLoader, s_cultureService, s_options);
            s_resourceService = new ResourceService(s_loadingStrategy, s_logger, s_componentMessageCacheManager, s_productInfoCacheManager, s_options);

            var translation = s_resourceService.GetTranslationByKeyAsync("Accounts", "MissingKey", "zh-CN").GetAwaiter().GetResult();
            Assert.IsNull(translation);
        }
    }
}