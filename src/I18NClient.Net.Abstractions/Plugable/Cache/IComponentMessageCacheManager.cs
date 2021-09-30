using I18NClient.Net.Abstractions.Domains;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <summary>
    /// The inherited interface for component message cache manager.
    /// </summary>
    public interface IComponentMessageCacheManager : IMemoryCacheManager<string, string, string>
    {
        /// <summary>
        /// Gets existing cache with the given component or creates new one with the given component.
        /// </summary>
        /// <param name="component">The instance of Component class.</param>
        /// <returns>The instance of IComponentMessageCache.</returns>
        IComponentMessageCache GetOrCreateCache(Component component);

        /// <summary>
        /// Gets cache name for the component.
        /// </summary>
        /// <param name="component">The instance of Component class.</param>
        /// <returns>The cache name of the component.</returns>
        string GetCacheName(Component component);

        /// <summary>
        /// Gets the IComponentMessageCache instance associated with the name if present.
        /// </summary>
        /// <param name="component">The instance of Component class.</param>
        /// <param name="cache">The instance of IComponentMessageCache.</param>
        /// <returns>true if the cache was added successfully; otherwise, false.</returns>
        bool TryGetCache(Component component, out IComponentMessageCache cache);

        /// <summary>
        /// Attempts to add a new IMemoryCache instance with given name.
        /// </summary>
        /// <param name="component">The instance of Component class.</param>
        /// <param name="cache">The instance of IComponentMessageCache.</param>
        /// <returns>true if the cache was added successfully; otherwise, false.</returns>
        bool TryCreateCache(Component component, IComponentMessageCache cache);
    }
}
