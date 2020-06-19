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
    public class GetAndSetLocale
    {

        private IReleaseMessages PM;
        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private String[] args;


        public GetAndSetLocale()
        {
            UtilAllFalse.Init();
            //Release = Util.Release();
            PM = UtilAllFalse.Messages();
            Translation = UtilAllFalse.Translation();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");
            SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object.", "argument verification");
            //SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            SourceHTMLTag = Translation.CreateSource("RESX", "Resx-message.URL");
            SourceHTMLTagWithSource = Translation.CreateSource("RESX", "Resx-message.URL", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "1" };


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Locale is valid locale")]
        public void Locale_valid()
        {

            
            Translation.SetCurrentLocale("zh-Hans-CN");
            String Currentlocale1 = Translation.GetCurrentLocale();
            Assert.AreEqual("zh-CN", Currentlocale1);
            List<string> LocaleList = PM.GetLocaleList();
            foreach (string key in LocaleList)
            {
                Console.WriteLine("Key: {0}", key);
            }

            Translation.SetCurrentLocale("es-MX");
            String Currentlocale2 = Translation.GetCurrentLocale();
            Assert.AreEqual("es-MX", Currentlocale2);

            Translation.SetCurrentLocale("en");
            String Currentlocale3 = Translation.GetCurrentLocale();
            Assert.AreEqual("en", Currentlocale3);

            Translation.SetCurrentLocale("FR");
            String Currentlocale4 = Translation.GetCurrentLocale();
            Assert.AreEqual("fr", Currentlocale4);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Locale is invalid locale_character")]
        public void Locale_invalid_character()
        {


            Translation.SetCurrentLocale("abc");
            String Currentlocale1 = Translation.GetCurrentLocale();
            Assert.AreEqual("abc", Currentlocale1);

            Translation.SetCurrentLocale("@");
            String Currentlocale2 = Translation.GetCurrentLocale();
            Assert.AreEqual("en-US", Currentlocale2);

            Translation.SetCurrentLocale("123");
            String Currentlocale3 = Translation.GetCurrentLocale();
            Assert.AreEqual("en-US", Currentlocale3);


        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Locale is invalid locale_null")]
        public void Locale_invalid_null()
        {


            Translation.SetCurrentLocale(null);
            String Currentlocale1 = Translation.GetCurrentLocale();
            Assert.AreEqual("en-US", Currentlocale1);


        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Locale is invalid locale_empty")]
        public void Locale_invalid_empty()
        {


            Translation.SetCurrentLocale("");
            String Currentlocale1 = Translation.GetCurrentLocale();
            Assert.AreEqual("en-US", Currentlocale1);


        }
    }
}