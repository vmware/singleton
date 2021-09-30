using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Cache
{
    /// <summary>
    /// The inherited interface for product information cache.
    /// </summary>
    public interface IProductInfoCache: IMemoryCache<string, IList<string>>
    {
    }
}
