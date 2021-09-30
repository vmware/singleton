using I18NClient.Net.Abstractions.Domains;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Resources;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using I18NClient.Net.Abstractions.Plugable.Localization;
using I18NClient.Net.Abstractions.Plugable.Option;
using I18NClient.Net.Abstractions.Plugable.Culture;
using I18NClient.Net.Abstractions.Plugable.Logger;
using I18NClient.Net.Abstractions.Plugable.Loader;

namespace I18NClient.Net.Loaders
{
    /// <summary>
    /// RESX local resource loader.
    /// </summary>
    public class ResxLocalLoader : ILocalLoader
    {
        private readonly AbstractI18NClientOptions _i18NClientOptions;

        private readonly ILogger _logger;

        private readonly Assembly _assembly;

        private readonly ICultureService _cultureService;

        /// <summary>
        /// Instantiate .resx resource loader.
        /// </summary>
        /// <param name="assembly">The assembly contains the culture-neutral resources.</param>
        /// <param name="i18NClientOptions">The options required for the local i18n solution.</param>
        /// <param name="cultureService">Provides the methods for culture helping operations.</param>
        /// <param name="logger">The logger instance to perform logging.</param>
        public ResxLocalLoader(
            AbstractI18NClientOptions i18NClientOptions,
            ILogger logger,
            Assembly assembly,
            ICultureService cultureService)
        {
            _i18NClientOptions = i18NClientOptions ?? throw new System.ArgumentNullException(nameof(i18NClientOptions));
            _logger = logger ?? throw new System.ArgumentNullException(nameof(logger));
            _assembly = assembly ?? throw new System.ArgumentNullException(nameof(assembly));
            _cultureService = cultureService ?? throw new System.ArgumentNullException(nameof(cultureService));
        }

        /// <inheritdoc/>
        public Task<IList<string>> LoadSupportedComponentListAsync(string productName, string version)
        {
            // Get the name of all source resources in the main assembly.
            string assemblyName = _assembly.GetName().Name;
            string[] resourceNames = _assembly.GetManifestResourceNames();
            IList<string> componentList = new List<string>();

            var componentNameQuery = from string resourceName in resourceNames
                                     let componentName = GetComponentName(assemblyName, resourceName)
                                     where componentName != null
                                     select componentName;

            componentNameQuery.ToList().ForEach(componentName => componentList.Add(componentName));
            return Task.FromResult(componentList);
        }

        private string GetComponentName(string assemblyName, string resourceName)
        {
            if (!resourceName.StartsWith(assemblyName)) return null;
            string resourceRelativePath = resourceName.Substring(assemblyName.Length + 1);

            // The qualified component name is NOT allowed to be a empty string.
            Regex reg = new Regex(_i18NClientOptions.OfflineResourceRelativePath.Replace("{0}", @"([\S]+?)"));
            Match match = reg.Match(resourceRelativePath);

            return match.Success ? match.Groups[1].Value : null;
        }

        /// <inheritdoc/>
        public Task<IList<string>> LoadSupportedLanguageListAsync(string productName, string version)
        {
            return Task.FromResult(_i18NClientOptions != null ? _i18NClientOptions.SupportedLanguages : Enumerable.Empty<string>().ToList());
        }

        /// <inheritdoc/>
        public Task<Component> LoadTranslationsByComponentAsync(Component component, ILoadingContext context)
        {
            var resourceRelativePath = string.Format(_i18NClientOptions.OfflineResourceRelativePath, component.ComponentName);

            if (_cultureService.IsDefaultCulture(component.LanguageCode))
            {
                LoadSourceMessages(component, resourceRelativePath);
                return Task.FromResult(component);
            }

            LoadTranslations(component, resourceRelativePath);
            return Task.FromResult(component);
        }

        private void LoadSourceMessages(Component component, string resourceRelativePath)
        {
            // Source resources are part of the main assembly.
            var resourcePath = string.Format("{0}.{1}.resources",
                _assembly.GetName().Name,
                resourceRelativePath);

            LoadResources(component, _assembly, resourcePath);
        }

        private void LoadTranslations(Component component, string resourceRelativePath)
        {
            // If the language isn't registered, return directly.
            if (!_i18NClientOptions.SupportedLanguages.Contains(component.LanguageCode))
            {
                _logger.LogWarning($"The language '{component.LanguageCode}'is not supported.");
                return;
            }

            // If the resource file isn't delivered in request language, return directly.
            if (!TryGetSatelliteAssembly(component.LanguageCode, out Assembly assembly)) 
            {
                _logger.LogWarning($"The resource file for '{component.LanguageCode}' is not found.");
                return;
            }

            var resourcePath = string.Format("{0}.{1}.{2}.resources",
                _assembly.GetName().Name,
                resourceRelativePath,
                component.LanguageCode);

            LoadResources(component, assembly, resourcePath);
        }

        private static void LoadResources(Component component, Assembly assembly, string resourcePath)
        {
            if (!assembly.GetManifestResourceNames().ToList().Contains(resourcePath)) return;

            // Enumerate all resources in the corresponding resource file.
            using (var stream = assembly.GetManifestResourceStream(resourcePath))
            {
                using (var resourceReader = new ResourceReader(stream))
                {
                    var resourceReaderEnumerator = resourceReader.GetEnumerator();
                    while (resourceReaderEnumerator.MoveNext())
                    {
                        var key = resourceReaderEnumerator.Key.ToString();
                        if (component.Messages.ContainsKey(key)) continue;
                        component.Messages.Add(key, resourceReaderEnumerator.Value.ToString());
                    }
                }
            }
        }

        private bool TryGetSatelliteAssembly(string languageCode, out Assembly assembly)
        {
            assembly = _assembly;
            bool isExist = false;

            try
            {
                // Get satellite assembly and corresponding resource path.
                CultureInfo cultureInfo = new CultureInfo(languageCode);
                assembly = _assembly.GetSatelliteAssembly(cultureInfo);
                isExist = true;
            }
            catch (CultureNotFoundException ex)
            {
                _logger.LogError(ex, "The culture {0} is not supported", languageCode);
            }
            catch (FileNotFoundException ex)
            {
                _logger.LogError(ex, "The satellite assembly corresponding to the culture {0} was not found.", languageCode);
            }

            return isExist;
        }
    }
}
