using I18NClient.Net.Abstractions.Plugable.Loader;

namespace I18NClient.Net.Loaders
{
    /// <inheritdoc/>
    public class LoadingContext : ILoadingContext
    {
        /// <inheritdoc/>
        public bool IsParallel { get; set; } = false;

        /// <summary>
        /// Initialize a context instance.
        /// </summary>
        public static LoadingContext Current => new LoadingContext();
    }
}
