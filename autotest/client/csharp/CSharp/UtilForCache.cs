﻿
using SingletonClient;
using System.Reflection;



namespace CSharp
{
    class ValuesForCache
    {
        private static string nameSpace =
            System.Reflection.MethodBase.GetCurrentMethod().DeclaringType.Namespace;
        public static string BASE_RES_NAME = nameSpace + ".Resources.Resource";
        public static Assembly assembly = typeof(ValuesForCache).Assembly;
    }

    public class UtilForCache
    {
        private static IRelease rel;
        private static int count = 1;

        public static void Init()
        {
            IConfig cfg = I18n.LoadConfig(
                ValuesForCache.BASE_RES_NAME, ValuesForCache.assembly, "SingletonForCache");
            rel = I18n.GetRelease(cfg);
        }


        public static IConfig Config()
        {
            return rel.GetConfig();
        }

        public static IExtension Extension()
        {
            return I18n.GetExtension();
        
        }

        public static IRelease Release()
        {
            return rel;
        }

        public static IProductMessages Messages()
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

