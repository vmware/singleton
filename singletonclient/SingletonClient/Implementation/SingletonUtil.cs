/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Resources;
using System.Text;
using System.Text.RegularExpressions;
using YamlDotNet.RepresentationModel;

namespace SingletonClient.Implementation
{
    public static class SingletonUtil
    {
        public enum ResponseStatus
        {
            NetFail,
            WrongFormat,
            NoMessages,
            NotModified,
            Messages
        };

        /// <summary>
        /// New a Hashtable object and make it thread safe.
        /// </summary>
        /// <returns></returns>
        public static Hashtable NewHashtable(bool ignoreCase)
        {
            if (ignoreCase)
            {
                return Hashtable.Synchronized(new Hashtable(StringComparer.OrdinalIgnoreCase));
            }
            return Hashtable.Synchronized(new Hashtable());
        }

        /// <summary>
        /// Get empty string list.
        /// </summary>
        /// <returns></returns>
        public static List<string> GetEmptyStringList()
        {
            return new List<string>();
        }

        public static bool Equals(string first, string second)
        {
            if (first == null)
            {
                return second == null;
            }

            return first.Equals(second, StringComparison.OrdinalIgnoreCase);
        }

        /// <summary>
        /// Read resource bytes from assembly
        /// </summary>
        /// <param name="resourceBaseName"></param>
        /// <param name="assembly"></param>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        public static byte[] ReadResource(
            string resourceBaseName, Assembly assembly, string resourceName, string locale = null)
        {
            if (assembly == null)
            {
                return new byte[0];
            }

            resourceBaseName = assembly.GetName().Name + "." + resourceBaseName;
            string[] parts = Regex.Split(resourceName, "__ITEMNAME__");
            if (parts.Length > 1)
            {
                resourceBaseName += "." + parts[0];
                resourceName = parts[1];
            }

            ResourceManager resourceManager = new ResourceManager(resourceBaseName, assembly);
            if (locale != null)
            {
                CultureInfo cultureInfo = new CultureInfo(locale);
                return (byte[])resourceManager.GetObject(resourceName, cultureInfo);
            }
            byte[] bytes = (byte[])resourceManager.GetObject(resourceName);
            return bytes;
        }

        /// <summary>
        /// Read a map from a resource by name and assembly.
        /// </summary>
        /// <param name="resourceBaseName"></param>
        /// <param name="assembly"></param>
        /// <returns></returns>
        public static Hashtable ReadResourceMap(string resourceBaseName, string locale, Assembly assembly)
        {
            Hashtable table = new Hashtable();
            if (assembly == null)
            {
                return table;
            }

            resourceBaseName = assembly.GetName().Name + "." + resourceBaseName;
            ResourceManager resourceManager = new ResourceManager(resourceBaseName, assembly);
            string localeInUse = locale;
            ISingletonLocale singletonLocale = SingletonUtil.GetSingletonLocale(localeInUse);
            if (singletonLocale.IsLocaleAtStringEnd(resourceBaseName))
            {
                localeInUse = ConfigConst.DefaultLocale;
                singletonLocale = SingletonUtil.GetSingletonLocale(localeInUse);
            }

            ISingletonLocale defaultLocale = SingletonUtil.GetSingletonLocale(ConfigConst.DefaultLocale);
            bool tryParents = defaultLocale.Compare(singletonLocale);

            CultureInfo cultureInfo = new System.Globalization.CultureInfo(localeInUse);
            try
            {
                ResourceSet resourceSet = resourceManager.GetResourceSet(cultureInfo, true, tryParents);
                if (resourceSet != null)
                {
                    IDictionaryEnumerator enumerator = resourceSet.GetEnumerator();

                    while (enumerator.MoveNext())
                    {
                        table[enumerator.Key] = enumerator.Value;
                    }
                    resourceSet.Close();
                }
            }
            catch (Exception e)
            {
                SingletonUtil.HandleException(e);
            }
            return table;
        }

        /// <summary>
        /// Convert from UTF8 binary to a string
        /// </summary>
        /// <param name="bytes"></param>
        /// <returns></returns>
        public static string ConvertToText(Byte[] bytes)
        {
            if (bytes == null)
            {
                return null;
            }
            if (bytes.Length > 2 && bytes[0] == 0xef && bytes[1] == 0xbb && bytes[2] == 0xbf)
            {
                bytes[0] = 0x01;
                bytes[1] = 0x01;
                bytes[2] = 0x01;
            }
            string text = System.Text.Encoding.UTF8.GetString(bytes);
            text = text.Replace("\u0001", "");
            return text;
        }

        public static JObject ConvertToDict(string text)
        {
            JObject dict = new JObject();
            if (text == null)
            {
                return dict;
            }

            try
            {
                dict = JObject.Parse(text);
            }
            catch (Exception e)
            {
                SingletonUtil.HandleException(e);
            }

            return dict;
        }

        public static ResponseStatus CheckResponseValid(JToken token, Hashtable headers)
        {
            if (headers != null)
            {
                string responseCode = (string)headers[SingletonConst.HeaderResponseCode];
                if (SingletonConst.StatusNotModified.Equals(responseCode))
                {
                    return ResponseStatus.NotModified;
                }
            }

            if (token == null)
            {
                return ResponseStatus.NetFail;
            }
            JObject result = token.Value<JObject>(SingletonConst.KeyResult);
            if (result == null)
            {
                return ResponseStatus.WrongFormat;
            }
            JObject status = result.Value<JObject>(SingletonConst.KeyResponse);
            if (status == null)
            {
                return ResponseStatus.WrongFormat;
            }

            int code = status.Value<int>(SingletonConst.KeyCode);
            if (code == 200 || code == 604)
            {
                return ResponseStatus.Messages;
            }
            return ResponseStatus.NoMessages;
        }

        public static JObject HttpGetJson(IAccessService accessService,
            string url, Hashtable headers, int timeout, out string status, ILog logger)
        {
            JObject obj = new JObject();
            string text = accessService.HttpGet(url, headers, timeout, out status, logger);
            if (text != null)
            {
                JObject dict = ConvertToDict(text);
                obj.Add(SingletonConst.KeyResult, dict);
            }
            return obj;
        }

        /// <summary>
        /// Get the root node of a yaml.
        /// </summary>
        /// <param name="text"></param>
        /// <returns></returns>
        public static YamlMappingNode GetYamlRoot(string text)
        {
            var input = new StringReader(text);
            var yaml = new YamlStream();
            yaml.Load(input);

            try
            {
                YamlMappingNode root = (YamlMappingNode)yaml.Documents[0].RootNode;
                return root;
            }
            catch (Exception e)
            {
                SingletonUtil.HandleException(e);
            }

            return new YamlMappingNode();
        }

        /// <summary>
        /// Get ISingletonLocale object by locale.
        /// </summary>
        /// <returns></returns>
        public static ISingletonLocale GetSingletonLocale(string locale)
        {
            return SingletonLocaleUtil.GetSingletonLocale(locale);
        }

        /// <summary>
        /// Read text from a file.
        /// </summary>
        /// <returns></returns>
        public static string ReadTextFile(string path)
        {
            try
            {
                string text = File.ReadAllText(path, Encoding.UTF8);
                return text;
            }
            catch (Exception e)
            {
                HandleException(e);
            }
            return null;
        }

        /// <summary>
        /// Handle exception.
        /// </summary>
        /// <param name="e"></param>
        /// <returns></returns>
        public static void HandleException(Exception e)
        {
            // Method intentionally left empty.
        }

        /// <summary>
        /// Set list from json.
        /// </summary>
        /// <param name="strList"></param>
        /// <param name="array"></param>
        public static void SetListFromJson(List<string> strList, JArray array)
        {
            strList.Clear();
            foreach (var one in array)
            {
                strList.Add(one.ToString());
            }
        }

        /// <summary>
        /// Update list from another list.
        /// </summary>
        /// <param name="strList"></param>
        /// <param name="ja"></param>
        public static void UpdateListFromAnotherList(List<string> strList, List<string> strAnotherList)
        {
            if (strAnotherList == null)
            {
                return;
            }

            for (int i = 0; i < strAnotherList.Count; i++)
            {
                if (!strList.Contains(strAnotherList[i]))
                {
                    strList.Add(strAnotherList[i]);
                }
            }
        }

        /// <summary>
        /// Build combine key with locale and component.
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="component"></param>
        public static string GetCombineKey(string locale, string component, string type, object value)
        {
            if (string.IsNullOrEmpty(component))
            {
                component = "";
            }
            return string.Format("{0}_!_{1}_!_{2}_!_{3}", locale, component, type, value);
        }

        public static string AddPseudo(string message)
        {
            if (message != null)
            {
                return string.Format("{0}{1}{0}", "@@", message);
            }
            return null;
        }
    }
}
