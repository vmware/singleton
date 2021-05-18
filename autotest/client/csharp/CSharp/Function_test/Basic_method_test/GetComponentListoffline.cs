/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;
using System.Xml;

namespace CSharp
{
    [TestClass]
    public class GetComponentListoffline
    {
        
        private IReleaseMessages PM;
        private IConfig CM;
        private ITranslation Translation;
        private Dictionary<string, ILocaleMessages> AllTranslation;


        public GetComponentListoffline()
        {
            Utiloffline_Internal_Resx.Init();         
            PM = Utiloffline_Internal_Resx.Messages();
            CM = Utiloffline_Internal_Resx.Config();
            Translation = Utiloffline_Internal_Resx.Translation();

        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("The method of IReleaseMessages")]
        public void ProductComponentListoffline()
        {
            Translation.SetCurrentLocale("ru");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("translation: {0}",result2);
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine("component list: {0}",result);
            List<string> localelist = PM.GetLocaleList();
            String result1 = Common.ParseListStringContent(localelist);
            Console.WriteLine("locale list: {0}", result1);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            foreach (string key in keys)
            {
                Console.WriteLine("Key: {0}", key);
            }
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("the method of ILocaleMessages")]
        public void ProductComponentListonline1()
        {
            Translation.SetCurrentLocale("de");
            String result2 = Translation.GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Console.WriteLine(result2);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            // Dictionary<string, ILocaleMessages>.ValueCollection values = AllTranslation.Values;
            foreach (string key in keys)
            {
                Console.WriteLine("Key: {0}", key);
            }
            //String result5 = Common.ParseListStringContent(AllTranslation["de"].GetComponentList());
            //Console.WriteLine(result5);
            List<string> componentlist1 = PM.GetLocaleMessages("de").GetComponentList();
            String result4 = Common.ParseListStringContent(componentlist1);
            Console.WriteLine(result4);
            String result5 = PM.GetLocaleMessages("de").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result5);
            //Assert.AreEqual("rrr연락처 페이지", result5);
            String result6 = PM.GetLocaleMessages("de").GetString("RESX", "contact.address");
            Console.WriteLine(result6);
            //Assert.AreEqual(null, result6);
            string message1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetString("contact.address");
            Console.WriteLine(message1);

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("the method of IComponentMessages")]
        public void ProductComponentListonline2()
        {
            Translation.SetCurrentLocale("de");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result2);
            PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetString("123", "456");
            string message1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetString("123");
            Console.WriteLine(message1);
            int number = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetCount();
            Console.WriteLine(number);
            string locale1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetLocale();
            Console.WriteLine(locale1);
            string component1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetComponent();
            Console.WriteLine(component1);
            //PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourcePath("d://123");
            string resoucepath1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourcePath();
            Console.WriteLine("the resoucepath:{0}", resoucepath1);
            //PM.GetLocaleMessages("de").GetComponentMessages("RESX").SetResourceType("string");
            string resourcetype1 = PM.GetLocaleMessages("de").GetComponentMessages("RESX").GetResourceType();
            Console.WriteLine("the resoucetype:{0}", resourcetype1);



        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("the method of ILocaleMessages")]
        public void ProductComponentListonline3()
        {
            Translation.SetCurrentLocale("en");
            String result2 = Translation.GetString("about", "about.title");
            Console.WriteLine(result2);
            AllTranslation = PM.GetAllLocaleMessages();
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            // Dictionary<string, ILocaleMessages>.ValueCollection values = AllTranslation.Values;
            foreach (string key in keys)
            {
                Console.WriteLine("Key: {0}", key);
            }
            //String result5 = Common.ParseListStringContent(AllTranslation["de"].GetComponentList());
            //Console.WriteLine(result5);
            List<string> componentlist1 = PM.GetLocaleMessages("en").GetComponentList();
            String result4 = Common.ParseListStringContent(componentlist1);
            Console.WriteLine(result4);
            String result5 = PM.GetLocaleMessages("en").GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Console.WriteLine(result5);
            //Assert.AreEqual("rrr연락처 페이지", result5);
            String result6 = PM.GetLocaleMessages("en").GetString("about", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Console.WriteLine(result6);
            String result10 = PM.GetLocaleMessages("en").GetString("about", "contact.title");
            Console.WriteLine(result10);
            //Assert.AreEqual(null, result6);
            int number = PM.GetLocaleMessages("en").GetComponentMessages("about").GetCount();
            Console.WriteLine(number);

        }



    }
}
