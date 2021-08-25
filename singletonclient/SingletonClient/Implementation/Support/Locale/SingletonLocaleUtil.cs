/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation.Support
{
    using SingletonClient.Implementation.Data;
    using SingletonClient.Implementation.Helpers;
    using System.Globalization;
    using YamlDotNet.RepresentationModel;

    public static class SingletonLocaleUtil
    {
        private static ISingletonTable<string> _localeConvertTable = new SingletonTable<string>();
        private static ISingletonTable<ISingletonLocale> _singletonLocaleTable = new SingletonTable<ISingletonLocale>();

        public static void SetFallbackConfig(string configText)
        {
            YamlMappingNode configRoot = SingletonUtil.GetYamlRoot(configText);
            var valuesMapping = (YamlMappingNode)configRoot.Children[new YamlScalarNode("locale")];
            foreach (var tuple in valuesMapping.Children)
            {
                _localeConvertTable.SetItem(tuple.Key.ToString(), tuple.Value.ToString());
            }
        }

        public static ISingletonLocale GetSingletonLocale(string locale)
        {
            if (string.IsNullOrEmpty(locale))
            {
                return GetSingletonLocale(CultureHelper.GetDefaultCulture());
            }

            ISingletonLocale singletonLocale = _singletonLocaleTable.GetItem(locale);
            if (singletonLocale != null)
            {
                return singletonLocale;
            }

            CultureInfo cultureInfo = CultureHelper.GetCulture(locale);
            if (cultureInfo == null)
            {
                singletonLocale = GetSingletonLocale(CultureHelper.GetDefaultCulture());
                _singletonLocaleTable.SetItem(locale, singletonLocale);
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
                string localeRemote = _localeConvertTable.GetItem(singletonLocale.GetNearLocale(i));
                if (localeRemote != null)
                {
                    singletonLocale.AddNearLocale(localeRemote);
                    break;
                }
            }

            _singletonLocaleTable.SetItem(locale, singletonLocale);
            return singletonLocale;
        }
    }
}
