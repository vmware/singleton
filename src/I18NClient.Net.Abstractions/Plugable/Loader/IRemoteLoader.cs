namespace I18NClient.Net.Abstractions.Plugable.Localization
{
    /// <summary>
    /// The loader is designed to load i18n remote resources.
    /// </summary>
    public interface IRemoteLoader : ILoader
    {
        /// <summary>
        /// Get the status of remote web service.
        /// </summary>
        /// <returns>If the service is reachable, return true. Otherwise, return false.</returns>
        bool IsServiceAvailable();
    }
}
