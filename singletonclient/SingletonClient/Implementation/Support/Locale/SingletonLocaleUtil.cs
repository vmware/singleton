/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Helpers;
    using System.Collections;
    using System.Globalization;
    using YamlDotNet.RepresentationModel;

    public static class SingletonLocaleUtil
    {
        private static Hashtable _localeConvertMap = SingletonUtil.NewHashtable(true);
        private static Hashtable _singletonLocaleMap = SingletonUtil.NewHashtable(true);

        public static void SetFallbackConfig(string configText)
        {
            YamlMappingNode configRoot = SingletonUtil.GetYamlRoot(configText);
            var valuesMapping = (YamlMappingNode)configRoot.Children[new YamlScalarNode("locale")];
            foreach (var tuple in valuesMapping.Children)
            {
                _localeConvertMap[tuple.Key.ToString()] = tuple.Value.ToString();
            }
        }

        public static ISingletonLocale GetSingletonLocale(string locale)
        {
            if (string.IsNullOrEmpty(locale))
            {
                return GetSingletonLocale(CultureHelper.GetDefaultCulture());
            }

            ISingletonLocale singletonLocale = (ISingletonLocale)_singletonLocaleMap[locale];
            if (singletonLocale != null)
            {
                return singletonLocale;
            }

            CultureInfo cultureInfo = CultureHelper.GetCulture(locale);
            if (cultureInfo == null)
            {
                singletonLocale = GetSingletonLocale(CultureHelper.GetDefaultCulture());
                _singletonLocaleMap[locale] = singletonLocale;
                return singletonLocale;
            }

            // 1. locale itself
            singletonLocale = new SingletonLocale(locale);
            // 2. Microsoft locale name
            singletonLocale.AddNearLocale(cultureInfo.Name);
            // 3. RFC 4646 locale name
            singletonLocale.AddNearLocale(cultureInfo.IetfLanguageTag);

            // 4. neutrual locale name
            if (cultureInfo.IsNeutralCulture)
            {
                singletonLocale.AddNearLocale(cultureInfo.Name);
            }
            else
            {
                CultureInfo cultureNeutral = cultureInfo.Parent;
                singletonLocale.AddNearLocale(cultureNeutral.Name);
            }

            // 5. locale after fallback
            for (int i = 0; i < singletonLocale.GetCount(); i++)
            {
                string localeRemote = (string)_localeConvertMap[singletonLocale.GetNearLocale(i)];
                if (localeRemote != null)
                {
                    singletonLocale.AddNearLocale(localeRemote);
                    break;
                }
            }

            _singletonLocaleMap[locale] = singletonLocale;
            return singletonLocale;
        }
    }
}