using System;
using System.Configuration;
using System.Linq;
using I18NClient.Net.Options;
using Sample.ConsoleApp.NetFramework;

namespace Sample.ConsoleApp.NetFramework.Utilities
{
    /// <summary>
    /// The helper to initialize the i18n client options.
    /// </summary>
    public static class I18NClientOptionsHelper
    {
        private const string SECTIONNAME = "I18NClientConfiguration";

        /// <summary>
        /// Gets i18n client options.
        /// </summary>
        /// <returns>i18n client options.</returns>
        public static I18NClientOptions GetI18NClientOptions()
        {
            var i18NClientConfiguration = ConfigurationManager.GetSection(SECTIONNAME) as I18NClientConfiguration;
            var i18nClientOptions = new I18NClientOptions();

            if (i18NClientConfiguration == null)
            {
                return i18nClientOptions;
            }

            i18nClientOptions.ProductName = i18NClientConfiguration.ProductName ?? i18nClientOptions.ProductName;
            i18nClientOptions.Version = i18NClientConfiguration.Version ?? i18nClientOptions.Version;
            i18nClientOptions.BackendServiceUrl = i18NClientConfiguration.BackendServiceUrl ?? i18nClientOptions.BackendServiceUrl;
            i18nClientOptions.OfflineResourceRelativePath = i18NClientConfiguration.OfflineResourceRelativePath ?? i18nClientOptions.OfflineResourceRelativePath;
            i18nClientOptions.MaxDegreeOfParallelism = i18NClientConfiguration.MaxDegreeOfParallelism;
            i18nClientOptions.DefaultLanguage = i18NClientConfiguration.DefaultLanguage;
            i18nClientOptions.SupportedLanguages = i18NClientConfiguration.SupportedLanguages.Trim().Split(',').ToList().ConvertAll(language => language.Trim());
            i18nClientOptions.ServiceCheckPointTimeSpan = i18NClientConfiguration.ServiceCheckPointTimeSpan;

            return i18nClientOptions;
        }
    }
}
