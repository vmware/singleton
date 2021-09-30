using Microsoft.VisualStudio.TestTools.UnitTesting;
using I18NClient.Net.Abstractions.Domains;
using System.Collections.Generic;
using System.Reflection;
using System.Threading.Tasks;
using I18NClient.Net.Options;
using I18NClient.Net.Loggers;
using I18NClient.Net.Culture;
using I18NClient.Net.Abstractions.Plugable.Localization;

namespace I18NClient.Net.Loaders.Tests
{
    [TestClass()]
    public class LocalResourceLoaderTests
    {
        private static ILocalLoader s_localResourceLoader;

        [ClassInitialize]
        public static void InitLocalResourceLoader(TestContext context)
        {
            var options = new I18NClientOptions()
            {
                OfflineResourceRelativePath = "Data.Resources.{0}.LabelValues",
                SupportedLanguages = new List<string>
                {
                    "en-US",
                    "zh-CN"
                }
            };

            s_localResourceLoader = new ResxLocalLoader(
                options,
                new TraceLogger(),
                Assembly.GetExecutingAssembly(),
                new CultureService(options, new TraceLogger()));
        }

        [TestMethod()]
        public void LocalResourceLoaderTest()
        {
            Assert.IsNotNull(s_localResourceLoader);
        }

        [TestMethod()]
        public async Task LoadSupportedComponentListTest()
        {
            var componentNameList = await s_localResourceLoader.LoadSupportedComponentListAsync("AWConsole", "1.0");
            Assert.AreEqual<int>(50, componentNameList.Count);
        }

        [TestMethod()]
        public async Task LoadSupportedLanguageListTest()
        {
            var languageList = await s_localResourceLoader.LoadSupportedLanguageListAsync("AWConsole", "1.0");
            Assert.AreEqual<int>(2, languageList.Count);
        }

        [TestMethod()]
        public async Task LoadTranslationsByComponentTest()
        {
            var singletonComponent = await s_localResourceLoader.LoadTranslationsByComponentAsync(new Component()
            {
                ProductName = "AWConsole",
                ComponentName = "Accounts",
                Version = "1.0",
                LanguageCode = "zh-CN"

            }, LoadingContext.Current);
            Assert.AreEqual<int>(301, singletonComponent.Messages.Count);
        }

        [TestMethod()]
        public async Task LoadTranslationsByComponentSourceLanguageTest()
        {
            var singletonComponent = await s_localResourceLoader.LoadTranslationsByComponentAsync(new Component()
            {
                ProductName = "AWConsole",
                ComponentName = "Accounts",
                Version = "1.0",
                LanguageCode = "en-US"
            }, LoadingContext.Current);
            Assert.AreEqual<int>(321, singletonComponent.Messages.Count);
        }

        [TestMethod()]
        public async Task LoadTranslationsByComponentUnknownLanguageTest()
        {
            var context = LoadingContext.Current;
            context.IsParallel = true;

            var singletonComponent = await s_localResourceLoader.LoadTranslationsByComponentAsync(new Component()
            {
                ProductName = "AWConsole",
                ComponentName = "Accounts",
                Version = "1.0",
                LanguageCode = "ar"
            }, LoadingContext.Current);
            Assert.AreEqual<int>(0, singletonComponent.Messages.Count);
        }
    }
}