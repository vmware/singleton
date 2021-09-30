using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <inheritdoc/>
    public class AbstractMemoryCacheManager<T> : IMemoryCacheManager<string, string, T>
    {
        private readonly ConcurrentDictionary<string, IMemoryCache<string, T>> s_cacheMap =
            new ConcurrentDictionary<string, IMemoryCache<string, T>>(StringComparer.InvariantCultureIgnoreCase);

        /// <inheritdoc/>
        public IMemoryCache<string, T> GetOrCreateCache(string name, Func<string, IMemoryCache<string, T>> valueFactory)
        {
            // Although it's possible that multiple lazy instances will be created,
            // the actual T value will only be created when access Lazy<T>.Value property.
            // ExecutionAndPublication ensures only one thread can initialize the value.
            return s_cacheMap.GetOrAdd(name, (cacheName) => new Lazy<IMemoryCache<string, T>>(() => valueFactory(cacheName),
                LazyThreadSafetyMode.ExecutionAndPublication).Value);
        }

        /// <inheritdoc/>
        public ICollection<string> GetCacheNames() => s_cacheMap.Keys;

        /// <inheritdoc/>
        public bool TryGetCache(string name, out IMemoryCache<string, T> cache) => s_cacheMap.TryGetValue(name, out cache);

        /// <inheritdoc/>
        public bool TryCreateCache(string name, IMemoryCache<string, T> cache) => s_cacheMap.TryAdd(name, cache);
    }
}
