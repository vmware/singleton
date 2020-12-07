/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using System;
using System.Reflection;

namespace Product2ResLib
{
    class Values
    {
        private static string nameSpace = 
            System.Reflection.MethodBase.GetCurrentMethod().DeclaringType.Namespace;
        public static string BASE_RES_NAME = nameSpace + ".SingletonRes.Singleton";
        public static Assembly assembly = typeof(Values).Assembly;
    }

    public class Util2
    {
        private static IRelease rel;
        private static int count = 1;

        public static void Init()
        {
            new TestUtil().DoSomething();

            string[] configNames =
            {
                "sgtn_offline_disk",


                "sgtn_offline_external",

                "sgtn_offline_external_de",

                "sgtn_offline_internal",

                "sgtn_offline_internal_native",

                "sgtn_offline_internal_properties",


                "sgtn_online_only",

                "sgtn_online_only_component",

                "sgtn_online_with_internal",

                "sgtn_online_with_external",

                "sgtn_sample_comment",


                "singleton_config"
            };

            int index = 9;
            Console.WriteLine(index);
            Console.WriteLine(configNames[index]);

            IConfig cfg = I18N.LoadConfig(
                Values.BASE_RES_NAME, Values.assembly, configNames[index]);
            rel = I18N.GetRelease(cfg);
        }

        public static int GetCount()
        {
            return count;
        }

        public static void CountDown()
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

        public static ISource Source(string component, string key, string source = null, string comment = null)
        {
            return rel.GetTranslation().CreateSource(component, key, source, comment);
        }
    }
}
