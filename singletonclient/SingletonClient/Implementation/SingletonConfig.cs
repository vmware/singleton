/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Text.RegularExpressions;
using YamlDotNet.RepresentationModel;

namespace SingletonClient.Implementation
{
    public class SingletonConfig : IConfig
    {
        private string _configText;
        private Hashtable _configData;
        private List<string> _componentList;
        private Hashtable _componentHt;

        private string _resourceBaseName;
        private Assembly _resourceAssembly;

        public SingletonConfig(string resourceBaseName, Assembly resourceAssembly)
        {
            _resourceBaseName = resourceBaseName;
            _resourceAssembly = resourceAssembly;
        }

        private void ReadConfig(string configText)
        {
            if (_configData != null)
            {
                return;
            }

            YamlMappingNode configRoot = SingletonUtil.GetYamlRoot(configText);
            _configData = SingletonUtil.NewHashtable();
            foreach (var entry in configRoot.Children)
            {
                _configData[entry.Key.ToString()] = entry.Value;
            }
        }

        public void SetConfigData(string text)
        {
            // To get rid of comments.
            var strs = Regex.Split(text, "(#.*)");

            _configText = "";
            for (int i = 0; i < strs.Length; i++)
            {
                if (i % 2 == 1)
                {
                    continue;
                }
                _configText += strs[i];
            }

            ReadConfig(_configText);
        }

        public string GetStringValue(string key)
        {
            var v = _configData[key];
            if (v != null)
            {
                return v.ToString();
            }
            return null;
        }

        public bool GetBoolValue(string key)
        {
            bool f = "true".Equals(_configData[key].ToString());
            return f;
        }

        public int GetIntValue(string key)
        {
            int value = Convert.ToInt32(_configData[key].ToString());
            return value;
        }

        public string GetKey()
        {
            string product = GetProduct();
            string version = GetVersion();

            string key = product + "^" + version;
            return key;
        }

        public string GetProduct()
        {
            string product = GetStringValue(ConfigConst.KeyProduct);
            return product;
        }

        public string GetVersion()
        {
            string version = GetStringValue(ConfigConst.KeyVersion);
            return version;
        }

        /// <summary>
        /// Get component name list.
        /// </summary>
        /// <returns></returns>
        public List<string> GetComponentList()
        {
            if (_componentList == null)
            {
                YamlSequenceNode v = (YamlSequenceNode)_configData[ConfigConst.KeyComponents];
                _componentList = new List<string>();
                _componentHt = SingletonUtil.NewHashtable();
                foreach (var entry in v.Children)
                {
                    string component = entry[ConfigConst.KeyComponent].ToString();
                    _componentList.Add(component);

                    YamlSequenceNode w = (YamlSequenceNode)entry[ConfigConst.KeyResource];
                    List<string> ar = new List<string>();
                    foreach (var one in w.Children)
                    {
                        string resName = one[ConfigConst.KeyName].ToString();
                        ar.Add(resName);
                    }
                    _componentHt.Add(component, ar);
                }
            }
            return _componentList;
        }

        /// <summary>
        /// Get source registration list of a component.
        /// </summary>
        /// <param name="component"></param>
        /// <returns></returns>
        public List<string> GetComponentSourceList(string component)
        {
            GetComponentList();
            List<string> sourceList = (List<string>)_componentHt[component];
            return sourceList;
        }

        /// <summary>
        /// Read resource as a text.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <returns></returns>
        public string ReadResourceText(string resourceName)
        {
            Byte[] bytes = SingletonUtil.ReadResource(
                _resourceBaseName, _resourceAssembly, resourceName);
            string text = SingletonUtil.ConvertToText(bytes);
            return text;
        }

        /// <summary>
        /// Read a map from resource by a parser.
        /// </summary>
        /// <param name="resourceName"></param>
        /// <param name="parser"></param>
        /// <returns></returns>
        public Hashtable ReadResourceMap(string resourceName, ISourceParser parser)
        {
            string[] array = resourceName.Split(new char[] {'(', ')'});
            string baseName = _resourceBaseName;
            if (array.Length > 1)
            {
                string[] parts = _resourceBaseName.Split(new char[] { '.' });
                parts[parts.Length - 1] = array[1];
                baseName = string.Join(".", parts);
                resourceName = array[0];
            }

            if (string.IsNullOrEmpty(resourceName))
            {
                return SingletonUtil.ReadResourceMap(baseName, _resourceAssembly);
            }

            Byte[] bytes = SingletonUtil.ReadResource(baseName, _resourceAssembly, resourceName);
            string text = SingletonUtil.ConvertToText(bytes);
            Hashtable bundle = parser.Parse(text);
            return bundle;
        }
    }
}

