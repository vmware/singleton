using Microsoft.VisualStudio.TestTools.UnitTesting;
using I18NClient.Net.Abstractions.Domains;
using System.Collections.Generic;
using System.Threading.Tasks;
using I18NClient.Net.Utilities;
using I18NClient.Net.Culture;
using I18NClient.Net.Options;
using I18NClient.Net.Loggers;
using System.Linq;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Option;

namespace I18NClient.Net.Loaders.Tests
{
    [TestClass()]
    public class SingletonRemoteLoaderTests
    {
        private static IRemoteLoader s_singltonRemoteLoader;

        private static readonly AbstractI18NClientOptions s_abstractI18NClientOptions = new I18NClientOptions()
        {
            BackendServiceUrl = "https://g11n-vip-dev-1.eng.vmware.com:8090/",
            SupportedLanguages = new List<string>
            { "en-US", "cs", "da", "de", "es", "fr", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sv", "tr", "zh-CN", "zh-TW" }
        };

        [ClassInitialize]
        public static void InitSingletonRemoteLoader(TestContext context)
        {
            s_singltonRemoteLoader = new SingletonRemoteLoader(
                s_abstractI18NClientOptions,
                new LanguageCodeConvertor(),
                new TraceLogger());
        }

        [TestMethod()]
        public void IsRemoteAvailableTest()
        {
            Assert.IsTrue(s_singltonRemoteLoader.IsServiceAvailable());
        }

        [TestMethod()]
        public async Task LoadSupportedComponentListTest()
        {
            var componentList = await s_singltonRemoteLoader.LoadSupportedComponentListAsync("AWConsole", "1.0");
            Assert.IsNotNull(componentList);
        }

        [TestMethod()]
        public async Task LoadSupportedLanguageListTest()
        {
            var languageList = await s_singltonRemoteLoader.LoadSupportedLanguageListAsync("AWConsole", "1.0");
            Assert.AreEqual(s_abstractI18NClientOptions.SupportedLanguages.Count, languageList.Count);
        }

        [TestMethod()]
        public async Task LoadTranslationsByComponentTest()
        {
            var component = await s_singltonRemoteLoader.LoadTranslationsByComponentAsync(new Component()
            {
                ComponentName = "Accounts",
                ProductName = "AWConsole",
                Version = "1.0",
                LanguageCode = "zh-CN"
            }, LoadingContext.Current);
            Assert.IsNotNull(component);
        }
    }
}