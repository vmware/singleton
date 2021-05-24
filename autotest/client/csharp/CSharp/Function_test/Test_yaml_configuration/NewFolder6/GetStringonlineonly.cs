using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;
using System.Diagnostics;

namespace CSharp
{
    [TestClass]
    public class Online_only
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


        public Online_only()
        {
            Utilonline_only.Init();
            //Release = Util.Release();
            Translation = Utilonline_only.Translation();
            CM = Utilonline_only.Config();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");

            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");
            //Sourcetest1 = Translation.CreateSource("about", "about.message");
        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for exist component,local and key")]
        public void GetLocaleMessagesOnline1()
        {
            //Thread.Sleep(4000);
            Translation.SetCurrentLocale("ja");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            Console.WriteLine(Currentlocale1);
            string result1 = Translation.GetString("about", "about.message");
            string result2 = Translation.GetString("contact", "contact.title");
            string result3 = Translation.GetString("about", "about.message", "your application description page.", "this is comment.");
            string result4 = Translation.GetString("resx", "resx.argument", "add {0} to the object.");
            //string result4 = translation.format(currentlocale1, sourcetest, "aa");
            Console.WriteLine("full param transaltion: {0}", result1);
            Console.WriteLine("full param transaltion2: {0}", result2);
            Console.WriteLine("full param transaltion2: {0}", result3);
            Console.WriteLine("full param transaltion4: {0}", result4);
            Assert.AreEqual("アプリケーションの説明ページ。", result1);



        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for no exist component")]
        public void GetLocaleMessagesOnline2()
        {

            List<string> LocaleList = CM.GetLocaleList("");
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);
            Translation.SetCurrentLocale("fr");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("xxxabout", "about.message");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Assert.AreEqual("about.message", result1);

         
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for local no exist and local is't default_locale")]
        public void GetLocaleMessagesOnline3()
        {

            Translation.SetCurrentLocale("da");
            //Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, Sourcetest);
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result1);


        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for key no exist")]
        public void GetLocaleMessagesOnline5()
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
        [Description("Get translation for key only exist in en and not exist in other locale")]
        //online mode messages_source.json no set
        public void GetLocaleMessagesOnline6()
        {

            Translation.SetCurrentLocale("zh-Hans");
            //Translation.SetCurrentLocale("ja");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.only-online");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT", "Add {0} to the object.");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("RESX.only-online", result1);


        }

    }
}