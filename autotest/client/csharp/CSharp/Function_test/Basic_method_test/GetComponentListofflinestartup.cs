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
    public class GetComponentListofflineexternal
    {
        

        private IReleaseMessages PM;
        private IConfig CM;
        private ITranslation Translation;
        private Dictionary<string, ILocaleMessages> AllTranslation;


        public GetComponentListofflineexternal()
        {
            Utiloffline_disk.Init();         
            PM = Utiloffline_disk.Messages();
            CM = Utiloffline_disk.Config();
            Translation = Utiloffline_disk.Translation();

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("The method of IReleaseMessages")]
        public void ProductComponentListofflineexternal()
        {
            Translation.SetCurrentLocale("ko");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Assert.AreEqual("ddFügen Sie dem Objekt {0} hinzu.", result2);
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Assert.AreEqual("about, contact, DefaultComponent, RESX, RESXPPP", result);
            List<string> localelist = PM.GetLocaleList();
            String result1 = Common.ParseListStringContent(localelist);
            Assert.AreEqual("ar, de, en-US, fr, ja, ko, zh-Hans, zh-Hant, en, es, my, ru", result1);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            Assert.IsTrue(AllTranslation.ContainsKey("ko"));
            //foreach (string key in keys)
            //{
            //    Console.WriteLine("Key: {0}", key);
            //}
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("the method of ILocaleMessages")]
        public void ProductComponentListofflineexternal1()
        {
            //Translation.SetCurrentLocale("ru");
            //String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            //Console.WriteLine(result2);
            AllTranslation = PM.GetAllLocaleMessages();
            List<string> componentlist1 = PM.GetLocaleMessages("de").GetComponentList();
            String result4 = Common.ParseListStringContent(componentlist1);
            Assert.AreEqual("DefaultComponent, RESXPPP, RESX, about, contact", result4);
            String result5 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Assert.AreEqual("dddYour contact page.", result5);
            // Assert.AreEqual("rrr연락처 페이지", result5);
            String result6 = PM.GetLocaleMessages("de").GetString("RESX", "contact.address");
            Assert.AreEqual(null, result6);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("the method of IComponentMessages")]
        public void ProductComponentListofflineexternal2()
        {
            //Translation.SetCurrentLocale("de");
            //String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            //Console.WriteLine(result2);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetString("123", "456");
            string message1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetString("123");
            Assert.AreEqual("456",message1);
            int number = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetCount();
            Assert.AreEqual("5", number.ToString());
            string locale1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetLocale();
            Assert.AreEqual("de-DE", locale1);
            string component1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetComponent();
            Assert.AreEqual("RESX", component1);
            //PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourcePath("d://123");
            string resoucepath1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourcePath();
            Assert.AreEqual("d:/l10ntest/CSharpClient/1.0.8/RESX/messages_de.json", resoucepath1);
            //PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourceType("string");
            string resourcetype1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourceType();
            Assert.AreEqual("bundle", resourcetype1);



        }



    }
}
