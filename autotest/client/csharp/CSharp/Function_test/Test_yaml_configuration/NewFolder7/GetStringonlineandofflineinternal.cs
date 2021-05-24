using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;

namespace CSharp
{
    [TestClass]
    public class Online_with_offline_internal
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private ISource Sourcetest;
        private ISource Sourcetest1;
        private IReleaseMessages PM;
        private IConfig CM;
        private Dictionary<string, ILocaleMessages> AllTranslation;
        private String[] args;


        public Online_with_offline_internal()
        {
            Utilonline_with_internal.Init();
            //Release = Util.Release();
            PM = Utilonline_with_internal.Messages();
            CM = Utilonline_with_internal.Config();
            Translation = Utilonline_with_internal.Translation();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");
            AllTranslation = PM.GetAllLocaleMessages();
            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");
            Sourcetest1 = Translation.CreateSource("about", "about.message");
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation when the locale both exist in online bundle and offline bundle ")]
        public void GetlocaleMessagesonline_with_offline_internal1()
        {

            Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("オブジェクトに{0}を追加します。", result1);
            String result6 = PM.GetLocaleMessages("ja").GetString("RESX", "RESX.ARGUMENT");
            String result2 = Translation.GetString("de", Sourcetest);
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result2);
            String result7 = PM.GetLocaleMessages("de").GetString("RESX", "RESX.ARGUMENT");




        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation when the locale only exist in online bundle")]
        public void GetlocaleMessagesonline_with_offline_internal2()
        {

            Translation.SetCurrentLocale("fr");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest, "XXX");
            String result2 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result1);
            Console.WriteLine("full param transaltion: {0}", result2);
            Assert.AreEqual("Ajoutez XXX à l'objet.", result1);
            Assert.AreEqual("La page Description de l'application.", result2);

        }

        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the locale only exist in offline bundle")]
        public void GetlocaleMessagesonline_with_offline_internal3_BUG1056()
        {
            Translation.SetCurrentLocale("ru");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("r222Add {0} to the object.", result1);
            String result9 = PM.GetLocaleMessages("ru").GetString("RESX", "RESX.ARGUMENT");
            List<string> localelist = PM.GetLocaleList();
            String result2 = Common.ParseListStringContent(localelist);
            Console.WriteLine("translation in cache: {0}", result9);
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation when the locale no exist in online bundle and offline bundle and it's not the default language ")]
        public void GetlocaleMessagesonline_with_offline_internal4()
        {

            Translation.SetCurrentLocale("da");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest,"test");
            Assert.AreEqual("Fügen Sie dem Objekt test hinzu.", result1);
            List<string> LocaleList = PM.GetLocaleList();
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);

        }


        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the key only exist in offline bundle")]
        public void GetlocaleMessagesonline_with_offline_internal5()
        {
            List<string> LocaleList = CM.GetLocaleList("");
            String result = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result);
            List<string> LocaleList1 = CM.GetLocaleList("123");
            String result1 = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result1);
            List<string> LocaleList2 = CM.GetLocaleList("RESX");
            String result2 = Common.ParseListStringContent(LocaleList);
            Console.WriteLine(result2);

            Translation.SetCurrentLocale("de");

            String result4 = Translation.GetString("RESX", "Resx.only-offline");
            Assert.AreEqual("test value offline", result4);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the key  only exist in online bundle")]
        public void GetlocaleMessagesonline_with_offline_internal6()
        {

            Translation.SetCurrentLocale("ja");
            String result1 = Translation.GetString("RESX", "RESX.only-online");
            Assert.AreEqual("j3333only in online", result1);
         

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the key both no exist in online bundle and offline bundle")]
        public void GetlocaleMessagesonline_with_offline_internal7()
        {

            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.kkkk");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("RESX.kkkk", result1);

        }

        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the component only exist offline bundle")]
        public void GetlocaleMessagesonline_with_offline_internal8_Undetermined()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("ResourceResx", "RESX.ARGUMENT");
            Assert.AreEqual("111Add {0} to the object.", result1);
        }



        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter not exist when RESX.resx is not equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_internal_compare9()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Assert.AreEqual("Your contact page.not equal", result1);
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter exist when source parameter is equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_internal_compare10()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT", "Add {0} to the object.");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result1);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter exist when source parameter is not equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_internal_compare11()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT", "Add {0} to the object.1");
            Assert.AreEqual("Add {0} to the object.1", result1);
        }

    }
}