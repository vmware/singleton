/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;


namespace CSharp
{
    [TestClass]
    public class GetStringPropertiesFile
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceAbout;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private String[] args;
        private IReleaseMessages PM;


        public GetStringPropertiesFile()
        {
            UtilAllFalse.Init();
            //Release = Util.Release();
            Translation = UtilAllFalse.Translation();
            PM = UtilAllFalse.Messages();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");
            SourceAbout = Translation.CreateSource("about", "about.message");
            //SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument");
            SourceHTMLTag = Translation.CreateSource("DefaultComponent", "message.url");
            SourceHTMLTagWithSource = Translation.CreateSource("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "+", "moto" };


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for existing language with 2 parameters")]
        public void GetLocaleMessages_ExistingLanguage_TwoParameters()
        {
            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceAbout);
            String result2 = Translation.GetString(Currentlocale1, SourceArgument);
            String result3 = Translation.GetString(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("zh-Hans transaltion1: {0}", result1);
            Assert.AreEqual("应用程序说明页。", result1);
            Assert.AreEqual("运算符'{0}'不支持属性'{1}'。", result2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);


            Translation.SetCurrentLocale("de");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultDE1 = Translation.GetString(Currentlocale2, SourceAbout);
            String resultDE2 = Translation.GetString(Currentlocale2, SourceArgument);
            String resultDE3 = Translation.GetString(Currentlocale2, SourceHTMLTagWithSource);
            Console.WriteLine("zh-Hans transaltion1: {0}", resultDE1);
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", resultDE1);
            Assert.AreEqual("Der Operator '{0}' unterstützt die Eigenschaft '{1}' nicht.", resultDE2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>Die geplante Wartung wurde gestartet. </strong></span></p><p>Wichtige Informationen zur Wartung finden Sie hier: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", resultDE3);




        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for existing language with Full parameters")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters()
        {

            Translation.SetCurrentLocale("zh-Hans");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("about", "about.message");
            //Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("zh-Hans").GetString("about", "about.title");
            Console.WriteLine("the cache:{0}",result7);

            String result2 = Translation.GetString("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            String result3 = Translation.GetString("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("应用程序说明页。", result1);
            Assert.AreEqual("运算符'{0}'不支持属性'{1}'。", result2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);

            Translation.SetCurrentLocale("de");
            String resultDE1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String resultDE2 = Translation.GetString("DefaultComponent", "message.argument");
            String resultDE3 = Translation.GetString("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            Console.WriteLine("full param transaltion: {0}", resultDE2);
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", resultDE1);
            Assert.AreEqual("Der Operator '{0}' unterstützt die Eigenschaft '{1}' nicht.", resultDE2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>Die geplante Wartung wurde gestartet. </strong></span></p><p>Wichtige Informationen zur Wartung finden Sie hier: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", resultDE3);
            Console.WriteLine("full param transaltion: {0}", result1);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for existing language with argument")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format()
        {

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            String result2 = Translation.Format(Currentlocale1, SourceArgument, args);
            String result3 = Translation.Format(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("zh-Hans transaltion1: {0}", result1);
            Assert.AreEqual("应用程序说明页。", result1);
            Assert.AreEqual("运算符'+'不支持属性'moto'。", result2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);


            Translation.SetCurrentLocale("de");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultDE1 = Translation.Format(Currentlocale2, SourceAbout, "aaa");
            String resultDE2 = Translation.Format(Currentlocale2, SourceArgument, "+", "moto");
            String resultDE3 = Translation.Format(Currentlocale2, SourceHTMLTagWithSource);
            Console.WriteLine("DE transaltion1: {0}", resultDE1);
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", resultDE1);
            Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultDE2);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>Die geplante Wartung wurde gestartet. </strong></span></p><p>Wichtige Informationen zur Wartung finden Sie hier: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", resultDE3);




        }

        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation for existing language with argument object null")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format_objectnull()
        {

            //Translation.SetCurrentLocale("en");
            //String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format("zh-Hans", SourceAbout, null);
            //try {
            //    String result1 = Translation.Format(Currentlocale1, SourceAbout, null);
            //}
            //catch
            //{
            //    Console.WriteLine("Exception:The object reference is not set to an instance of the object");
            //}
            //try
            //{
            //    String result2 = Translation.Format(Currentlocale1, SourceArgument, null);
            //}
            //catch
            //{
            //    Console.WriteLine("Exception:The object reference is not set to an instance of the object");
            //}
            //String result1 = Translation.Format(Currentlocale1, SourceAbout, null);
            //String result2 = Translation.Format(Currentlocale1, SourceArgument,null);
           // String result3 = Translation.Format(Currentlocale1, SourceHTMLTagWithSource);
           // Console.WriteLine("zh-Hans transaltion1: {0}", result2);
            //Assert.AreEqual("应用程序说明页。", result1);
           // Assert.AreEqual("运算符'+'不支持属性'moto'。", result2);
            //Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);


            //Translation.SetCurrentLocale("de");
            //String Currentlocale2 = Translation.GetCurrentLocale();
            //String resultDE1 = Translation.Format(Currentlocale2, SourceAbout, "aaa");
            //String resultDE2 = Translation.Format(Currentlocale2, SourceArgument, "+", "moto");
            //String resultDE3 = Translation.Format(Currentlocale2, SourceHTMLTagWithSource);
            //Console.WriteLine("DE transaltion1: {0}", resultDE1);
            //Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", resultDE1);
            //Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultDE2);
            //Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>Die geplante Wartung wurde gestartet. </strong></span></p><p>Wichtige Informationen zur Wartung finden Sie hier: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", resultDE3);




        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("1.get translation by Argument_Format with parameter object Index greater than parameter")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format_objectIndex_greater_parameter()
        {

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
           // try
            //{
            String result2 = Translation.Format(Currentlocale1, SourceArgument,12);
            //String result2 = Translation.Format(Currentlocale1, SourceArgument,12);
            Console.WriteLine(result2);

            //}
            //catch
            //{
            //    Console.WriteLine("Exception:The index (starting from zero) must be greater than or equal to zero and smaller than the size of the parameter list.");
            //}
        }

        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation for fallback language")]
        //public void GetLocaleMessagesForFallbackLanguage_Bug_2280()
        //{
        //    //Source = Translation.CreateSource("about", "about.message");

            //    //String Currentlocale = Translation.GetCurrentLocale();
            //    //Console.WriteLine(Currentlocale);

            //    String resultzhCN = Translation.GetString("zh-CN", SourceAbout);
            //    Console.WriteLine("zh-CN transaltion: {0}", resultzhCN);
            //    Assert.AreEqual("应用程序说明页。", resultzhCN);
            //    Translation.SetCurrentLocale("zh-CN");
            //    String resultzhCNFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //    Assert.AreEqual("应用程序说明页。", resultzhCNFull);
            //    String resultzhCNFormat = Translation.Format("zh-CN", SourceArgument, "+", "moto");
            //    Assert.AreEqual("运算符'+'不支持属性'moto'。", resultzhCNFormat);


            //    String resultfrCA = Translation.GetString("fr-CA", SourceAbout);
            //    Console.WriteLine("fr-CA transaltion: {0}", resultfrCA);
            //    Assert.AreEqual("La page Description de l'application.", resultfrCA);
            //    Translation.SetCurrentLocale("fr-CA");
            //    String resultfrCAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //    Assert.AreEqual("La page Description de l'application.", resultfrCAFull);
            //    String resultfrFormat = Translation.Format("fr-CA", SourceArgument, args);
            //    Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultfrFormat);
            //}


            //[TestMethod]
            //[Priority(1)]
            //[TestCategory("")]
            //[Description("Get translation with upper case language")]
            //public void GetLocaleMessages_UpperCaseLanguage_FR_Bug_2287()
            //{

            //    String resultFR = Translation.GetString("FR", SourceAbout);
            //    Console.WriteLine("FR transaltion: {0}", resultFR);
            //    Assert.AreEqual("La page Description de l'application.", resultFR);
            //    Translation.SetCurrentLocale("FR");
            //    String resultFRFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            //    Console.WriteLine("FR transaltion: {0}", resultFRFull);
            //    Assert.AreEqual("La page Description de l'application.", resultFRFull);
            //    String resultFRFormat = Translation.Format("FR", SourceArgument, args);
            //    Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultFRFormat);


            //}


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for nonexistent language")]
        public void GetLocaleMessages_nonexistent_Language()
        {
            

            String resultDA = Translation.GetString("da", SourceAbout);
            Console.WriteLine("DA transaltion: {0}", resultDA);
            Assert.AreEqual("Your application description page.", resultDA);
            Translation.SetCurrentLocale("da");
            String resultDAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            Assert.AreEqual("Your application description page.", resultDAFull);
            String resultDAFormat = Translation.Format("da", SourceArgument, "+", "moto");
            Assert.AreEqual("Operator '+' is not support for property 'moto'.", resultDAFormat);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for invalid language")]
        public void GetLocaleMessages_Invaild_Language()
        {


            String resultDA = Translation.GetString("da1", SourceAbout);
            Console.WriteLine("DA transaltion: {0}", resultDA);
            Assert.AreEqual("Your application description page.", resultDA);
            Translation.SetCurrentLocale("da1");
            String resultDAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            Assert.AreEqual("Your application description page.", resultDAFull);
            String resultDAFormat = Translation.Format("da1", SourceArgument, "+", "moto");
            Assert.AreEqual("Operator '+' is not support for property 'moto'.", resultDAFormat);

        }



        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for empty language")]
        public void GetLocaleMessages_Empty_Language()
        {


            String resultDA = Translation.GetString("", SourceAbout);
            Console.WriteLine("DA transaltion: {0}", resultDA);
            Assert.AreEqual("Your application description page.", resultDA);
            Translation.SetCurrentLocale("");
            String resultDAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            Assert.AreEqual("Your application description page.", resultDAFull);
            String resultDAFormat = Translation.Format("", SourceArgument, "+", "moto");
            Assert.AreEqual("Operator '+' is not support for property 'moto'.", resultDAFormat);

        }

        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation for null language")]
        //public void GetLocaleMessages_Null_Language()
        //{


        //    String resultDA = Translation.GetString(null, SourceAbout);
        //    Console.WriteLine("DA transaltion: {0}", resultDA);
        //    Assert.AreEqual("Your application description page.", resultDA);
        //    Translation.SetCurrentLocale(null);
        //    String resultDAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
        //    Assert.AreEqual("Your application description page.", resultDAFull);
        //    String resultDAFormat = Translation.Format(null, SourceArgument, "+", "moto");
        //    Assert.AreEqual("Operator '+' is not support for property 'moto'.", resultDAFormat);

        //}

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for null key")]
        public void GetLocaleMessages_Null_key_bug_2307()
        {


            String resultDA = Translation.GetString("zh-CN", null);
            Console.WriteLine("DA transaltion: {0}", resultDA);
            Assert.AreEqual(null, resultDA);
            Translation.SetCurrentLocale("zh-CN");
            String resultDAFull = Translation.GetString("about", null, "Your application description page.", "this is comment.");
            Assert.AreEqual(null, resultDAFull);
            String resultDAFormat = Translation.Format("zh-CN", null, "+", "moto");
            Assert.AreEqual(null, resultDAFormat);


        }
        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for nonexistent source")]
        public void GetLocaleMessages_nonexistentsource()
        {


            //String resultDA = Translation.GetString("zh-CN", null);
            //Console.WriteLine("DA transaltion: {0}", resultDA);
            //Assert.AreEqual(null, resultDA);
            Translation.SetCurrentLocale("zh-CN");
            String resultDAFull = Translation.GetString("about", "dddd", "Your application description page123.", "this is comment.");
            Console.WriteLine("DA transaltion: {0}", resultDAFull);
            Assert.AreEqual("Your application description page123.", resultDAFull);
            //String resultDAFormat = Translation.Format("zh-CN", null, "+", "moto");
            //Assert.AreEqual(null, resultDAFormat);


        }
        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translatio for nonexistent key and no value")]
        public void GetLocaleMessages_nonexistentkey()
        {


            //String resultDA = Translation.GetString("zh-CN", null);
            //Console.WriteLine("DA transaltion: {0}", resultDA);
            //Assert.AreEqual(null, resultDA);
            Translation.SetCurrentLocale("zh-CN");
            String resultDAFull = Translation.GetString("about", "dddddd");
            Console.WriteLine("DA transaltion: {0}", resultDAFull);
            Assert.AreEqual("dddddd", resultDAFull);
            //String resultDAFormat = Translation.Format("zh-CN", null, "+", "moto");
            //Assert.AreEqual(null, resultDAFormat);


        }



        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation for ISource without source param about specail string")]
        public void GetLocaleMessages_ExistingLanguage_SpecailString_Bug_2257()
        {

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceHTMLTag);
            Console.WriteLine("zh-Hans transaltion1: {0}", result1);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result1);
            String result2 = Translation.GetString("DefaultComponent", "message.url");
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result2);
            String result3 = Translation.Format(Currentlocale1, SourceHTMLTag);
            Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);


        }

    }
}