/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;
using System.IO;

namespace CSharp
{
    [TestClass]
    public class GetComponentListCache
    {
        

        private IReleaseMessages PM;
        private ILocaleMessages LM_source;
        private ILocaleMessages LM_translation;
        private IExtension Ext;
        private ICacheManager CM;

        public GetComponentListCache()
        {
            UtilAllFalse.Init();         
            PM = UtilAllFalse.Messages();
            //LM_source = PM.GetAllSource();
            LM_source = PM.GetLocaleMessages("en");
            UtilAllFalse.Translation().GetString("zh-Hans", UtilAllFalse.Source("about", "about.message"));

        }


        [TestMethod]
        [Priority(3)]
        [TestCategory("")]
        [Description("Get component list from GetAllSource()")]
        public void ProductComponentList_GetAllSource()
        {
            
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("about, contact, DefaultComponent, RESX", result);
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get component list from GetLocaleMessages() with existing locale")]
        public void ProductComponentList_GetLocaleMessages_ExistingLocale()
        {
            LM_translation = PM.GetLocaleMessages("zh-Hans");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.IsTrue(ComponentList.Contains("about"));
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component list from GetLocaleMessages() with nonexistent locale")]
        public void ProductComponentList_GetLocaleMessages_nonexistentLocale()
        {
            LM_translation = PM.GetLocaleMessages("da");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("", result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component list from GetLocaleMessages() with empty locale")]
        public void ProductComponentList_GetLocaleMessages_EmptyLocale()
        {
            LM_translation = PM.GetLocaleMessages("");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("", result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Can't get component list from GetLocaleMessages() with null locale")]
        public void ProductComponentList_GetLocaleMessages_NullLocale_bug_2294()
        {
            LM_translation = PM.GetLocaleMessages(null);
            Assert.AreEqual(null, LM_translation);
            

            try
            {
                List<string> ComponentList = LM_translation.GetComponentList();
            }
            catch (System.NullReferenceException e)
            {
                Console.WriteLine("Can't get component list if LM translation is null.");
            }
        }

        


    }
}
