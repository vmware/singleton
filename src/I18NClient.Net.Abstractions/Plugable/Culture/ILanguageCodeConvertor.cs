namespace I18NClient.Net.Abstractions.Plugable.Culture
{
    /// <summary>
    /// Handle the conversion between language codes of different platforms.
    /// </summary>
    public interface ILanguageCodeConvertor
    {
        /// <summary>
        /// From the local registered language code, get the language code supported in remote service.
        /// </summary>
        /// <param name="languageCode">The local language code.</param>
        /// <returns>The language code to query the translations from remote service.</returns>
        string LocalToRemoteLanguageCode(string languageCode);

        /// <summary>
        /// From the remote language code, get the language code supported in local.
        /// </summary>
        /// <param name="languageCode">The remote language code.</param>
        /// <returns>The language code to read local resource bundle.</returns>
        string RemoteLanguageCodeToLocal(string languageCode);
    }
}
