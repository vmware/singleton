using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class Offline_internal_resx
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private ISource Sourcetest;
        private ISource Sourcetest1;
        private IConfig CM;
        private String[] args;


        public Offline_internal_resx()
        {
            Utiloffline_Internal_Resx.Init();
            //Release = Util.Release();
            Translation = Utiloffline_Internal_Resx.Translation();
            CM = Utiloffline_Internal_Resx.Config();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");

            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");
            Sourcetest1 = Translation.CreateSource("about", "about.message");
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for exist component,local and key")]
        public void GetLocaleMessagesofflineresx1()
        {
            Translation.SetCurrentLocale("fr");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, Sourcetest);
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("f111Add {0} to the object.", result1);


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for no exist component")]
        public void GetLocaleMessagesofflineresx2()
        {

            List<string> LocaleList = CM.GetLocaleList("");
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);
            Translation.SetCurrentLocale("ko");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("noesistcomponent", "RESX.ARGUMENT");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("RESX.ARGUMENT", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for local no exist and local is't default_locale")]
        public void GetLocaleMessagesofflineresx3()
        {

            Translation.SetCurrentLocale("da");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest,"obj");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("d111Add obj to the object.", result1);


        }



        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key no exist")]
        public void GetLocaleMessagesofflineresx4()
        {

            Translation.SetCurrentLocale("es");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.no_existkey");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("RESX.no_existkey", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key only exist in en and ont exist in other locale")]
        //online mode messages_source.json no set
        public void GetLocaleMessagesofflineresx5()
        {

            Translation.SetCurrentLocale("zh-Hans");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "Onlysource");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("test source", result1);


        }
    }
}