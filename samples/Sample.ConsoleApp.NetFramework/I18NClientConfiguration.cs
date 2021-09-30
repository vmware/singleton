namespace Sample.ConsoleApp.NetFramework
{
    using System.Configuration;

    /// <summary>
    /// The configuration constants for I18NClient.
    /// </summary>
    public class I18NClientConfiguration : ConfigurationSection
    {
        /// <summary>
        /// Gets or sets the product name registered in the Singleton service.
        /// </summary>
        [ConfigurationProperty("productName", IsKey = true, IsRequired = true)]
        public string ProductName
        {
            get { return (string)this["productName"]; }
            set { this["productName"] = value; }
        }

        /// <summary>
        /// Gets or sets the release version registered in the Singleton service.
        /// </summary>
        [ConfigurationProperty("version", IsKey = true, IsRequired = true)]
        public string Version
        {
            get { return (string)this["version"]; }
            set { this["version"] = value; }
        }

        /// <summary>
        /// Gets or sets the Singleton service URL.
        /// </summary>
        [ConfigurationProperty("backendServiceUrl", IsKey = true, IsRequired = false)]
        public string BackendServiceUrl
        {
            get { return (string)this["backendServiceUrl"]; }
            set { this["backendServiceUrl"] = value; }
        }

        /// <summary>
        /// Gets or sets the off-line resource relative path.
        /// </summary>
        [ConfigurationProperty("offlineResourceRelativePath", IsKey = true, IsRequired = true)]
        public string OfflineResourceRelativePath
        {
            get { return (string)this["offlineResourceRelativePath"]; }
            set { this["offlineResourceRelativePath"] = value; }
        }

        /// <summary>
        /// Gets or sets the default language code.
        /// </summary>
        [ConfigurationProperty("defaultLanguage", IsKey = true, IsRequired = false, DefaultValue = "en-US")]
        public string DefaultLanguage
        {
            get { return (string)this["defaultLanguage"]; }
            set { this["defaultLanguage"] = value; }
        }

        /// <summary>
        /// Gets or sets the supported languages.
        /// </summary>
        [ConfigurationProperty("supportedLanguages", IsKey = true, IsRequired = false, DefaultValue = "en-US")]
        public string SupportedLanguages
        {
            get { return (string)this["supportedLanguages"]; }
            set { this["supportedLanguages"] = value; }
        }

        /// <summary>
        /// Gets or sets the max degree of parallelism.
        /// </summary>
        [ConfigurationProperty("maxDegreeOfParallelism", IsKey = true, IsRequired = false, DefaultValue = 30)]
        public int MaxDegreeOfParallelism
        {
            get { return (int)this["maxDegreeOfParallelism"]; }
            set { this["maxDegreeOfParallelism"] = value; }
        }

        /// <summary>
        /// Gets or sets the service checkPoint timeSpan.
        /// </summary>
        [ConfigurationProperty("serviceCheckPointTimeSpan", IsKey = true, IsRequired = false, DefaultValue = 300)]
        public int ServiceCheckPointTimeSpan
        {
            get { return (int)this["serviceCheckPointTimeSpan"]; }
            set { this["serviceCheckPointTimeSpan"] = value; }
        }
    }
}
