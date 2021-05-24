using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;

namespace CSharp
{
    [TestClass]
    public class Online_with_offline_external
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
        private IReleaseMessages PM;
        private String[] args;


        public Online_with_offline_external()
        {
            Utilonline_with_external.Init();
            //Release = Util.Release();
            PM = Utilonline_with_external.Messages();
            Translation = Utilonline_with_external.Translation();
            CM = Utilonline_with_external.Config();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");

            //args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");
            Sourcetest1 = Translation.CreateSource("about", "about.title");//about123
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the locale both exist in online bundle and offline bundle ")]
        public void GetlocaleMessagesonline_with_offline_external1()
        {

            Translation.SetCurrentLocale("de");
            String Currentlocale1 = Translation.GetCurrentLocale();  
            String result1 = Translation.GetString(Currentlocale1, Sourcetest1);
            Assert.AreEqual("Über", result1);
            String result2 = PM.GetLocaleMessages("de").GetString("about", "about.title");
            String result3 = Translation.GetString("about", "about.title");
            Assert.AreEqual("Über", result3);
            String result4 = PM.GetLocaleMessages("de").GetString("about", "about.title");

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the locale only exist in online bundle")]
        public void GetlocaleMessagesonline_with_offline_external2()
        {
            Translation.SetCurrentLocale("ko");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT", "Add {0} to the object.");
            Assert.AreEqual("오브젝트에 {0}을 (를) 추가하십시오.", result1);
        }

        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the locale only exist in offline bundle")]
        public void GetlocaleMessagesonline_with_offline_external3_bug1056()
        {

            Translation.SetCurrentLocale("ar");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("إضافة {0} إلى الكائن.", result1);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the locale not exist in online bundle and offline bundle and it's not the default language ")]
        public void GetlocaleMessagesonline_with_offline_external4()
        {

            Translation.SetCurrentLocale("da");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest, "test");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("Fügen Sie dem Objekt test hinzu.", result1);

        }


        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the key only exist in offline bundle")]
        public void GetlocaleMessagesonline_with_offline_external5()
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
            Console.WriteLine("full param transaltion: {0}", result4);
            Assert.AreEqual("Resx.only-offline", result4);

        }

        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the key only exist in online bundle")]
        public void GetlocaleMessagesonline_with_offline_external6()
        {

            Translation.SetCurrentLocale("ja");
            String result1 = Translation.GetString("RESX", "RESX.only-online");
            Assert.AreEqual("j3333only in online", result1);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation when the key both no exist in online bundle and offline bundle")]
        public void GetlocaleMessagesonline_with_offline_external7()
        {

            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.kkkk");
            Assert.AreEqual("RESX.kkkk", result1);

        }

        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get translation when the component both only exist offline bundle")]
        public void GetlocaleMessagesonline_with_offline_external8_Undetermined()
        {

            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESXoffline", "RESX.ARGUMENT");
            Assert.AreEqual("dddFügen Sie dem Objekt {0} hinzu.", result1);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter not exist when RESX.resx is not equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_external_compare9()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Assert.AreEqual("Your contact page not equal.", result1);
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter exist when source parameter is equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_external_compare10()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT", "Add {0} to the object.");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result1);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("source parameter exist when source parameter is not equal message_en.json in online bundle")]
        public void GetlocaleMessagesonline_with_offline_external_compare11()
        {
            Translation.SetCurrentLocale("de");
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT", "Add {0} to the object.1");
            Assert.AreEqual("Add {0} to the object.1", result1);
        }
    }
}