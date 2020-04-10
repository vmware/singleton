using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class GetStringPseudoTrue
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceArgument_NotExistingKey;
        private ISource SourceArgument_NotExistingValue;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private String[] args;


        public GetStringPseudoTrue()
        {
            UtilPseudoTrue.Init();
            //Release = Util.Release();
            Translation = UtilPseudoTrue.Translation();
            //from resx file
            SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object.", "argument verification");
            //from resx file
            SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            //from resx file
            SourceHTMLTag = Translation.CreateSource("RESX", "Resx-message.URL");
            //from properties file
            SourceHTMLTagWithSource = Translation.CreateSource("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "1" };

            SourceArgument_NotExistingKey = Translation.CreateSource("RESX", "RESX.ARGUMENT.NotExisting", "Add {0} to the object.", "argument verification");
            SourceArgument_NotExistingValue = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object not existing.", "argument verification");

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get pseudo translation for language(en,fr) with 2 parameters")]
        public void GetPseudo_ExistingLanguage_TwoParameters()
        {

            Translation.SetCurrentLocale("en");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, SourceError);
            String result2 = Translation.GetString(Currentlocale1, SourceArgument);
            String result4 = Translation.GetString(Currentlocale1, SourceHTMLTag);
            String result3 = Translation.GetString(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("en pseudo transaltion1: {0}", result1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, result1);
            Assert.AreEqual(TestDataConstant.valueArgPseudo, result2);
            Assert.AreEqual(TestDataConstant.valueURLPseudo, result4);
            Assert.AreEqual(TestDataConstant.valueURLPPseudo, result3);



            Translation.SetCurrentLocale("fr");
            String Currentlocale2 = Translation.GetCurrentLocale();
            String resultFR1 = Translation.GetString(Currentlocale2, SourceError);
            String resultFR2 = Translation.GetString(Currentlocale2, SourceArgument);
            String resultFR3 = Translation.GetString(Currentlocale2, SourceHTMLTagWithSource);
            String resultFR4 = Translation.GetString(Currentlocale2, SourceHTMLTag);
            Console.WriteLine("FR pseudo transaltion1: {0}", resultFR1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, resultFR1);
            Assert.AreEqual(TestDataConstant.valueArgPseudo, resultFR2);
            Assert.AreEqual(TestDataConstant.valueURLPPseudo, resultFR3);
            Assert.AreEqual(TestDataConstant.valueURLPseudo, resultFR4);


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get translation for language(ja,da) with Full parameters")]
        public void GetTranslation_ExistingLanguage_FullParameters()
        {

            Translation.SetCurrentLocale("ja");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", TestDataConstant.keyError, TestDataConstant.valueError);
            String result2 = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            String result3 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
            String result4 = Translation.GetString("DefaultComponent", TestDataConstant.keyURLP, TestDataConstant.valueURLP);
            Console.WriteLine("full param pseudo transaltion: {0}", result1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, result1);
            Assert.AreEqual(TestDataConstant.valueArgPseudo, result2);
            Assert.AreEqual(TestDataConstant.valueURLPseudo, result3);
            Assert.AreEqual(TestDataConstant.valueURLPPseudo, result4);


            Translation.SetCurrentLocale("da");
            String resultDA1 = Translation.GetString("RESX", TestDataConstant.keyError, null);
            String resultDA2 = Translation.GetString("RESX", TestDataConstant.keyArg, null, null);
            String resultDA3 = Translation.GetString("RESX", TestDataConstant.keyURL, TestDataConstant.valueURL);
            String resultDA4 = Translation.GetString("DefaultComponent", TestDataConstant.keyURLP, TestDataConstant.valueURLP);
            Console.WriteLine("fr transaltion1: {0}", resultDA1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, resultDA1);
            Assert.AreEqual(TestDataConstant.valueArgPseudo, resultDA2);
            Assert.AreEqual(TestDataConstant.valueURLPseudo, resultDA3);
            Assert.AreEqual(TestDataConstant.valueURLPPseudo, resultDA4);
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for existing language with argument")]
        public void GetTranslation_ExistingLanguage_Argument_Format()
        {

            Translation.SetCurrentLocale("ja");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceError, "aaa");
            String result2 = Translation.Format(Currentlocale1, SourceArgument, args);
            String result3 = Translation.Format(Currentlocale1, SourceHTMLTag);
            String result4 = Translation.Format(Currentlocale1, SourceHTMLTagWithSource);
            Console.WriteLine("ja format pseudo transaltion: {0}", result1);
            Assert.AreEqual(TestDataConstant.valueErrorPseudo, result1);
            Assert.AreEqual("@@Add 1 to the object.@@", result2);
            Assert.AreEqual(TestDataConstant.valueURLPseudo, result3);
            Assert.AreEqual(TestDataConstant.valueURLPPseudo, result4);


        }

       


    }
}