using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Cache;

namespace I18NClient.Net.Caches
{
    /// <inheritdoc/>
    public class ComponentMessageCacheManager : AbstractMemoryCacheManager<string>, IComponentMessageCacheManager
    {

        /// <inheritdoc/>
        public IComponentMessageCache GetOrCreateCache(Component component)
        {
            return (IComponentMessageCache)GetOrCreateCache(GetCacheName(component),
                (cacheName) => new ComponentMessageCache(cacheName)
                {
                    ProductName = component.ProductName,
                    Version = component.Version,
                    ComponentName = component.ComponentName,
                    LanguageCode = component.LanguageCode
                });
        }

        /// <inheritdoc/>
        public string GetCacheName(Component component)
        {
            return $"{component.ProductName}::{component.Version}::{component.ComponentName}::{component.LanguageCode}";
        }

        /// <inheritdoc/>
        public bool TryGetCache(Component component, out IComponentMessageCache cache)
        {
            var isSuccess = TryGetCache(GetCacheName(component), out IMemoryCache<string, string> interCache);
            cache = interCache as IComponentMessageCache;
            return isSuccess;
        }

        /// <inheritdoc/>
        public bool TryCreateCache(Component component, IComponentMessageCache cache)
        {
            return TryCreateCache(GetCacheName(component), cache);
        }
    }
}
