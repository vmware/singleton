using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Loader;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Abstractions.Plugable.Localization
{
    /// <summary>
    /// A loader is designed to load the i18n data and related options.
    /// </summary>
    public interface ILoader
    {
        /// <summary>
        /// Load the translations for the given i18n component.
        /// </summary>
        /// <param name="component">The i18n component.</param>
        /// <param name="context">The context in which the current function is running.</param>
        /// <returns>The i18n component that have been loaded with messages.</returns>
        Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context);

        /// <summary>
        /// Load all component names of the application with given product name and version.
        /// </summary>
        /// <param name="productName">The name of the application.</param>
        /// <param name="version">The version of the application.</param>
        /// <returns>The component list of the application.</returns>
        Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version);

        /// <summary>
        /// Load the supported language list of the application.
        /// </summary>
        /// <param name="productName">The name of the application.</param>
        /// <param name="version">The version of the application.</param>
        /// <returns>The supported language list of the application.</returns>
        Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version);
    }
}
