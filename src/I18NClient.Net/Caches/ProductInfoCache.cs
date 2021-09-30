using I18NClient.Net.Abstractions.Plugable.Cache;
using System;
using System.Collections.Generic;

namespace I18NClient.Net.Caches
{
    /// <inheritdoc/>
    public class ProductInfoCache : AbstractMemoryCache<IList<string>>, IProductInfoCache
    {
        /// <inheritdoc/>
        public string ProductName { get; set; }

        /// <inheritdoc/>
        public string Version { get; set; }

        /// <inheritdoc/>
        public ProductInfoCache(string cacheName)
        {
            _cacheName = cacheName ?? throw new ArgumentNullException(nameof(cacheName));
        }
    }
}
