using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;

namespace CSharp
{
    [TestClass]
    public class Offline_internal_properties
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private ISource Sourcetest;
        private ISource Sourcetestonlysource;
        private IReleaseMessages PM;
        private IConfig CM;
        private String[] args;


        public Offline_internal_properties()
        {
            Utiloffline_internal_properties.Init();
            //Release = Util.Release();
            PM = Utiloffline_internal_properties.Messages();
            Translation = Utiloffline_internal_properties.Translation();
            CM = Utiloffline_internal_properties.Config();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");

            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("about", "about.message");
            Sourcetestonlysource = Translation.CreateSource("about", "about.onlysource");
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for exist component,local and key")]
        public void GetLocaleMessagesofflineproperties1()
        {
            Translation.SetCurrentLocale("zh-Hant");
            string xx = Translation.GetCurrentLocale();
            Console.WriteLine("default: {0}", xx);
            String result1 = Translation.GetString("contact", "contact.support");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("zzzSupport:", result1);
            List<string> LocaleList = PM.GetLocaleList();
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine("locale list {0}", result);
            List<string> componentlist = PM.GetComponentList();
            String result2 = Common.ParseListStringContent(componentlist);
            Console.WriteLine("locale list {0}", result2);
            String result5 = PM.GetLocaleMessages("zh-Hant").GetString("contact", "about.title");
            Console.WriteLine(result5);

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for no exist component")]
        public void GetLocaleMessagesofflineproperties2()
        {

            List<string> LocaleList = CM.GetLocaleList("");
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);
            Translation.SetCurrentLocale("ko");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("noesistcomponent", "about.message");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("about.message", result1);


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for local no exist and local is't default_locale")]
        public void GetLocaleMessagesofflineproperties3_bug856()
        {
            Translation.SetCurrentLocale("da");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result2 = Translation.GetString("about", "about.message");
            //String result1 = Translation.GetString(Currentlocale1, Sourcetest);
            Console.WriteLine("full param transaltion: {0}", result2);
            Assert.AreEqual("222Your application description page.", result2);


        }



        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key no exist")]
        public void GetLocaleMessagesofflineproperties5()
        {

            Translation.SetCurrentLocale("ja");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("about", "no_exist_key");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("no_exist_key", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key only exist in en and not exist in other locale")]
        //online mode messages_source.json no set
        public void GetLocaleMessagesofflineproperties6()
        {

            Translation.SetCurrentLocale("my");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetestonlysource,"ppp");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("only ppp source exist", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key only exist in en and not exist in other locale")]
        //online mode messages_source.json no set
        public void GetLocaleMessagesofflineproperties7_bug674()
        {

            Translation.SetCurrentLocale("fr");
            String result1 = Translation.GetString("about", "about.title");
            String result2 = PM.GetLocaleMessages("fr").GetString("about", "contact.address");
            Console.WriteLine("full param transaltion: {0}", result1);
            Console.WriteLine("full param transaltion: {0}", result2);
            Assert.AreEqual("fff1About", result1);
            Assert.AreEqual(null, result2);
        }
    }
}