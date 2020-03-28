
using SingletonClient;
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
            IConfig cfg = I18n.LoadConfig(
                Values.BASE_RES_NAME, Values.assembly, "singleton_config");
            rel = I18n.GetRelease(cfg);
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
