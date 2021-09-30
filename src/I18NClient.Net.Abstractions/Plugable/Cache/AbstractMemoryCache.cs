using System;
using System.Collections.Concurrent;
using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <inheritdoc/>
    public abstract class AbstractMemoryCache<T> : IMemoryCache<string, T>
    {
        private readonly ConcurrentDictionary<string, T> _internalCache =
            new ConcurrentDictionary<string, T>(StringComparer.InvariantCultureIgnoreCase);

        /// <inheritdoc/>
        public ICollection<string> CacheKeys => _internalCache.Keys;

        /// <inheritdoc/>
        protected string _cacheName;

        /// <inheritdoc/>
        public void Clear() => _internalCache.Clear();

        /// <inheritdoc/>
        public string GetName() => _cacheName;

        /// <inheritdoc/>
        public bool TryAdd(string key, T value) => _internalCache.TryAdd(key, value);

        /// <inheritdoc/>
        public bool TryGetValue(string key, out T value) => _internalCache.TryGetValue(key, out value);

        /// <inheritdoc/>
        public bool TryRemove(string key, out T value) => _internalCache.TryRemove(key, out value);
    }
}
