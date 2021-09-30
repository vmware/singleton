using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <summary>
    /// The inherited interface for product information cache manager.
    /// </summary>
    public interface IProductInfoCacheManager : IMemoryCacheManager<string, string, IList<string>>
    {
        /// <summary>
        /// Gets existing cache with the given product name and version
        /// or creates new one with the given product name and version.
        /// </summary>
        /// <param name="productName">The name of product.</param>
        /// <param name="version">The version of the product release.</param>
        /// <returns>The instance of IProductInfoCache.</returns>
        IProductInfoCache GetOrCreateCache(string productName, string version);

        /// <summary>
        /// Gets cache name for the product and version.
        /// </summary>
        /// <param name="productName">The name of product.</param>
        /// <param name="version">The version of the product release.</param>
        /// <returns>The cache name for the product name and version.</returns>
        string GetCacheName(string productName, string version);
    }
}
