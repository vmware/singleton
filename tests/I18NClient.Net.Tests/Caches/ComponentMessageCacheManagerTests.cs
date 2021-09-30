using Microsoft.VisualStudio.TestTools.UnitTesting;
using I18NClient.Net.Abstractions.Domains;
using System.Linq;
using I18NClient.Net.Abstractions.Plugable.Cache;

namespace I18NClient.Net.Caches.Tests
{
    [TestClass()]
    public class ComponentMessageCacheManagerTests
    {
        private static IComponentMessageCacheManager _componentMessageCacheManager;

        private static Component _component;

        [TestInitialize]
        public void InitComponentMessageCacheManager()
        {
            _componentMessageCacheManager = new ComponentMessageCacheManager();
            _component = new Component()
            {
                ProductName = "app",
                Version = "1.0.0",
                ComponentName = "test",
                LanguageCode = "zh-CN"
            };
        }

        [TestMethod()]
        public void GetOrCreateCache_FirstAccess_ShouldNotNull()
        {
            var cache = _componentMessageCacheManager.GetOrCreateCache(_component);
            Assert.IsNotNull(cache);
        }

        [TestMethod()]
        public void GetOrCreateCache_SecondAccess_ShouldGetSameCache()
        {
            var cache1 = _componentMessageCacheManager.GetOrCreateCache(_component);
            var cache2 = _componentMessageCacheManager.GetOrCreateCache(_component);

            Assert.IsNotNull(cache1);
            Assert.IsNotNull(cache2);
            Assert.AreEqual(cache1, cache2);
        }

        [TestMethod()]
        public void GetCacheName_NormalComponent_ShouldNameAsExpected()
        {
            var cacheName = _componentMessageCacheManager.GetCacheName(_component);
            Assert.AreEqual("app::1.0.0::test::zh-CN", cacheName);
        }

        [TestMethod()]
        public void GetCacheNames_OneComponent_ShouldCorrectInfo()
        {
            _ = _componentMessageCacheManager.GetOrCreateCache(_component);
            var names = _componentMessageCacheManager.GetCacheNames();

            Assert.AreEqual(1, names.Count);
            Assert.AreEqual("app::1.0.0::test::zh-CN", names.ToArray()[0]);
        }
    }
}