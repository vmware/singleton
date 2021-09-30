using I18NClient.Net.Abstractions.Resource;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Cache;

namespace I18NClient.Net.Abstractions
{
    /// <summary>
    /// Provides a interface to access the public service interfaces.
    /// </summary>
    public interface II18NClient
    {
        /// <summary>
        /// Gets the resource service.
        /// </summary>
        IResourceService ResourceService { get; }

        /// <summary>
        /// Gets the culture service.
        /// </summary>
        ICultureService CultureService { get; }

        /// <summary>
        /// Gets the component message cache manager.
        /// </summary>
        IComponentMessageCacheManager ComponentMessageCacheManager { get; }

        /// <summary>
        /// Gets the product info cache manager.
        /// </summary>
        IProductInfoCacheManager ProductInfoCacheManager { get; }
    }
}
