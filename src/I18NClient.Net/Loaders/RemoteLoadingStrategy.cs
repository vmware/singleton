using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Loader;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Constants;
using I18NClient.Net.Extensions;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Loaders
{
    /// <summary>
    /// The logic is used to load the i18n resource from remote service.
    /// </summary>
    public class RemoteLoadingStrategy : ILoadingStrategy
    {
        private readonly ILocalLoader _localLoader;

        private readonly IRemoteLoader _remoteLoader;

        private readonly ICultureService _cultureService;

        private readonly AbstractI18NClientOptions _options;

        /// <summary>
        /// Instantiate a remote loading strategy.
        /// </summary>
        /// <param name="localLoader">The local loader which implement ILocalLoader.</param>
        /// <param name="remoteLoader">The remote loader which implement IRemoteLoader.</param>
        /// <param name="cultureService">The culture service instance.</param>
        /// <param name="options">The i18n client related configurations.</param>
        public RemoteLoadingStrategy(
            ILocalLoader localLoader,
            IRemoteLoader remoteLoader,
            ICultureService cultureService,
            AbstractI18NClientOptions options)
        {
            _localLoader = localLoader ?? throw new ArgumentNullException(nameof(localLoader));
            _cultureService = cultureService ?? throw new ArgumentNullException(nameof(cultureService));
            _remoteLoader = remoteLoader ?? throw new ArgumentNullException(nameof(remoteLoader));
            _options = options ?? throw new ArgumentNullException(nameof(options));
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version)
        {
            return await _localLoader.LoadSupportedComponentListAsync(productName, version).ConfigureAwait(false) ?? new List<string>();
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version)
        {
            return await _remoteLoader.LoadSupportedLanguageListAsync(productName, version).ConfigureAwait(false) ?? new List<string>();
        }

        /// <inheritdoc/>
        public async Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context)
        {
            // If the requested language is default language, load the translations from local bundles.
            if (_cultureService.IsDefaultCulture(component.LanguageCode))
                return await _localLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false);

            return await _remoteLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false) ?? component;
        }

        /// <inheritdoc/>
        public async Task<IList<Component>> LoadTranslationsByComponentsAsync(IList<Component> components, ILoadingContext context)
        {
            return await components.LoadMessagesParallel((component) => LoadTranslationsByComponentAsync(component, context),
                _options.MaxDegreeOfParallelism == default ? I18NClientConstants.Parallelism.maxDegreeOfParallelism : _options.MaxDegreeOfParallelism)
                .ConfigureAwait(false);
        }
    }
}
