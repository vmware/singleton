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
        

        private IReleaseMessages PM;

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
            Assert.AreEqual(result, "ar, de, en, fr, ja, ko, zh-Hans, zh-Hant, es");

            
        }

        
    }
}
