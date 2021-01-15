/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using System;
using System.Reflection;

namespace Product2ResLib
{
    class Values
    {
        public static string RES_NAME = "SingletonRes.Singleton";
        public static Assembly assembly = typeof(Values).Assembly;
    }

    public class Util2
    {
        private static IRelease rel;
        private static int count;

        public static void Init(int index)
        {
            count = 1;
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

                "sgtn_online_with_external",

                "sgtn_online_with_internal",

                "sgtn_sample_comment",


                "sgtn_sample"
            };

            Console.WriteLine("--- config --- " + index + " --- " + configNames[index]);

            IConfig cfg = I18N.LoadConfig(
                Values.RES_NAME, Values.assembly, configNames[index]);
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
