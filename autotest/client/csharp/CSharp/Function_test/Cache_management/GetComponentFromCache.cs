/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
//using SingletonClient.Implementation.Support;

namespace CSharp
{
    [TestClass]
    public class GetComponentFromCache
    {
        

        private IReleaseMessages PM;
        private ILocaleMessages LM_source;
        private IExtension IE_source;
        private ICacheManager EM;
        private ICacheMessages IC_source;
        private ILocaleMessages LM_translation_en;
        private ILocaleMessages LM_translation_cn;
        private IComponentMessages CM_source;
        private IComponentMessages CM_translation_en;
        private IComponentMessages CM_translation_cn;
        private ILocaleMessages LM_translation_la;
        private ICacheManager CManager;
        private Dictionary<string, ILocaleMessages> AllTranslation;



        public GetComponentFromCache()
        {
            UtilAllFalse.Init();
            
            PM = UtilAllFalse.Messages();
            // LM_source = PM.GetAllSource();
            //AllTranslation = PM.GetLocaleMessages("de");
            //CM_source = AllTranslation.GetComponentMessages("RESX");
            //IE_source = UtilAllFalse.Extension();
            // EM = IE_source.RegisterCacheManager();
            //IC_source =
            /////The client doesn't know the latest language
            //LM_translation_la = PM.GetLocaleMessages("latest");
            //CM_source = LM_translation_la.GetComponentMessages("RESX");
            LM_translation_cn = PM.GetLocaleMessages("zh-Hans");
            LM_translation_en = PM.GetLocaleMessages("en-US");
            CM_translation_en = LM_translation_en.GetComponentMessages("RESX");
            CM_translation_cn = LM_translation_cn.GetComponentMessages("RESX");
            //CManager = new SingletonCacheManager();

        }

        //////The client doesn't know the latest language
        //[TestMethod]
        //[Priority(0)]
        //[TestCategory("")]
        //[Description("Add one string to cache from GetAllSource(), will get the latest file content")]
        //public void ProductString_GetAllSource_addOneString()
        //{
        //    LM_translation_la = PM.GetLocaleMessages("latest");
        //    CM_source = LM_translation_la.GetComponentMessages("RESX");
        //    int count = CM_source.GetCount();
        //    Console.WriteLine(count);

        //    CM_source.SetString("RESX.1", "string 1");
        //    int count1 = CM_source.GetCount();
        //    Console.WriteLine(count1);
        //    Assert.AreEqual(count + 1, count1);

        //    //Thread.Sleep(70000);

        //    String value = CM_source.GetString("RESX.1");
        //    Console.WriteLine(value);
        //    Assert.AreEqual("string 1", value);

        //    String locale = CM_source.GetLocale();
        //    Assert.AreEqual("latest", locale);

        //    String component = CM_source.GetComponent();
        //    Assert.AreEqual("RESX", component);

        //    ICollection keys = CM_source.GetKeys();
        //    Assert.AreEqual(count1, keys.Count);


        //}

        //////The client doesn't know the latest language
        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Add multi string to cache from GetAllSource(), will get the latest file content")]
        //public void ProductString_GetAllSource_addMultiString()
        //{
        //    LM_translation_la = PM.GetLocaleMessages("xxxxxxxxxxx");
        //    CM_source = LM_translation_la.GetComponentMessages("RESX");
        //    int count = CM_source.GetCount();
        //    Console.WriteLine(count);

        //    CM_source.SetString("RESX.2", "string 2");
        //    CM_source.SetString("RESX.3", "string 3");
        //    int count2 = CM_source.GetCount();
        //    Console.WriteLine(count2);
        //    Assert.AreEqual(count + 2, count2);


        //    String value = CM_source.GetString("RESX.2");
        //    Console.WriteLine(value);
        //    Assert.AreEqual("string 2", value);

        //    String locale = CM_source.GetLocale();
        //    Assert.AreEqual("latest", locale);

        //    String component = CM_source.GetComponent();
        //    Assert.AreEqual("RESX", component);

        //    ICollection keys = CM_source.GetKeys();
        //    Assert.AreEqual(count2, keys.Count);


        //}

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Add one string to cache from GetLocaleMessages(), will get the language_en file content")]
        public void ProductString_GetLocaleMessagesEN_addOneString()
        {

            int count = CM_translation_en.GetCount();
            Console.WriteLine(count);

            CM_translation_en.SetString("RESX.4", "string 4");
            int count1 = CM_translation_en.GetCount();
            Console.WriteLine(count1);
            Assert.AreEqual(count + 1, count1);


            String value = CM_translation_en.GetString("RESX.4");
            Console.WriteLine(value);
            Assert.AreEqual("string 4", value);

            String locale = CM_translation_en.GetLocale();
            Assert.AreEqual("en-US", locale);

            String component = CM_translation_en.GetComponent();
            Assert.AreEqual("RESX", component);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Add multi string to cache from GetLocaleMessages(), will get the language_en file content")]
        public void ProductString_GetLocaleMessagesEN_addMultiString()
        {
            int count = CM_translation_en.GetCount();
            Console.WriteLine(count);

            CM_translation_en.SetString("RESX.5", "string 5");
            CM_translation_en.SetString("RESX.6", "string 6");
            int count1 = CM_translation_en.GetCount();
            Console.WriteLine(count1);
            Assert.AreEqual(count + 2, count1);


            String value = CM_translation_en.GetString("RESX.6");
            Console.WriteLine(value);
            Assert.AreEqual("string 6", value);

            String locale = CM_translation_en.GetLocale();
            Assert.AreEqual("en", locale);

            String component = CM_translation_en.GetComponent();
            Assert.AreEqual("RESX", component);

            ICollection<string> keys = CM_translation_en.GetKeys();
            Assert.AreEqual(count1, keys.Count);


        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Add one string to cache from GetLocaleMessages(), will get the language_cn file content")]
        public void ProductString_GetLocaleMessagesCN_addOneString()
        {
            int count = CM_translation_cn.GetCount();
            Console.WriteLine(count);

            CM_translation_cn.SetString("RESX.7", "string 7");
            int count1 = CM_translation_cn.GetCount();
            Console.WriteLine(count1);
            Assert.AreEqual(count + 1, count1);

            //Thread.Sleep(70000);

            //int count2 = CM_translation_cn.GetCount();
            //Console.WriteLine(count2);

            String value = CM_translation_cn.GetString("RESX.7");
            Console.WriteLine(value);
            Assert.AreEqual("string 7", value);

            String locale = CM_translation_cn.GetLocale();
            Assert.AreEqual("zh-Hans", locale);

            String component = CM_translation_cn.GetComponent();
            Assert.AreEqual("RESX", component);

            ICollection<string> keys = CM_translation_cn.GetKeys();
            Assert.AreEqual(count1, keys.Count);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Add multi string to cache from GetLocaleMessages(), will get the language_CN file content")]
        public void ProductString_GetLocaleMessagesCN_addMultiString()
        {
            int count = CM_translation_cn.GetCount();
            Console.WriteLine(count);

            CM_translation_cn.SetString("RESX.8", "string 8");
            CM_translation_cn.SetString("RESX.9", "string 9");
            int count1 = CM_translation_cn.GetCount();
            Console.WriteLine(count1);
            Assert.AreEqual(count + 2, count1);


            String value = CM_translation_cn.GetString("RESX.9");
            Console.WriteLine(value);
            Assert.AreEqual("string 9", value);

            String locale = CM_translation_cn.GetLocale();
            Assert.AreEqual("zh-Hans", locale);

            String component = CM_translation_cn.GetComponent();
            Assert.AreEqual("RESX", component);

            ICollection<string> keys = CM_translation_cn.GetKeys();
            Assert.AreEqual(count1, keys.Count);


        }







    }
}
