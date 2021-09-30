namespace I18NClient.Net.Abstractions.Plugable.Loader
{
    /// <summary>
    /// Provide the context information in which the loading process runs.
    /// </summary>
    public interface ILoadingContext
    {
        /// <summary>
        /// Indicate if current is a parallel environment.
        /// </summary>
        bool IsParallel { get; set; }
    }
}
