using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Cache;
using System.Collections.Generic;
using System.Linq;

namespace I18NClient.Net.Utilities
{
    internal static class CacheUtils
    {
        internal static bool TryLoadFromCache(
            Component component,
            string key,
            IComponentMessageCacheManager componentMessageCacheManager,
            out string translation)
        {
            translation = null;

            if (!componentMessageCacheManager.TryGetCache(component, out IComponentMessageCache cache))
                return false;

            if (!cache.TryGetValue(key, out translation))
                return false;

            return true;
        }

        internal static bool TryLoadFromCache(
            Component component,
            IComponentMessageCacheManager componentMessageCacheManager)
        {
            if (componentMessageCacheManager.TryGetCache(component, out IComponentMessageCache cache))
            {
                cache.CacheKeys.ToList().ForEach(key =>
                {
                    if (cache.TryGetValue(key, out string value))
                    {
                        component.Messages.Add(key, value);
                    }
                });

                return true;
            }

            return false;
        }

        internal static bool TryLoadFromCache(
            string productName,
            string version,
            string cacheKey,
            IProductInfoCacheManager productInfoCacheManager,
            List<string> items)
        {
            var cache = productInfoCacheManager.GetOrCreateCache(productName, version);

            if (cache.TryGetValue(cacheKey, out IList<string> value))
            {
                items.AddRange(value);
                return true;
            }

            return false;
        }

        internal static void CacheResult(
            Component component,
            IComponentMessageCacheManager componentMessageCacheManager)
        {
            if (component != null && component.Messages.Any())
            {
                var cache = componentMessageCacheManager.GetOrCreateCache(component);
                foreach (var item in component.Messages)
                    cache.TryAdd(item.Key, item.Value);
            }
        }

        internal static void CacheResult(
            string productName,
            string version,
            string cacheKey,
            IProductInfoCacheManager productInfoCacheManager,
            List<string> items)
        {
            if (items != null && items.Any())
            {
                var cache = productInfoCacheManager.GetOrCreateCache(productName, version);
                cache.TryAdd(cacheKey, items);
            }
        }
    }
}
