using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Cache;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Abstractions.Resource;
using I18NClient.Net.Constants;
using I18NClient.Net.Loaders;
using I18NClient.Net.Utilities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace I18NClient.Net.Resources
{
    /// <inheritdoc/>
    public class ResourceService : IResourceService
    {
        private readonly ILoadingStrategy _loadingStrategy;

        private readonly ILogger _logger;

        private readonly IComponentMessageCacheManager _componentMessageCacheManager;

        private readonly IProductInfoCacheManager _productInfoCacheManager;

        private readonly AbstractI18NClientOptions _options;

        /// <summary>
        /// Instantiate a resource service.
        /// </summary>
        /// <param name="loadingStrategy">The logic is used to load i18n resources.</param>
        /// <param name="logger">A Logger object is used to log messages for the resource service.</param>
        /// <param name="componentMessageCacheManager">The instance of IComponentMessageCacheManager.</param>
        /// <param name="productInfoCacheManager">The instance of IProductInfoCacheManager.</param>
        /// <param name="options">The i18n client options.</param>
        public ResourceService(
            ILoadingStrategy loadingStrategy,
            ILogger logger,
            IComponentMessageCacheManager componentMessageCacheManager,
            IProductInfoCacheManager productInfoCacheManager,
            AbstractI18NClientOptions options)
        {
            _loadingStrategy = loadingStrategy ?? throw new ArgumentNullException(nameof(loadingStrategy));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
            _componentMessageCacheManager = componentMessageCacheManager ?? throw new ArgumentNullException(nameof(componentMessageCacheManager));
            _productInfoCacheManager = productInfoCacheManager ?? throw new ArgumentNullException(nameof(productInfoCacheManager));
            _options = options ?? throw new ArgumentNullException(nameof(options));
        }

        /// <inheritdoc/>
        public async Task<IList<string>> GetSupportedComponentListAsync()
        {
            IList<string> supportedComponentList = new List<string>();

            if (CacheUtils.TryLoadFromCache(_options.ProductName, _options.Version, I18NClientConstants.ProductInfoCacheKey.SupportedComponents,
                _productInfoCacheManager, (List<string>)supportedComponentList)) return supportedComponentList;

            _logger.LogDebug($"Cache miss for supported component list: {_options.ProductName}/{_options.Version}");

            supportedComponentList = await _loadingStrategy
                .LoadSupportedComponentListAsync(_options.ProductName, _options.Version)
                .ConfigureAwait(false) ?? supportedComponentList;

            CacheUtils.CacheResult(_options.ProductName, _options.Version, I18NClientConstants.ProductInfoCacheKey.SupportedComponents,
                _productInfoCacheManager, (List<string>)supportedComponentList);

            return supportedComponentList;
        }

        /// <inheritdoc/>
        public async Task<IList<string>> GetSupportedLanguageListAsync()
        {
            IList<string> supportedLanguageList = new List<string>();

            if (CacheUtils.TryLoadFromCache(_options.ProductName, _options.Version, I18NClientConstants.ProductInfoCacheKey.SupportedLanguages,
                _productInfoCacheManager, (List<string>)supportedLanguageList)) return supportedLanguageList;

            _logger.LogDebug($"Cache miss for supported language list: {_options.ProductName}/{_options.Version}");

            supportedLanguageList = await _loadingStrategy
                .LoadSupportedLanguageListAsync(_options.ProductName, _options.Version)
                .ConfigureAwait(false) ?? supportedLanguageList;

            CacheUtils.CacheResult(_options.ProductName, _options.Version, I18NClientConstants.ProductInfoCacheKey.SupportedLanguages,
                _productInfoCacheManager, (List<string>)supportedLanguageList);

            return supportedLanguageList;
        }

        /// <inheritdoc/>
        public async Task<string> GetTranslationByKeyAsync(string componentName, string key, string languageCode)
        {
            var component = GetComponent(componentName, languageCode);

            if (CacheUtils.TryLoadFromCache(component, key, _componentMessageCacheManager, out string translation)) return translation;

            _logger.LogDebug($"Cache miss for key message: {componentName}/{key}/{languageCode}");

            component = await _loadingStrategy
                .LoadTranslationsByComponentAsync(component, LoadingContext.Current)
                .ConfigureAwait(false) ?? component;

            CacheUtils.CacheResult(component, _componentMessageCacheManager);
            component.Messages?.TryGetValue(key, out translation);

            return translation;
        }

        /// <inheritdoc/>
        public async Task<Component> GetTranslationsByComponentAsync(string componentName, string languageCode)
        {
            var component = GetComponent(componentName, languageCode);

            if (CacheUtils.TryLoadFromCache(component, _componentMessageCacheManager)) return component;

            _logger.LogDebug($"Cache miss for component messages: {componentName}/{languageCode}");

            component = await _loadingStrategy
                .LoadTranslationsByComponentAsync(component, LoadingContext.Current)
                .ConfigureAwait(false) ?? component;

            CacheUtils.CacheResult(component, _componentMessageCacheManager);

            return component;
        }

        /// <inheritdoc/>
        public async Task<bool> TryInitTranslationsByComponentAsync(string componentName, string languageCode)
        {
            try
            {
                var component = GetComponent(componentName, languageCode);
                var loadedComponent = await _loadingStrategy
                    .LoadTranslationsByComponentAsync(component, LoadingContext.Current)
                    .ConfigureAwait(false);

                loadedComponent = loadedComponent ?? component;
                CacheUtils.CacheResult(loadedComponent, _componentMessageCacheManager);
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Initialize messages for '{0}/{1}/{2}/{3}' failed.",
                    _options.ProductName, _options.Version, componentName, languageCode);
                return false;
            }
        }

        /// <inheritdoc/>
        public async Task<bool> TryInitTranslationsByLanguageAsync(string languageCode)
        {
            try
            {
                var componentNameList = await _loadingStrategy
                    .LoadSupportedComponentListAsync(_options.ProductName, _options.Version)
                    .ConfigureAwait(false);

                var context = LoadingContext.Current;
                context.IsParallel = true;

                IList<Component> componentList = new List<Component>();
                componentNameList.ToList().ForEach(name =>
                     componentList.Add(new Component()
                     {
                         ProductName = _options.ProductName,
                         Version = _options.Version,
                         ComponentName = name,
                         LanguageCode = languageCode
                     }));

                componentList = await _loadingStrategy.LoadTranslationsByComponentsAsync(componentList, context).ConfigureAwait(false);
                componentList?.ToList().ForEach(component => CacheUtils.CacheResult(component, _componentMessageCacheManager));
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Initialize messages for '{0}/{1}/{2}' failed.", _options.ProductName, _options.Version, languageCode);
                return false;
            }
        }

        /// <inheritdoc/>
        public async Task<bool> TryInitTranslationsByProductAsync()
        {
            try
            {
                var componentNameList = await _loadingStrategy
                    .LoadSupportedComponentListAsync(_options.ProductName, _options.Version)
                    .ConfigureAwait(false);

                var languageList = await _loadingStrategy
                    .LoadSupportedLanguageListAsync(_options.ProductName, _options.Version)
                    .ConfigureAwait(false);

                var context = LoadingContext.Current;
                context.IsParallel = true;

                // Make sure the default language is always included.
                if (languageList != null && !languageList.Contains(_options.DefaultLanguage))
                    languageList.Add(_options.DefaultLanguage);

                languageList = languageList ?? new List<string>() { _options.DefaultLanguage };

                foreach (string name in componentNameList)
                {
                    IList<Component> componentList = new List<Component>();
                    languageList?.ToList().ForEach(language =>
                    {
                        componentList.Add(new Component()
                        {
                            ProductName = _options.ProductName,
                            Version = _options.Version,
                            ComponentName = name,
                            LanguageCode = language
                        });
                    });

                    componentList = await _loadingStrategy
                        .LoadTranslationsByComponentsAsync(componentList, context)
                        .ConfigureAwait(false);

                    componentList?.ToList().ForEach(component => CacheUtils.CacheResult(component, _componentMessageCacheManager));
                }
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Initialize messages for '{0}/{1}' failed.", _options.ProductName, _options.Version);
                return false;
            }
        }

        private Component GetComponent(string componentName, string languageCode)
        {
            return new Component()
            {
                ProductName = _options.ProductName,
                Version = _options.Version,
                ComponentName = componentName,
                LanguageCode = languageCode
            };
        }
    }
}
