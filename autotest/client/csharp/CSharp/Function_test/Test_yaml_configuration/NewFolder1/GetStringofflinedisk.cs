using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class Offline_external_disk
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


        public Offline_external_disk()
        {
            Utiloffline_disk.Init();
            //Release = Util.Release();
            Translation = Utiloffline_disk.Translation();
            CM = Utiloffline_disk.Config();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");

            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");
            Sourcetest1 = Translation.CreateSource("about", "about.message");
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for exist component,local and key")]
        public void GetLocaleMessagesofflinedisk1()
        {
            //Thread.Sleep(40000);
            Translation.SetCurrentLocale("fr");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            //String result1 = Translation.GetString(Currentlocale1, Sourcetest1);
            String result1 = Translation.GetString("about", "about.message", "Your application description page1.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("La page Description de l'application.", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for no exist component")]
        public void GetLocaleMessagesofflinedisk2()
        {

            List<string> LocaleList = CM.GetLocaleList("");
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);
            Translation.SetCurrentLocale("ko");
            String result1 = Translation.GetString("noesistcomponent", "about.message");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("about.message", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for local no exist and local is't default_locale")]
        public void GetLocaleMessagesofflinedisk3()
        {

            Translation.SetCurrentLocale("da");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest,"obj");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("ddFügen Sie dem Objekt obj hinzu.", result1);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for local ")]
        //default local = source local issure
        public void GetLocaleMessagesofflinedisk4()
        {

            Translation.SetCurrentLocale("da");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, Sourcetest);
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("ddFügen Sie dem Objekt {0} hinzu.", result1);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key no exist")]
        public void GetLocaleMessagesofflinedisk5()
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
        [Description("Get translation for key only exist in en and no exist in other locale--5-17--default locale：en-US？")]
        //online mode messages_source.json no set
        public void GetLocaleMessagesofflinedisk6()
        {

            Translation.SetCurrentLocale("zh-Hans");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("contact", "contact.only_exist_insource");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("only in source", result1);


        }

    }
}