using I18NClient.Net.Abstractions.Plugable.Cache;
using System.Collections.Generic;

namespace I18NClient.Net.Caches
{
    /// <inheritdoc/>
    public class ProductInfoCacheManager : AbstractMemoryCacheManager<IList<string>>, IProductInfoCacheManager
    {
        /// <inheritdoc/>
        public IProductInfoCache GetOrCreateCache(string productName, string version)
        {
            return (IProductInfoCache)GetOrCreateCache(GetCacheName(productName, version),
                (cacheName) => new ProductInfoCache(cacheName)
                {
                    ProductName = productName,
                    Version = version,
                });
        }

        /// <inheritdoc/>
        public string GetCacheName(string productName, string version)
        {
            return $"{productName}::{version}";
        }
    }
}
