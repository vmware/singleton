/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using System.Reflection;

namespace UnitTestSingleton
{
    public class Util
    {
        private static IRelease rel;

        public static void Init()
        {
            IConfig cfg = I18N.LoadConfig(
                "res.Singleton", Assembly.GetExecutingAssembly(), "singleton_config");
            rel = I18N.GetRelease(cfg);
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

