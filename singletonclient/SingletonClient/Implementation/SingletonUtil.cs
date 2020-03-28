/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json.Linq;
using System;
using System.Collections;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Resources;
using YamlDotNet.RepresentationModel;

namespace SingletonClient.Implementation
{
    public class SingletonUtil
    {
        /// <summary>
        /// New a Hashtable object and make it thread safe.
        /// </summary>
        /// <returns></returns>
        public static Hashtable NewHashtable()
        {
            return Hashtable.Synchronized(new Hashtable());
        }

        /// <summary>
        /// Read resource bytes from assembly
        /// </summary>
        /// <param name="resourceBaseName"></param>
        /// <param name="assembly"></param>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        public static Byte[] ReadResource(
            string resourceBaseName, Assembly assembly, string resourceName)
        {
            ResourceManager resourceManager = new ResourceManager(resourceBaseName, assembly);
            Byte[] bytes = (Byte[])resourceManager.GetObject(resourceName);
            return bytes;
        }

        /// <summary>
        /// Read a map from a resource by name and assembly.
        /// </summary>
        /// <param name="resourceBaseName"></param>
        /// <param name="assembly"></param>
        /// <returns></returns>
        public static Hashtable ReadResourceMap(string resourceBaseName, Assembly assembly)
        {
            ResourceManager resourceManager = new ResourceManager(resourceBaseName, assembly);
            CultureInfo cultureInfo = new System.Globalization.CultureInfo("en-US");
            ResourceSet resourceSet = resourceManager.GetResourceSet(cultureInfo, true, true);
            IDictionaryEnumerator enumerator = resourceSet.GetEnumerator();

            Hashtable table = new Hashtable();
            while (enumerator.MoveNext()) {
                table[enumerator.Key] = enumerator.Value;
            }
            resourceSet.Close();
            return table;
        }

        /// <summary>
        /// Convert from UTF8 binary to a string
        /// </summary>
        /// <param name="bytes"></param>
        /// <returns></returns>
        public static string ConvertToText(Byte[] bytes)
        {
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
            JObject dict = JObject.Parse(text);
            return dict;
        }

        public static bool CheckResponseValid(JToken token, Hashtable headers)
        {
            if (headers != null)
            {
                if (SingletonConst.StatusNotModified.Equals(headers[SingletonConst.HeaderResponseCode]))
                {
                    return false;
                }
            }
            if (token == null)
            {
                return false;
            }
            JObject result = token.Value<JObject>(SingletonConst.KeyResult);
            JObject status = result.Value<JObject>(SingletonConst.KeyResponse);
            if (status != null && status.Value<int>(SingletonConst.KeyCode) == 200)
            {
                return true;
            }
            return false;
        }

        public static JObject HttpGetJson(IAccessService accessService, string url, Hashtable headers)
        {
            JObject obj = new JObject();
            string text = accessService.HttpGet(url, headers);
            if (text != null)
            {
                JObject dict = ConvertToDict(text);
                obj.Add(SingletonConst.KeyResult, dict);
            }
            return obj;
        }

        public static JObject HttpPost(IAccessService accessService, string url, string text, Hashtable headers)
        {
            string responseData = accessService.HttpPost(url, text, headers);
            if (responseData == null)
            {
                return null;
            }

            JObject obj = new JObject();
            obj.Add(SingletonConst.KeyResult, ConvertToDict(responseData));
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

            YamlMappingNode root = (YamlMappingNode)yaml.Documents[0].RootNode;
            return root;
        }

        public static string NearLocale(string locale)
        {
            return SingletonClientManager.GetInstance().GetFallbackLocale(locale);
        }
    }
}

