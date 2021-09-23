/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Text;
using System.IO;
using YamlDotNet.RepresentationModel;

using SingletonClient.Implementation;


namespace UnitTestSingleton
{
    public class TextUtil
    {
        public static YamlMappingNode GetYamlRoot(string text)
        {
            if (string.IsNullOrEmpty(text))
            {
                return null;
            }

            return (YamlMappingNode)SingletonUtil.GetYamlRoot(text);
        }

        public static YamlMappingNode GetMapChild(YamlMappingNode node, string key)
        {
            foreach (var entry in node.Children)
            {
                if (entry.Key.ToString().Equals(key))
                {
                    return (YamlMappingNode)entry.Value;
                }
            }

            return null;
        }

        public static string GetItemValue(YamlMappingNode node, string key)
        {
            foreach (var entry in node.Children)
            {
                if (entry.Key.ToString().Equals(key))
                {
                    return (string)entry.Value;
                }
            }

            return null;
        }

        public static YamlSequenceNode GetArrayChild(YamlMappingNode node, string key)
        {
            foreach (var entry in node.Children)
            {
                if (entry.Key.ToString().Equals(key))
                {
                    return (YamlSequenceNode)entry.Value;
                }
            }

            return null;
        }

        public static string GetText(YamlMappingNode node)
        {
            var yaml = new YamlDocument(node);
            var yamlStream = new YamlStream(yaml);
            var buffer = new StringBuilder();
            var writer = new StringWriter(buffer);
            yamlStream.Save(writer);
            string text = writer.ToString();
            text = text.Substring(0, text.Length - 5);
            return text;
        }
    }
}
