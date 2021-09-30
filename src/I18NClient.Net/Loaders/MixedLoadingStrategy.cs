using I18NClient.Net.Abstractions.Domains;
using System.Collections.Generic;
using System.Threading.Tasks;
using System;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Extensions;
using I18NClient.Net.Abstractions.Plugable.Loader;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Constants;
using I18NClient.Net.Abstractions.Plugable.Logger;

namespace I18NClient.Net.Loaders
{
    /// <summary>
    /// The logic is used to load the i18n resource from both local and remote.
    /// </summary>
    public class MixedLoadingStrategy : ILoadingStrategy
    {
        private readonly ILocalLoader _localLoader;

        private readonly IRemoteLoader _remoteLoader;

        private readonly ICultureService _cultureService;

        private readonly AbstractI18NClientOptions _options;

        private readonly ILogger _logger;

        /// <summary>
        /// Instantiate a local loading strategy.
        /// </summary>
        /// <param name="localLoader">The local loader which implement ILocalLoader.</param>
        /// <param name="remoteLoader">The remote loader which implement IRemoteLoader.</param>
        /// <param name="cultureService">The culture service instance.</param>
        /// <param name="options">The i18n client related configurations.</param>
        /// <param name="logger">The logger instance to perform logging.</param>
        public MixedLoadingStrategy(
            ILocalLoader localLoader,
            IRemoteLoader remoteLoader,
            ICultureService cultureService,
            AbstractI18NClientOptions options,
            ILogger logger)
        {
            _localLoader = localLoader ?? throw new ArgumentNullException(nameof(localLoader));
            _cultureService = cultureService ?? throw new ArgumentNullException(nameof(cultureService));
            _remoteLoader = remoteLoader ?? throw new ArgumentNullException(nameof(remoteLoader));
            _options = options ?? throw new ArgumentNullException(nameof(options));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version)
        {
            return await _localLoader.LoadSupportedComponentListAsync(productName, version).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version)
        {
            IList<string> languageList = await _remoteLoader.LoadSupportedLanguageListAsync(productName, version).ConfigureAwait(false);

            if (languageList != null) return languageList;

            _logger.LogDebug($"Try to load supported language list from local for {productName}/{version}.");
            return await _localLoader.LoadSupportedLanguageListAsync(productName, version).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context)
        {
            // By default, the remote service is considered reachable.
            return await LoadTranslationsByComponentAsync(component, context, true).ConfigureAwait(false) ?? component;
        }

        private async Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context, bool isServiceAvailable)
        {
            // If the requested language is default language, load the translations from local bundles.
            if (_cultureService.IsDefaultCulture(component.LanguageCode))
                return await _localLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false);

            Component loadedComponent = isServiceAvailable ? await _remoteLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false) : null;

            if (loadedComponent != null) return loadedComponent;

            _logger.LogDebug($"Try to load component translations from local for {component.ProductName}/{component.ComponentName}/{component.LanguageCode}.");
            return await _localLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<IList<Component>> LoadTranslationsByComponentsAsync(IList<Component> components, ILoadingContext context)
        {
            // Get the status of the remote service in advance to avoid repeated invalid service access.
            bool IsServiceAvailable = _remoteLoader.IsServiceAvailable();
            return await components.LoadMessagesParallel((component) => LoadTranslationsByComponentAsync(component, context, IsServiceAvailable),
                _options.MaxDegreeOfParallelism == default ? I18NClientConstants.Parallelism.maxDegreeOfParallelism : _options.MaxDegreeOfParallelism)
                .ConfigureAwait(false);
        }
    }
}
