using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class GetComponentListCache
    {
        

        private IProductMessages PM;
        private ILanguageMessages LM_source;
        private ILanguageMessages LM_translation;
        private IExtension Ext;
        private ICacheManager CM;

        public GetComponentListCache()
        {
            UtilAllFalse.Init();         
            PM = UtilAllFalse.Messages();
            LM_source = PM.GetAllSource();

            UtilAllFalse.Translation().GetString("zh-Hans", UtilAllFalse.Source("about", "about.message"));

        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get component list from GetAllSource()")]
        public void ProductComponentList_GetAllSource()
        {
            
            List<string> ComponentList = LM_source.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("RESX, contact, about, DefaultComponent", result);

            
        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get component list from GetTranslation() with existing locale")]
        public void ProductComponentList_GetTranslation_ExistingLocale()
        {
            LM_translation = PM.GetTranslation("zh-Hans");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            //Assert.AreEqual("about", result);
            Assert.IsTrue(ComponentList.Contains("about"));


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component list from GetTranslation() with non-existing locale")]
        public void ProductComponentList_GetTranslation_nonExistingLocale()
        {
            LM_translation = PM.GetTranslation("da");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("", result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component list from GetTranslation() with empty locale")]
        public void ProductComponentList_GetTranslation_EmptyLocale()
        {
            LM_translation = PM.GetTranslation("");
            List<string> ComponentList = LM_translation.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("", result);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Can't get component list from GetTranslation() with null locale")]
        public void ProductComponentList_GetTranslation_NullLocale_bug_2294()
        {
            LM_translation = PM.GetTranslation(null);
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
