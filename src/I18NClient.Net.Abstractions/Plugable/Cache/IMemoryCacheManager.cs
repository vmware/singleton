using System;
using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <summary>
    /// A container for memory cache that maintain all aspects of their life cycle.
    /// </summary>
    /// <typeparam name="TName">Type of cache name.</typeparam>
    /// <typeparam name="TKey">Type of cache item key.</typeparam>
    /// <typeparam name="TValue">Type of cache item value.</typeparam>
    public interface IMemoryCacheManager<TName, TKey, TValue>
    {
        /// <summary>
        /// Gets existing cache with the given name or creates new one with the given name.
        /// </summary>
        /// <param name="name">The name of cache.</param>
        /// <param name="valueFactory">The function is used to create instances of IMemoryCache.</param>
        /// <returns>The instance of IMemoryCache.</returns>
        IMemoryCache<TKey, TValue> GetOrCreateCache(TName name, Func<TName, IMemoryCache<TKey, TValue>> valueFactory);

        /// <summary>
        /// Gets the IMemoryCache instance associated with the name if present.
        /// </summary>
        /// <param name="name">The name of cache.</param>
        /// <param name="cache">The instance of IMemoryCache.</param>
        /// <returns>if the cache name was found.</returns>
        bool TryGetCache(TName name, out IMemoryCache<TKey, TValue> cache);

        /// <summary>
        /// Attempts to add a new IMemoryCache instance with given name.
        /// </summary>
        /// <param name="name">The name of cache.</param>
        /// <param name="cache">The instance of IMemoryCache.</param>
        /// <returns>true if the cache was added successfully; otherwise, false.</returns>
        bool TryCreateCache(TName name, IMemoryCache<TKey, TValue> cache);

        /// <summary>
        /// Gets a collection of the cache names known by this manager.
        /// </summary>
        /// <returns>The names of all caches known by the cache manager.</returns>
        ICollection<TName> GetCacheNames();
    }
}
