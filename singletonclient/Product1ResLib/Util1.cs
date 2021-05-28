/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using MosaicoSolutions.CSharpProperties;

using SingletonClient;
using SingletonClient.Implementation;
using System.IO;
using System.Reflection;

namespace Product1ResLib
{
    public class Util1
    {
        private static IRelease rel;
        private static int count = 0;

        public static void Init()
        {
            IConfig cfg = I18N.LoadConfig(
                "SingletonRes.Singleton", Assembly.GetExecutingAssembly(), "singleton_config");
            rel = I18N.GetRelease(cfg);
        }

        public static int GetCount()
        {
            return count;
        }

        public static void IncreaseCount()
        {
            count ++;
        }

        public static void DecreaseCount()
        {
            if (count > 0)
            {
                count--;
            }
        }

        public static IConfig Config()
        {
            return rel.GetConfig();
        }

        public static IRelease Release()
        {
            return rel;
        }

        public static IReleaseMessages Messages()
        {
            return rel.GetMessages();
        }

        public static ITranslation Translation()
        {
            return rel.GetTranslation();
        }

        public static ISource Source(
            string component, string key, string source = null, string comment = null)
        {
            return rel.GetTranslation().CreateSource(component, key, source, comment);
        }
    }

    public class Test1
    {
        public static void DoTest1()
        {
            byte[] bytes = SingletonUtil.ReadResource("SingletonRes.Singleton", Assembly.GetExecutingAssembly(), "my_source");
            string content = SingletonUtil.ConvertToText(bytes);
            var properties = Properties.Load(new StringReader(content));

            string[] strs = { "text1", "text2" };
        }

    }
}
