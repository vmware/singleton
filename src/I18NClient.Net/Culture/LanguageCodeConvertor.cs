using I18NClient.Net.Abstractions.Plugable.Culture;
using System;
using System.Collections.Generic;
using System.Linq;

namespace I18NClient.Net.Culture
{
    /// <summary>
    /// The implementation of language convertor based on Singleton service.
    /// </summary>
    public class LanguageCodeConvertor : ILanguageCodeConvertor
    {
        // The list of values is best not to be too long, and each value should be unique.
        private static readonly Dictionary<string, string> s_languageMap = new Dictionary<string, string>(StringComparer.InvariantCultureIgnoreCase)
        {
            { "zh-Hans","zh-CN" },
            { "zh-Hant","zh-TW" },
            { "en","en-US" }
        };

        private static readonly Dictionary<string, string> s_reversedLanguageMapd = s_languageMap.ToDictionary(x => x.Value, x => x.Key);

        /// <inheritdoc/>
        public string LocalToRemoteLanguageCode(string languageCode)
        {
            return s_reversedLanguageMapd.TryGetValue(languageCode, out string value) ? value : languageCode;
        }

        /// <inheritdoc/>
        public string RemoteLanguageCodeToLocal(string languageCode)
        {
            return s_languageMap.TryGetValue(languageCode, out string value) ? value : languageCode;
        }
    }
}
