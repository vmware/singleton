using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <summary>
    /// Represents a local in-memory cache.
    /// </summary>
    /// <typeparam name="TKey"></typeparam>
    /// <typeparam name="TValue"></typeparam>
    public interface IMemoryCache<TKey, TValue>
    {
        /// <summary>
        /// Gets a collection containing the keys in the cache.
        /// </summary>
        ICollection<TKey> CacheKeys { get; }

        /// <summary>
        /// Gets the name of the cache.
        /// </summary>
        string GetName();

        /// <summary>
        /// Removes all keys and values from the cache.
        /// </summary>
        void Clear();

        /// <summary>
        /// Removes a key and value from the cache.
        /// </summary>
        /// <param name="key">The key of the element to remove and return.</param>
        /// <param name="value">When this method returns, contains the object removed from the cache.</param>
        /// <returns>true if the object was removed successfully; otherwise, false.</returns>
        bool TryRemove(TKey key, out TValue value);

        /// <summary>
        /// Attempts to get the value associated with the specified key from the cache.
        /// </summary>
        /// <param name="key">The key of the value to get.</param>
        /// <param name="value">When this method returns, contains the value from the cache.</param>
        /// <returns>true if the key was found in the cache.</returns>
        bool TryGetValue(TKey key, out TValue value);

        /// <summary>
        /// Attempts to add the specified key and value to the cache.
        /// </summary>
        /// <param name="key">The key of the element to add.</param>
        /// <param name="value">The value of the element to add.</param>
        /// <returns>true if the key was added, otherwise return false.</returns>
        bool TryAdd(TKey key, TValue value);
    }
}
