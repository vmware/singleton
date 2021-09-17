/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using SingletonClient.Implementation.Release;

namespace SingletonClient.Implementation
{
    public class SingletonRelease : SingletonReleaseInternal, IRelease, IReleaseMessages, ITranslation, ILog
    {
        /// <summary>
        /// IRelease
        /// </summary>
        public IReleaseMessages GetMessages()
        {
            return this;
        }

        /// <summary>
        /// IRelease
        /// </summary>
        public ITranslation GetTranslation()
        {
            return this;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetAllSource()
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(
                _config.GetSourceLocale(), true);
            return languageMessages;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public ILocaleMessages GetLocaleMessages(string locale, bool asSource = false)
        {
            ILocaleMessages languageMessages = GetReleaseMessages().GetLocaleMessages(
                locale, asSource);
            return languageMessages;
        }

        /// <summary>
        /// IReleaseMessages
        /// </summary>
        public Dictionary<string, ILocaleMessages> GetAllLocaleMessages()
        {
            Dictionary<string, ILocaleMessages> langDataDict =
                new Dictionary<string, ILocaleMessages>();
            List<string> localeList = GetLocaleList();
            for (int i = 0; i < localeList.Count; i++)
            {
                string locale = localeList[i];
                ILocaleMessages languageData = GetReleaseMessages().GetLocaleMessages(locale);
                if (languageData != null)
                {
                    langDataDict[locale] = languageData;
                }
            }
            return langDataDict;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public ISource CreateSource(
            string component, string key, string source = null, string comment = null)
        {
            if ((component == null && _byKey == null) || key == null)
            {
                return null;
            }

            ISource src = new SingletonSource(component, key, source, comment);
            return src;
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(string locale, ISource source)
        {
            _task.Check();

            return GetRawMessage(locale, source);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string GetString(
            string component, string key, string source = null, string comment = null)
        {
            ISource sourceObject = CreateSource(component, key, source, comment);
            string locale = GetCurrentLocale();
            return GetString(locale, sourceObject);
        }

        /// <summary>
        /// ITranslation
        /// </summary>
        public string Format(string locale, ISource source, params object[] objects)
        {
            if (source == null)
            {
                return null;
            }
            string text = GetString(locale, source);
            if (text != null && objects != null && objects.Length > 0)
            {
                try
                {
                    text = string.Format(text, objects);
                }
                catch (FormatException)
                {
                    string[] strs = Regex.Split(text, "{([0-9]+)}");
                    int maxPlaceHolderIndex = -1;
                    for (int i = 1; i < strs.Length; i += 2)
                    {
                        int temp = Convert.ToInt32(strs[i]);
                        if (temp > maxPlaceHolderIndex)
                        {
                            maxPlaceHolderIndex = temp;
                        }
                    }
                    if (maxPlaceHolderIndex >= 0)
                    {
                        object[] objectsExt = new object[maxPlaceHolderIndex + 1];

                        for (int i = 0; i < maxPlaceHolderIndex + 1; i++)
                        {
                            if (i < objects.Length)
                            {
                                objectsExt[i] = objects[i];
                            }
                            else
                            {
                                objectsExt[i] = "{" + i + "}";
                            }
                        }
                        text = string.Format(text, objectsExt);
                    }
                }
            }
            return text;
        }
    }
}
