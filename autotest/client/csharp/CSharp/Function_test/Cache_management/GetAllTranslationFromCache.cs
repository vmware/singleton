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
    public class GetAllLocaleMessagesFromCache
    {
        

        private IReleaseMessages PM;
        private ILocaleMessages LM_Source;
        private ILocaleMessages LM_Translation;
        private Dictionary<string, ILocaleMessages> AllTranslation;

        public GetAllLocaleMessagesFromCache()
        {
            //this method is get all translation from ILanguageMessageCache, if there is nothing in cache, nothing will be get.
            UtilAllFalse.Init();         
            PM = UtilAllFalse.Messages();
            //LM_Source = PM.GetAllSource();

            UtilAllFalse.Translation().GetString("de", UtilAllFalse.Source("about", "about.message"));
            AllTranslation = PM.GetAllLocaleMessages();

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get all locales from cache")]
        public void GetAllLocalesFromCache()
        {

            
            Dictionary<string, ILocaleMessages>.KeyCollection keys = AllTranslation.Keys;
            foreach (string key in keys)
            {
                Console.WriteLine("Key: {0}", key);
            }
                       
            //List<string> ComponentList = PM.GetComponentList();
            //String result = Common.ParseListStringContent(ComponentList);
            //Console.WriteLine(AllTranslation.Keys);
            Assert.AreEqual(Int32.Parse("9"), AllTranslation.Keys.Count);
            Assert.IsTrue(AllTranslation.ContainsKey("ar"));
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component in cache")]
        public void GetComponentInCache()
        {

            //IProductMessage().GetAllLocaleMessages()
            String ComponentInCache = Common.ParseListStringContent(AllTranslation["de"].GetComponentList());
            Console.WriteLine("component list: {0} ", ComponentInCache);
            Assert.AreEqual("about", ComponentInCache);
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get locale's translation in cache")]
        public void GetLocaleTranslationInCache()
        {

            String translationInCache = PM.GetLocaleMessages("de").GetString("about", "about.message");
            Console.WriteLine("DE translation for about.message : {0} ", translationInCache);
            Assert.AreEqual("Ihrer Bewerbungs Beschreibung Seite.", translationInCache);

        }

        [TestMethod]
        [Priority(2)]
        [TestCategory("")]
        [Description("Get locale's translation not in cache")]
        public void GetLocaleTranslationNotInCache()
        {
            
            String translationNotInCache = PM.GetLocaleMessages("fr").GetString("about", "about.message");
            Console.WriteLine("FR translation for about.message : {0} ", translationNotInCache);
            Assert.AreEqual(null, translationNotInCache);
        }
     
    }
}
