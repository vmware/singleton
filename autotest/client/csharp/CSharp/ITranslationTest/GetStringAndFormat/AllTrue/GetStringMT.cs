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
    public class GetStringAllTrue
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private String[] args;

        //Return MT translation
        public GetStringAllTrue()
        {
            UtilAllTrue.Init();
            //Release = Util.Release();
            Translation = UtilAllTrue.Translation();
            //from resx file
            SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object.", "argument verification");
            //from resx file
            SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            //from resx file
            SourceHTMLTag = Translation.CreateSource("RESX", "Resx-message.URL");
            //from properties file
            SourceHTMLTagWithSource = Translation.CreateSource("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "1" };


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get MT translation for language(en,fr) with 2 parameters")]
        public void Get_MT_ExistingLanguage_TwoParameters()
        {

            Translation.SetCurrentLocale("en");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceError);
            String result2 = Translation.GetString(Currentlocale1, SourceArgument);
            //String result4 = Translation.GetString(Currentlocale1, SourceHTMLTag);
            //String result3 = Translation.GetString(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("en MT: {0}", result1);
            Assert.AreEqual(TestDataConstant.valueError, result1);
            Assert.AreEqual(TestDataConstant.valueArg, result2);
            //Assert.AreEqual(TestDataConstant.valueURLPseudo, result4);
            //Assert.AreEqual(TestDataConstant.valueURLPPseudo, result3);



            Translation.SetCurrentLocale("es");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultFR1 = Translation.GetString(Currentlocale2, SourceError);
            String resultFR2 = Translation.GetString(Currentlocale2, SourceArgument);
            //String resultFR3 = Translation.GetString(Currentlocale2, SourceHTMLTagWithSource);
            //String resultFR4 = Translation.GetString(Currentlocale2, SourceHTMLTag);
            Console.WriteLine("ES MT: {0}", resultFR1);
            Console.WriteLine("ES MT: {0}", resultFR2);
            Assert.AreEqual("Su página de contacto.", resultFR1);
            Assert.AreEqual("Agregue {0} al objeto.", resultFR2);
            //Assert.AreEqual(TestDataConstant.valueURLPPseudo, resultFR3);
            //Assert.AreEqual(TestDataConstant.valueURLPseudo, resultFR4);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for language(ko,da) with Full parameters")]
        public void Get_MT_ExistingLanguage_FullParameters()
        {

            Translation.SetCurrentLocale("ko");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", TestDataConstant.keyError, TestDataConstant.valueError);
            String result2 = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            //String result3 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
            //String result4 = Translation.GetString("DefaultComponent", TestDataConstant.keyURLP, TestDataConstant.valueURLP);
            Console.WriteLine("full param MT: {0}", result1);
            Assert.AreEqual("연락처 페이지.", result1);
            Assert.AreEqual("개체에 {0} 추가합니다.", result2);
            //Assert.AreEqual(TestDataConstant.valueURLPseudo, result3);
            //Assert.AreEqual(TestDataConstant.valueURLPPseudo, result4);



        }

        //[TestMethod]
        //[Priority(2)]
        //[TestCategory("")]
        //[Description("Get translation for language(da) not in supportLanguageList with Full parameters")]
        //public void Get_MT_LanguageNotInSupportLanguageList_FullParameters_bug_2282()
        //{

            
        //    Translation.SetCurrentLocale("da");
        //    String resultDA1 = Translation.GetString("RESX", TestDataConstant.keyError, null);
        //    String resultDA2 = Translation.GetString("RESX", TestDataConstant.keyArg, null, null);

        //    Console.WriteLine("da transaltion1: {0}", resultDA1);
        //    Assert.AreEqual("Din kontakt side.", resultDA1);
        //    Assert.AreEqual("Føj {0} til objektet.", resultDA2);

        //}

        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get translation for invalid language(abc) with Full parameters")]
        public void Get_MT_InvalidLanguage_FullParameters()
        {

            
            Translation.SetCurrentLocale("abc");
            String resultABC1 = Translation.GetString("RESX", TestDataConstant.keyError, null);
            String resultABC2 = Translation.GetString("RESX", TestDataConstant.keyArg, null, null);

            Console.WriteLine("abc transaltion1: {0}", resultABC1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, resultABC1);
            Assert.AreEqual(TestDataConstant.valueArgPseudo, resultABC2);

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for existing language with argument")]
        public void Get_MT_ExistingLanguage_Argument_Format()
        {

            Translation.SetCurrentLocale("ko");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceError, "aaa");
            String result2 = Translation.Format(Currentlocale1, SourceArgument, args);
            //String result3 = Translation.Format(Currentlocale1, SourceHTMLTag);
            //String result4 = Translation.Format(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("ko format MT: {0}", result1);
            Assert.AreEqual("연락처 페이지.", result1);
            Assert.AreEqual("개체에 1 추가합니다.", result2);
            //Assert.AreEqual(TestDataConstant.valueURLPseudo, result3);
            //Assert.AreEqual(TestDataConstant.valueURLPPseudo, result4);


        }

        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get MT for some strings with html tag")]
        //public void GetTranslationForStringWithHTMLTag_bug_2281()
        //{


        //    String result1 = Translation.GetString("es", SourceHTMLTagWithSource);
        //    String result2 = Translation.Format("es", SourceHTMLTag);
        //    Assert.AreEqual(TestDataConstant.valueURLPMTes, result1);
        //    Assert.AreEqual(TestDataConstant.valueURLMTes, result2);

        //    Translation.SetCurrentLocale("es");
        //    String Currentlocale = Translation.GetCurrentLocale();
        //    Console.WriteLine(Currentlocale);
        //    String result4 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
        //    Assert.AreEqual(TestDataConstant.valueURLMTes, result4);



        //}


    }
}