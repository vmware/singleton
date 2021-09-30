
using I18NClient.Net.Abstractions;
using I18NClient.Net.Abstractions.Resource;
using System;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Cache;

namespace I18NClient.Net
{
    /// <inheritdoc/>
#pragma warning disable S101 // Types should be named in PascalCase
    public class I18NClient : II18NClient
#pragma warning restore S101 // Types should be named in PascalCase
    {
        private readonly IResourceService _resourceService;

        private readonly ICultureService _cultureService;

        private readonly IProductInfoCacheManager _productInfoCacheManager;

        private readonly IComponentMessageCacheManager _componentMessageCacheManager;

        /// <summary>
        /// Initialize a I18NClient instance.
        /// </summary>
        /// <param name="resourceService">Provides the interfaces to access i18n resource.</param>
        /// <param name="cultureService">Gets or sets culture at runtime.</param>
        /// <param name="productInfoCacheManager">Manages the product info cache instances.</param>
        /// <param name="componentMessageCacheManager">Manages the component message cache instances.</param>
        public I18NClient(
            IResourceService resourceService,
            ICultureService cultureService,
            IProductInfoCacheManager productInfoCacheManager,
            IComponentMessageCacheManager componentMessageCacheManager)
        {
            _resourceService = resourceService ?? throw new ArgumentNullException(nameof(resourceService));
            _cultureService = cultureService ?? throw new ArgumentNullException(nameof(cultureService));
            _productInfoCacheManager = productInfoCacheManager ?? throw new ArgumentNullException(nameof(productInfoCacheManager));
            _componentMessageCacheManager = componentMessageCacheManager ?? throw new ArgumentNullException(nameof(componentMessageCacheManager));
        }

        /// <inheritdoc/>
        public IResourceService ResourceService => _resourceService;

        /// <inheritdoc/>
        public ICultureService CultureService => _cultureService;

        /// <inheritdoc/>
        public IComponentMessageCacheManager ComponentMessageCacheManager => _componentMessageCacheManager;

        /// <inheritdoc/>
        public IProductInfoCacheManager ProductInfoCacheManager => _productInfoCacheManager;
    }
}
