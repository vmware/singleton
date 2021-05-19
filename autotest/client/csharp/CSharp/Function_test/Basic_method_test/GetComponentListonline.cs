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
    public class GetComponentListonline
    {
        

        private IReleaseMessages PM;
        private IConfig CM;
        private ITranslation Translation;
        private Dictionary<string, ILocaleMessages> AllTranslation;


        public GetComponentListonline()
        {
            Utilonline_only.Init();         
            PM = Utilonline_only.Messages();
            CM = Utilonline_only.Config();
            Translation = Utilonline_only.Translation();

        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("The method of IReleaseMessages")]
        public void ProductComponentListonline()
        {
            Translation.SetCurrentLocale("ko");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("오브젝트에 {0}을 (를) 추가하십시오.", result2);
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Assert.AreEqual("about, contact, DefaultComponent, RESX", result);
            List<string> localelist = PM.GetLocaleList();
            String result1 = Common.ParseListStringContent(localelist);
            Assert.AreEqual("ar, de, en, fr, ja, ko, zh-Hans, zh-Hant, es", result1);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            Assert.IsTrue(AllTranslation.ContainsKey("zh-Hans"));
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("the method of ILocaleMessages")]
        public void ProductComponentListonline1()
        {
            Translation.SetCurrentLocale("de-DE");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result2);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            Assert.IsTrue(AllTranslation.ContainsKey("ar"));
            // Dictionary<string, ILocaleMessages>.ValueCollection values = AllTranslation.Values;
            //foreach (string key in keys)
            //{
            //    Console.WriteLine("Key: {0}", key);
            //}
            String result5 = Common.ParseListStringContent(AllTranslation["de"].GetComponentList());
            Assert.AreEqual("RESX", result5);
            List<string> componentlist1 = PM.GetLocaleMessages("de").GetComponentList();
            String result4 = Common.ParseListStringContent(componentlist1);
            Assert.AreEqual("RESX", result4);
            String result6 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Assert.AreEqual("Ihre Kontaktseite.", result6);
            String result7 = PM.GetLocaleMessages("de").GetString("RESX", "contact.address");
            Assert.AreEqual(null, result7);

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("the method of IComponentMessages")]
        public void ProductComponentListonline2()
        {
            Translation.SetCurrentLocale("de");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("Fügen Sie dem Objekt {0} hinzu.", result2);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetString("123", "456");
            string message1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetString("123");
            Assert.AreEqual("456", message1);
            int number = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetCount();
            Assert.AreEqual("4", number.ToString());
            string locale1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetLocale();
            Assert.AreEqual("de", locale1);
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
