/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient.Implementation.Support;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Threading;
using YamlDotNet.RepresentationModel;

namespace SingletonClient.Implementation.Helpers
{
    public static class CultureHelper
    {
        private const string DefaultCulture = "en-US";

        private static Hashtable _localeConvertMap = SingletonUtil.NewHashtable(true);
        private static Hashtable _localeFallbackMap = SingletonUtil.NewHashtable(true);

        public static void SetCurrentCulture(string cultureName)
        {
            Thread.CurrentThread.CurrentCulture = GetCulture(cultureName);
            Thread.CurrentThread.CurrentUICulture = Thread.CurrentThread.CurrentCulture;
        }

        public static CultureInfo GetCulture(string cultureName)
        {
            if (!string.IsNullOrEmpty(cultureName))
            {
                try
                {
                    return new System.Globalization.CultureInfo(cultureName);
                }
                catch (CultureNotFoundException e)
                {
                    SingletonUtil.HandleException(e);
                }
            }
            return new System.Globalization.CultureInfo(GetDefaultCulture());
        }

        public static string GetDefaultCulture()
        {
            return DefaultCulture;
        }

        public static string GetCurrentCulture()
        {
            return Thread.CurrentThread.CurrentCulture.Name;
        }

        public static string GetCultureName(string cultureName)
        {
            if (string.IsNullOrEmpty(cultureName))
            {
                return DefaultCulture;
            }

            try
            {
                CultureInfo cultureInfo = new System.Globalization.CultureInfo(cultureName);
                return cultureInfo.IetfLanguageTag;
            }
            catch (CultureNotFoundException e)
            {
                SingletonUtil.HandleException(e);
            }

            return DefaultCulture;
        }

        public static void SetFallbackConfig(string configText)
        {
            YamlMappingNode configRoot = SingletonUtil.GetYamlRoot(configText);
            var valuesMapping = (YamlMappingNode)configRoot.Children[new YamlScalarNode("locale")];
            foreach (var tuple in valuesMapping.Children)
            {
                _localeConvertMap[tuple.Key.ToString()] = tuple.Value.ToString();
            }
        }

        public static ISingletonLocale GetFallbackLocaleList(string locale)
        {
            if (string.IsNullOrEmpty(locale))
            {
                return GetFallbackLocaleList(DefaultCulture);
            }

            ISingletonLocale singletonLocale = (ISingletonLocale)_localeFallbackMap[locale];
            if (singletonLocale != null)
            {
                return singletonLocale;
            }

            CultureInfo cultureInfo = null;
            try
            {
                cultureInfo = new System.Globalization.CultureInfo(locale);
                cultureInfo = new System.Globalization.CultureInfo(cultureInfo.LCID);
            }
            catch (CultureNotFoundException)
            {
                singletonLocale = GetFallbackLocaleList(DefaultCulture);
                _localeFallbackMap[locale] = singletonLocale;
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
            } else
            {
                CultureInfo cultureNeutral = cultureInfo.Parent;
                singletonLocale.AddNearLocale(cultureNeutral.Name);
            }

            // 5. locale after fallback
            string localeRemote = null;
            for(int i=0; i<singletonLocale.GetCount(); i++)
            {
                localeRemote = (string)_localeConvertMap[singletonLocale.GetNearLocale(i)];
                if (localeRemote != null)
                {
                    singletonLocale.AddNearLocale(localeRemote);
                    break;
                }
            }

            _localeFallbackMap[locale] = singletonLocale;
            return singletonLocale;
        }
    }
}
