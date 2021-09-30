using I18NClient.Net.Abstractions.Domains;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Abstractions.Resource
{
    /// <summary>
    /// Provides an interface to get or set i18n resources.
    /// </summary>
    public interface IResourceService
    {
        /// <summary>
        /// Gets translations for the given component name and language as an asynchronous operation.
        /// </summary>
        /// <param name="componentName">The name of the component.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>The component instance has loaded messages.</returns>
        Task<Component> GetTranslationsByComponentAsync(string componentName, string languageCode);

        /// <summary>
        /// Gets the supported language list of the application as an asynchronous operation. 
        /// </summary>
        /// <returns>The supported language list.</returns>
        Task<IList<string>> GetSupportedLanguageListAsync();

        /// <summary>
        /// Gets the supported component list of the application as an asynchronous operation. 
        /// </summary>
        /// <returns>The supported component list.</returns>
        Task<IList<string>> GetSupportedComponentListAsync();

        /// <summary>
        /// Gets translation for the given component name, key and language as an asynchronous operation.
        /// </summary>
        /// <returns>The translation associated with the specified key.</returns>
        Task<string> GetTranslationByKeyAsync(string componentName, string key, string languageCode);

        /// <summary>
        /// Loads messages by product from local bundles or remote into local cache as an asynchronous operation.
        /// </summary>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        Task<bool> TryInitTranslationsByProductAsync();

        /// <summary>
        /// Loads messages by product and language from local bundles or remote into local cache as an asynchronous operation.
        /// </summary>
        /// <param name="languageCode">The language code.</param>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        Task<bool> TryInitTranslationsByLanguageAsync(string languageCode);

        /// <summary>
        /// Loads messages by component and language from local bundles or remote into local cache as an asynchronous operation. 
        /// </summary>
        /// <param name="componentName">The name of component.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        Task<bool> TryInitTranslationsByComponentAsync(string componentName, string languageCode);
    }
}
