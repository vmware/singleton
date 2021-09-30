using System.Collections.Generic;

namespace I18NClient.Net.Abstractions.Plugable.Option
{
    /// <summary>
    /// The options are used to create and initialize a i18n client.
    /// </summary>
    public abstract class AbstractI18NClientOptions
    {
        /// <summary>
        /// The ProductName property contains the name of the application being localized.
        /// This is used to communicate with Singleton service to fetch the translations.
        /// </summary>
        public abstract string ProductName { get; set; }

        /// <summary>
        /// The Version property contains the version of the application being localized.
        /// This is used to communicate with Singleton service to fetch the translations.
        /// </summary>
        public abstract string Version { get; set; }

        /// <summary>
        /// The BackendServiceUrl property contains the URL of the back-end service instance.
        /// This is used to communicate with Singleton service to fetch the translations.
        /// </summary>
        public string BackendServiceUrl { get; set; }

        /// <summary>
        /// The relative path refers to a location that is relative to the current project directory.
        /// The '{0}' is a placeholder for the component name.
        /// </summary>
        public string OfflineResourceRelativePath { get; set; } = "Resources.{0}.Messages";

        /// <summary>
        /// Local configuration of currently supported languages has a lower priority than remote configuration.
        /// </summary>
        public IList<string> SupportedLanguages { get; set; } = new List<string>();

        /// <summary>
        /// The property is used to specify the language used at the root level.
        /// </summary>
        public string DefaultLanguage { get; set; } = "en-US";

        /// <summary>
        /// The expiration time of remote service status, default value is 5 minutes.
        /// </summary>
        public int ServiceCheckPointTimeSpan { get; set; } = 300;

        /// <summary>
        /// Gets or sets the maximum number of concurrent tasks enabled.
        /// </summary>
        public abstract int MaxDegreeOfParallelism { get; set; }
    }
}
