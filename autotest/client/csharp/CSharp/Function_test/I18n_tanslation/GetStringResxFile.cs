/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class GetStringResxFile
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceAbout;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private ISource Sourcetest;
        private ISource Sourcetest1;
        private String[] args;


        public GetStringResxFile()
        {
            UtilAllFalse.Init();
            //Release = Util.Release();
            Translation = UtilAllFalse.Translation();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");
            SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object.", "argument verification");
            //SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            SourceHTMLTag = Translation.CreateSource("RESX", "Resx-message.URL");
            SourceHTMLTagWithSource = Translation.CreateSource("RESX", "Resx-message.URL", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "1" };
            Sourcetest = Translation.CreateSource("ResourceResx", "RESX.ARGUMENT");
            Sourcetest1 = Translation.CreateSource("about","about.message");
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for existing language with 2 parameters")]
        public void GetLocaleMessages2_ExistingLanguage_TwoParameters()
        {
            Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceError);
            String result2 = Translation.GetString(Currentlocale1, SourceArgument);
            String result3 = Translation.GetString(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("ja transaltion1: {0}", result1);
            Assert.AreEqual("連絡先ページ。", result1);
            Assert.AreEqual("オブジェクトに{0}を追加します。", result2);
            Assert.AreEqual(TestDataConstant.valueURLja, result3);


            Translation.SetCurrentLocale("fr");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultFR1 = Translation.GetString(Currentlocale2, SourceError);
            String resultFR2 = Translation.GetString(Currentlocale2, SourceArgument);
            String resultFR3 = Translation.GetString(Currentlocale2, SourceHTMLTagWithSource);
            Console.WriteLine("fr transaltion1: {0}", resultFR1);
            Assert.AreEqual("Votre page de contact.", resultFR1);
            Assert.AreEqual("Ajoutez {0} à l'objet.", resultFR2);
            Assert.AreEqual(TestDataConstant.valueURLfr, resultFR3);


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for existing language with Full parameters")]
        public void GetLocaleMessages2_ExistingLanguage_FullParameters()
        {

            Translation.SetCurrentLocale("ja");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", TestDataConstant.keyError, TestDataConstant.valueError);
            String result2 = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            String result3 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("連絡先ページ。", result1);
            Assert.AreEqual("オブジェクトに{0}を追加します。", result2);
            Assert.AreEqual(TestDataConstant.valueURLja, result3);


            Translation.SetCurrentLocale("fr");
            String resultFR1 = Translation.GetString("RESX", TestDataConstant.keyError, null);
            String resultFR2 = Translation.GetString("RESX", TestDataConstant.keyArg, null, null);
            String resultFR3 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
            Console.WriteLine("fr transaltion1: {0}", resultFR1);
            Assert.AreEqual("Votre page de contact.", resultFR1);
            Assert.AreEqual("Ajoutez {0} à l'objet.", resultFR2);
            Assert.AreEqual(TestDataConstant.valueURLfr, resultFR3);
        }



        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for existing language with argument")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format()
        {

            Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceError, "aaa");
            String result2 = Translation.Format(Currentlocale1, SourceArgument,args);
            String result3 = Translation.Format(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("ja format transaltion: {0}", result1);
            Assert.AreEqual("連絡先ページ。", result1);
            Assert.AreEqual("オブジェクトに1を追加します。", result2);
            Assert.AreEqual(TestDataConstant.valueURLja, result3);


            Translation.SetCurrentLocale("fr");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultFR1 = Translation.Format(Currentlocale2, SourceError, "aaa");
            String resultFR2 = Translation.Format(Currentlocale2, SourceArgument, "1");
            String resultFR3 = Translation.Format(Currentlocale2, SourceHTMLTagWithSource);
            Console.WriteLine("fr format transaltion1: {0}", resultFR1);
            Assert.AreEqual("Votre page de contact.", resultFR1);
            Assert.AreEqual("Ajoutez 1 à l'objet.", resultFR2);
            Assert.AreEqual(TestDataConstant.valueURLfr, resultFR3);



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

        //    String resultzhCN = Translation.GetString("zh-CN", SourceError);
        //    Console.WriteLine("zh-CN transaltion: {0}", resultzhCN);
        //    Assert.AreEqual("您的联系页面。", resultzhCN);
        //    Translation.SetCurrentLocale("zh-CN");
        //    String resultzhCNFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
        //    Assert.AreEqual("将{0}添加到对象。", resultzhCNFull);
        //    String resultzhCNFormat = Translation.Format("zh-CN", SourceArgument, "1");
        //    Assert.AreEqual("将1添加到对象。", resultzhCNFormat);


        //    String resultfrCA = Translation.GetString("fr-CA", SourceError);
        //    Console.WriteLine("fr-CA transaltion: {0}", resultfrCA);
        //    Assert.AreEqual("Votre page de contact.", resultfrCA);
        //    Translation.SetCurrentLocale("fr-CA");
        //    String resultfrCAFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
        //    Assert.AreEqual("Ajoutez {0} à l'objet.", resultfrCAFull);
        //    String resultfrFormat = Translation.Format("fr-CA", SourceArgument, args);
        //    Assert.AreEqual("Ajoutez 1 à l'objet.", resultfrFormat);
        //}


        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation with nonexistent language")]
        public void GetLocaleMessages_nonexistentLanguage_da()
        {

            //String resultDA = Translation.GetString("da", SourceError);
            //Console.WriteLine("da transaltion: {0}", resultDA);
            //Assert.AreEqual(TestDataConstant.valueError, resultDA);
            Translation.SetCurrentLocale("da");
            String resultDAFull = Translation.GetString("RESX", TestDataConstant.keyArg);
            //String resultDAFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            Assert.AreEqual("RESX.ARGUMENT", resultDAFull);
            String resultDAFormat = Translation.Format("da", SourceArgument, "1");
            Assert.AreEqual("Add 1 to the object.", resultDAFormat);


        }

        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation with invalid language")]
        public void GetLocaleMessages_InvalidLanguage_abc()
        {
            Translation.SetCurrentLocale("dac");
            String Currentlocale1 = Translation.GetCurrentLocale();
            Console.WriteLine(Currentlocale1);
            String resultABC = Translation.GetString("abc", SourceError);
            Console.WriteLine("abc transaltion: {0}", resultABC);
            Assert.AreEqual("Ihre Kontaktseite.", resultABC);
            Translation.SetCurrentLocale("abc");
            String resultABCFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", resultABCFull);
            String resultABCFormat = Translation.Format("abc", SourceArgument, "1");
            Assert.AreEqual("Fügen Sie dem Objekt 1 hinzu.", resultABCFormat);


        }


        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation with upper case language")]
        //public void GetLocaleMessages_UpperCaseLanguage_FR_Bug_2287()
        //{

        //    String resultFR = Translation.GetString("FR", SourceError);
        //    Console.WriteLine("FR transaltion: {0}", resultFR);
        //    Assert.AreEqual("Votre page de contact.", resultFR);
        //    Translation.SetCurrentLocale("FR");
        //    String resultFRFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
        //    Assert.AreEqual("Ajoutez {0} à l'objet.", resultFRFull);
        //    String resultFRFormat = Translation.Format("FR", SourceArgument, "1");
        //    Assert.AreEqual("Ajoutez 1 à l'objet.", resultFRFormat);


        //}


        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation with nonexistent component")]
        public void GetLocaleMessages_nonexistentComponent()
        {

            
            Translation.SetCurrentLocale("zh-CN");
            String resultDAFull = Translation.GetString("nonexistentComponent", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            Assert.AreEqual(TestDataConstant.valueArg, resultDAFull);
            


        }

        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation for ISource without source param about specail string")]
        public void GetLocaleMessages_ExistingLanguage_SpecailString()
        {

            Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceHTMLTag);
            Console.WriteLine("ja transaltion1: {0}", result1);
            Assert.AreEqual(TestDataConstant.valueURLja, result1);
            String result2 = Translation.GetString("RESX", TestDataConstant.keyURL);
            Assert.AreEqual(TestDataConstant.valueURLja, result2);
            String result3 = Translation.Format(Currentlocale1, SourceHTMLTag);
            Assert.AreEqual(TestDataConstant.valueURLja, result3);


        }

    }
}