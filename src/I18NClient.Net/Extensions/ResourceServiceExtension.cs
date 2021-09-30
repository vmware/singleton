using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Resource;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Extensions
{
    /// <summary>
    /// The extension to extend the features of resource service.
    /// </summary>
    public static class ResourceServiceExtension
    {
        /// <summary>
        /// Gets the supported component list of the application as an asynchronous operation. 
        /// </summary>
        /// <returns>The supported component list.</returns>
        public static IList<string> GetSupportedComponentList(this IResourceService resourceService)
        {
            return Task.Run(() => resourceService.GetSupportedComponentListAsync()).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Gets the supported language list of the application as an asynchronous operation. 
        /// </summary>
        /// <returns>The supported language list.</returns>
        public static IList<string> GetSupportedLanguageList(this IResourceService resourceService)
        {
            return Task.Run(() => resourceService.GetSupportedLanguageListAsync()).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Gets translations for the given component name and language.
        /// </summary>
        /// <param name="resourceService">The resource service to be extended.</param>
        /// <param name="componentName">The name of the component.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>The component instance has loaded messages.</returns>
        public static Component GetTranslationsByComponent(this IResourceService resourceService, string componentName, string languageCode)
        {
            return Task.Run(() => resourceService.GetTranslationsByComponentAsync(componentName, languageCode)).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Gets translation for the given component name, key and language.
        /// </summary>
        /// <param name="resourceService">The resource service to be extended.</param>
        /// <param name="componentName">The name of the component.</param>
        /// <param name="key">The key of the translation to get.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>The translation associated with the specified key.</returns>
        public static string GetTranslationByKey(this IResourceService resourceService, string componentName, string key, string languageCode)
        {
            return Task.Run(() => resourceService.GetTranslationByKeyAsync(componentName, key, languageCode)).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Loads messages by component and language from local bundles or remote into local cache.. 
        /// </summary>
        /// <param name="resourceService">The resource service to be extended.</param>
        /// <param name="componentName">The name of component.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        public static bool TryInitTranslationsByComponent(this IResourceService resourceService, string componentName, string languageCode)
        {
            return Task.Run(() => resourceService.TryInitTranslationsByComponentAsync(componentName, languageCode)).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Loads messages by product and language from local bundles or remote into local cache.
        /// </summary>
        /// <param name="resourceService">The resource service to be extended.</param>
        /// <param name="languageCode">The language code.</param>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        public static bool TryInitTranslationsByLanguage(this IResourceService resourceService, string languageCode)
        {
            return Task.Run(() => resourceService.TryInitTranslationsByLanguageAsync(languageCode)).GetAwaiter().GetResult();
        }

        /// <summary>
        /// Loads messages by product from local bundles or remote into local cache.
        /// </summary>
        /// <returns>True, If the messages are loaded successfully. Otherwise, false.</returns>
        public static bool TryInitTranslationsByProduct(this IResourceService resourceService)
        {
            return Task.Run(() => resourceService.TryInitTranslationsByProductAsync()).GetAwaiter().GetResult();
        }
    }
}
