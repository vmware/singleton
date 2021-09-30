using I18NClient.Net.Abstractions.Plugable.Cache;
using System;

namespace I18NClient.Net.Caches
{
    /// <inheritdoc/>
    public class ComponentMessageCache : AbstractMemoryCache<string>, IComponentMessageCache
    {
        /// <inheritdoc/>
        public string ProductName { get; set; }

        /// <inheritdoc/>
        public string Version { get; set; }

        /// <inheritdoc/>
        public string ComponentName { get; set; }

        /// <inheritdoc/>
        public string LanguageCode { get; set; }

        /// <summary>
        /// Instantiate a component message cache.
        /// </summary>
        /// <param name="cacheName">The name of the cache.</param>
        public ComponentMessageCache(string cacheName)
        {
            _cacheName = cacheName ?? throw new ArgumentNullException(nameof(cacheName));
        }
    }
}
