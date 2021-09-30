using Newtonsoft.Json;
using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Models;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Utilities;
using I18NClient.Net.Abstractions.Plugable.Loader;

namespace I18NClient.Net.Loaders
{
    /// <summary>
    /// Remote resource loader based on Singleton service.
    /// </summary>
    public class SingletonRemoteLoader : IRemoteLoader
    {
        private readonly AbstractI18NClientOptions _i18NClientOptions;

        private readonly ILanguageCodeConvertor _languageCodeConvertor;

        private readonly ILogger _logger;

        private SingletonServiceStatus _singletonServiceStatus;

        /// <summary>
        /// Instantiate a remote resource loader based on Singleton service.
        /// </summary>
        /// <param name="i18NClientOptions">The options are used to create a i18n client based on Singleton service.</param>
        /// <param name="languageCodeConvertor">The language convertor to convert the language code between local and remote.</param>
        /// <param name="logger">The logger instance to perform logging.</param>
        public SingletonRemoteLoader(
            AbstractI18NClientOptions i18NClientOptions,
            ILanguageCodeConvertor languageCodeConvertor,
            ILogger logger)
        {
            _i18NClientOptions = i18NClientOptions ?? throw new ArgumentNullException(nameof(i18NClientOptions));
            _languageCodeConvertor = languageCodeConvertor ?? throw new ArgumentNullException(nameof(languageCodeConvertor));
            _logger = logger ?? throw new ArgumentNullException(nameof(logger));

            // Warm up HttpClient and initialize the Singleton service status.
            IsServiceAvailable();
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version)
        {
            try
            {
                var responseBody = await HttpClientProvider.GetHttpClient()
                    .GetStringAsync($"{_i18NClientOptions.BackendServiceUrl}/i18n/api/v2/translation/products/{productName}/versions/{version}/componentlist")
                    .ConfigureAwait(false);

                var response = JsonConvert.DeserializeObject<SingletonResponse<SingletonComponentList>>(responseBody);
                return response.data.components;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to load supported component list for {0}/{1}.", productName, version);
                return null;
            }
        }

        /// <inheritdoc/>
        public async Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version)
        {
            try
            {
                var responseBody = await HttpClientProvider.GetHttpClient()
                    .GetStringAsync($"{_i18NClientOptions.BackendServiceUrl}/i18n/api/v2/translation/products/{productName}/versions/{version}/localelist")
                    .ConfigureAwait(false);

                var response = JsonConvert.DeserializeObject<SingletonResponse<SingletonSupportedLocaleList>>(responseBody);
                return response.data.locales.ConvertAll(locale => _languageCodeConvertor.RemoteLanguageCodeToLocal(locale));
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to load supported locale list for {0}/{1}.", productName, version);
                return null;
            }
        }

        /// <inheritdoc/>
        public async Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context)
        {
            try
            {
                var httpClient = context.IsParallel ? HttpClientProvider.GetBatchHttpClient() : HttpClientProvider.GetHttpClient();
                var languageCode = _languageCodeConvertor.LocalToRemoteLanguageCode(component.LanguageCode);

                var responseBody = await httpClient
                    .GetStringAsync($"{_i18NClientOptions.BackendServiceUrl}/i18n/api/v2/translation/products" +
                    $"/{component.ProductName}/versions/{component.Version}/locales/{languageCode}/components/{component.ComponentName}")
                    .ConfigureAwait(false);

                var response = JsonConvert.DeserializeObject<SingletonResponse<SingletonComponentTranslations>>(responseBody);
                component.Messages = new Dictionary<string, string>(response.data.messages, StringComparer.OrdinalIgnoreCase);
                return component;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to load component translations for {0}/{1}/{2}/{3}.",
                    component.ProductName, component.Version, component.ComponentName, component.LanguageCode);
                return null;
            }
        }

        /// <inheritdoc/>
        public bool IsServiceAvailable()
        {
            _singletonServiceStatus = _singletonServiceStatus ?? new SingletonServiceStatus()
            {
                CheckPoint = DateTime.MinValue,
                IsServiceReachable = false
            };

            // Get the service status directly if not expired.
            TimeSpan ts = DateTime.UtcNow - _singletonServiceStatus.CheckPoint;
            if (ts.TotalSeconds < _i18NClientOptions.ServiceCheckPointTimeSpan)
                return _singletonServiceStatus.IsServiceReachable;

            _singletonServiceStatus.CheckPoint = DateTime.UtcNow;

            try
            {
                HttpClientProvider.GetHttpClient().SendAsync(new HttpRequestMessage
                {
                    Method = new HttpMethod("HEAD"),
                    RequestUri = new Uri($"{_i18NClientOptions.BackendServiceUrl}/i18n/api/v2/about/version", UriKind.Absolute)
                }).Result.EnsureSuccessStatusCode();
                _singletonServiceStatus.IsServiceReachable = true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "The remote service is not available.");
                _singletonServiceStatus.IsServiceReachable = false;
            }

            return _singletonServiceStatus.IsServiceReachable;
        }
    }
}
