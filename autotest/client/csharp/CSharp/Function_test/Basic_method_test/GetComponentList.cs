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
    public class GetComponentList
    {
        

        private IReleaseMessages PM;
    

        public GetComponentList()
        {
            UtilAllFalse.Init();         
            PM = UtilAllFalse.Messages();
            
        }


        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Get component list")]
        public void ProductComponentList()
        {
            List<string> ComponentList = PM.GetComponentList();
            String result = Common.ParseListStringContent(ComponentList);
            Console.WriteLine(result);
            Assert.AreEqual("about, contact, DefaultComponent, RESX", result);

            
        }

        
        
    }
}
