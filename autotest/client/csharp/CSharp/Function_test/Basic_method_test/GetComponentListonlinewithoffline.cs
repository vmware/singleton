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
    public class GetComponentListonlinewithoffline_exteral
    {
        

        private IReleaseMessages PM;
        private IConfig CM;
        private ITranslation Translation;
        private Dictionary<string, ILocaleMessages> AllTranslation;


        public GetComponentListonlinewithoffline_exteral()
        {
            Utilonline_with_external.Init();         
            PM = Utilonline_with_external.Messages();
            CM = Utilonline_with_external.Config();
            Translation = Utilonline_with_external.Translation();

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("The method of IReleaseMessages")]
        public void ProductComponentListonlinewithexternal()
        {
            Translation.SetCurrentLocale("ru");
            String result2 = Translation.GetString("RESX", "Resx.only-offline");
            Assert.AreEqual("test offline key", result2);
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Assert.AreEqual("about, addcomponent, contact, DefaultComponent, RESX", result);
            List<string> localelist = PM.GetLocaleList();
            String result1 = Common.ParseListStringContent(localelist);
            Assert.AreEqual("de, en, fr, ja, ko, zh-Hans, zh-Hant, es", result1);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            Assert.IsTrue(AllTranslation.ContainsKey("zh-Hans"));
            //foreach (string key in keys)
            //{
            //    Console.WriteLine("Key: {0}", key);
            //}
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("the method of ILocaleMessages")]
        public void ProductComponentListonlinewithexternal1()
        {
            Translation.SetCurrentLocale("de");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result2);
            AllTranslation = PM.GetAllLocaleMessages();
            Assert.IsTrue(AllTranslation.ContainsKey("zh-Hans"));
            // Dictionary<string, ILocaleMessages>.ValueCollection values = AllTranslation.Values;
            //foreach (string key in keys)
            //{
            //    Console.WriteLine("Key: {0}", key);
            //}
            //String result5 = Common.ParseListStringContent(AllTranslation["ru"].GetComponentList());
            //Console.WriteLine(result5);
            List<string> componentlist1 = PM.GetLocaleMessages("de").GetComponentList();
            String result4 = Common.ParseListStringContent(componentlist1);
            Assert.AreEqual("RESX", result4);
            String result6 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Assert.AreEqual("Ihre Kontaktseite.", result6);
            String result7 = PM.GetLocaleMessages("de").GetString("RESX", "String123");
            Assert.AreEqual(null, result7);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("the method of IComponentMessages")]
        public void ProductComponentListonlinewithexternal2()
        {
            Translation.SetCurrentLocale("de");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result2);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetString("123", "456");
            string message1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetString("123");
            Assert.AreEqual("456", message1);
            int number = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetCount();
            Assert.AreEqual("5", number.ToString());
            string locale1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetLocale();
            Assert.AreEqual("de-DE", locale1);
            string component1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetComponent();
            Assert.AreEqual("RESX", component1);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourcePath("d://123");
            string resoucepath1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourcePath();
            Assert.AreEqual("d://123", resoucepath1);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourceType("string");
            string resourcetype1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourceType();
            Assert.AreEqual("string", resourcetype1);



        }



    }
}
