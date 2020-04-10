﻿using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class GetStringFromCache
    {
        

        private IProductMessages PM;
        private ILanguageMessages LM_source;
        private ILanguageMessages LM_translation;
        private IExtension Ext;
        private ICacheManager CM;

        public GetStringFromCache()
        {
            UtilForCache.Init();         
            PM = UtilForCache.Messages();
            LM_source = PM.GetAllSource();

            UtilForCache.Translation().GetString("zh-Hans", UtilForCache.Source("RESX", "RESX.ARGUMENT"));

        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get string from GetAllSource()")]
        public void ProductString_GetAllSource()
        {
            
            String result = LM_source.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual(TestDataConstant.valueArg, result);


            
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string from GetAllSource() with non-existing component")]
        public void ProductString_GetAllSource_nonExisting_component()
        {

            String result = LM_source.GetString("abc", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);



        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string from GetAllSource() with non-existing key")]
        public void ProductString_GetAllSource_nonExisting_key()
        {

            String result = LM_source.GetString("RESX", "abc");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string from GetAllSource() with non-existing key")]
        public void ProductString_GetAllSource_NULL_bug_2295()
        {

            String result = LM_source.GetString(null, null);
            Console.WriteLine(result);
            Assert.AreEqual(null, result);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string from GetAllSource() with empty")]
        public void ProductString_GetAllSource_empty()
        {

            String result = LM_source.GetString("", "");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);

        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get string1 in cache from GetTranslation() with existing locale")]
        public void ProductString1_InCache_GetTranslation_ExistingLocale()
        {
           
            
            LM_translation = PM.GetTranslation("zh-Hans");
            string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual("将{0}添加到对象。", result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string2 in cache from GetTranslation() with existing locale")]
        public void ProductString2_InCache_GetTranslation_ExistingLocale()
        {


            LM_translation = PM.GetTranslation("zh-Hans");
            string result = LM_translation.GetString("RESX", "Resx-message.URL");
            Console.WriteLine(result);
            Assert.AreEqual(TestDataConstant.valueURLcn, result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string in cache from GetTranslation() with locale not in supported list")]
        public void ProductString_InCache_GetTranslation_Locale_NotInSupportedList()
        {


            LM_translation = PM.GetTranslation("ko");
            string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string in cache from GetTranslation() with non-existing locale")]
        public void ProductString_GetTranslation_nonExistingLocale()
        {
            LM_translation = PM.GetTranslation("da");
            string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string in cache from GetTranslation() with empty locale")]
        public void ProductString_GetTranslation_EmptyLocale()
        {
            LM_translation = PM.GetTranslation("");
            string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result);
            Assert.AreEqual(null, result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get string in cache from GetTranslation() with null locale")]
        public void ProductString_GetTranslation_NullLocale_bug_2294()
        {
            LM_translation = PM.GetTranslation(null);
            Assert.AreEqual(null, LM_translation);

            try
            {
                string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            }
            catch(System.NullReferenceException e)
            {
                Console.WriteLine("Can't get string if LM translation is null.");
            }
            //string result = LM_translation.GetString("RESX", "RESX.ARGUMENT");
            //Console.WriteLine(result);
            //Assert.AreEqual(null, result);
        }

        


    }
}
