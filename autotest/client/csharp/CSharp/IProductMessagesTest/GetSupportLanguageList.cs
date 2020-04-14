/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using SingletonClient.Implementation;

namespace CSharp
{
    [TestClass]
    public class GetSupportLanguageList
    {
        

        private IProductMessages PM;

        public GetSupportLanguageList()
        {
            UtilAllFalse.Init();
            //ILog logger = SingletonClientManager.GetInstance().GetLogger(ConfigConst.TypeDefault);

            //logger.Log(LogType.Error, "abc");

            PM = UtilAllFalse.Messages();


        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get support language list name")]
        public void ProductLocaleList()
        {
            List<string> LocaleList = PM.GetLocaleList();
            String result = Common.ParseListStringContent(LocaleList);
            
            Console.WriteLine(result);
            Assert.AreEqual(result, "de, ko, zh-Hans, fr, ja, en, zh-Hant, es, ar");

            
        }

        
    }
}
