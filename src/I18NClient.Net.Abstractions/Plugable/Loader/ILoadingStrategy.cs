using I18NClient.Net.Abstractions.Domains;
using I18NClient.Net.Abstractions.Plugable.Loader;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace I18NClient.Net.Abstractions.Plugable.Localization
{
    /// <summary>
    /// The strategy to define the logic for loading data from multiple loaders.
    /// </summary>
    public interface ILoadingStrategy : ILoader
    {
        /// <summary>
        /// The method to load messages for a batch of components in parallel.
        /// </summary>
        /// <param name="components">The component instance list.</param>
        /// <param name="context">The context in which the current function is running.</param>
        /// <returns>The loaded component list.</returns>
        Task<IList<Component>> LoadTranslationsByComponentsAsync(IList<Component> components, ILoadingContext context);
    }
}
