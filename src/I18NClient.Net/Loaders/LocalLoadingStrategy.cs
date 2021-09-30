using I18NClient.Net.Abstractions.Domains;
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
    /// <inheritdoc/>
    public class LocalLoadingStrategy : ILoadingStrategy
    {
        private readonly ILocalLoader _localLoader;

        private readonly AbstractI18NClientOptions _options;

        /// <summary>
        /// Instantiate a local loading strategy.
        /// </summary>
        /// <param name="localLoader">The instance of the ILocalLoader</param>
        /// <param name="options">The i18n client related configurations.</param>
        public LocalLoadingStrategy(ILocalLoader localLoader, AbstractI18NClientOptions options)
        {
            _localLoader = localLoader ?? throw new ArgumentNullException(nameof(localLoader));
            _options = options ?? throw new ArgumentNullException(nameof(options));
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version)
        {
            return await _localLoader.LoadSupportedComponentListAsync(productName, version).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version)
        {
            return await _localLoader.LoadSupportedLanguageListAsync(productName, version).ConfigureAwait(false);
        }

        /// <inheritdoc/>
        public async Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context)
        {
            return await _localLoader.LoadTranslationsByComponentAsync(component, context).ConfigureAwait(false) ?? component;
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
