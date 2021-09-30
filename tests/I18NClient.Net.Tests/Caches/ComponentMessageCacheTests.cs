using Microsoft.VisualStudio.TestTools.UnitTesting;
using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Cache;

namespace I18NClient.Net.Caches.Tests
{
    [TestClass()]
    public class ComponentMessageCacheTests
    {

        private static IComponentMessageCacheManager _componentMessageCacheManager;

        [ClassInitialize]
        public static void InitComponentMessageCacheManager(TestContext context)
        {
            _componentMessageCacheManager = new ComponentMessageCacheManager();

        }

        [TestMethod()]
        public void ComponentMessageCache_NormalInstantiation_ShouldCachePropertiesCorrect()
        {
            var component = new Component()
            {
                ProductName = "app",
                Version = "1.0.0",
                ComponentName = "test",
                LanguageCode = "zh-CN"
            };

            var cache = (ComponentMessageCache)_componentMessageCacheManager.GetOrCreateCache(component);

            Assert.AreEqual("app", cache.ProductName);
            Assert.AreEqual("1.0.0", cache.Version);
            Assert.AreEqual("test", cache.ComponentName);
            Assert.AreEqual("zh-CN", cache.LanguageCode);
            Assert.AreEqual("app::1.0.0::test::zh-CN", cache.GetName());
        }

        [TestMethod]
        public void Clear_AddThree_ShouldEmptyAfterClear()
        {
            var component = new Component()
            {
                ProductName = "app1",
                Version = "1.0.0",
                ComponentName = "test",
                LanguageCode = "zh-CN"
            };

            var cache = (ComponentMessageCache)_componentMessageCacheManager.GetOrCreateCache(component);

            cache.TryAdd("key1", "value1");
            cache.TryAdd("key2", "value2");
            cache.TryAdd("key3", "value3");

            cache.Clear();
            Assert.IsFalse(cache.TryGetValue("key1", out _));
            Assert.IsFalse(cache.TryGetValue("key2", out _));
            Assert.IsFalse(cache.TryGetValue("key3", out _));
        }

        [TestMethod]
        public void TryAdd_AddThree_ShouldTrueWhenGet()
        {
            var component = new Component()
            {
                ProductName = "app2",
                Version = "1.0.0",
                ComponentName = "test",
                LanguageCode = "zh-CN"
            };

            var cache = (ComponentMessageCache)_componentMessageCacheManager.GetOrCreateCache(component);

            cache.TryAdd("key1", "value1");
            cache.TryAdd("key2", "value2");
            cache.TryAdd("key3", "value3");

            Assert.IsTrue(cache.TryGetValue("key1", out _));
            Assert.IsTrue(cache.TryGetValue("key2", out _));
            Assert.IsTrue(cache.TryGetValue("key3", out _));
        }
    }
}