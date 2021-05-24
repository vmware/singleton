///*
// * Copyright 2020 VMware, Inc.
// * SPDX-License-Identifier: EPL-2.0
// */

//using System;
//using System.Collections.Generic;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using SingletonClient;

//namespace CSharp
//{
//    [TestClass]
//    public class InitialConfigTest
//    {

//        private IConfig configLoad;
//        private IConfig configGet;
//        private IConfig configRel;
//        //private IConfig config;
//        //private IRelease release;
//        public InitialConfigTest()
//        {
//            UtilAllFalse.Init();
//            //config = Util.Config();
//            //release = Util.Release();
//            configLoad = I18N.LoadConfig(
//                ValuesAllFalse.BASE_RES_NAME, ValuesAllFalse.assembly, "SingletonAllFalse");
//            configGet = I18N.GetConfig("CSharpClient", "1.0.0");
//            configRel = UtilAllFalse.Config();
        
//        }


//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get product name")]
//        public void Product()
//        {
//            string ProductGet = configGet.GetProduct();
//            string ProductLoad = configLoad.GetProduct();
//            string ProductRel = configRel.GetProduct();
//            Assert.AreEqual(ProductGet, "CSharpClient");
//            Assert.AreEqual(ProductLoad, "CSharpClient");
//            Assert.AreEqual(ProductRel, "CSharpClient");
//        }

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get string value of key")]
//        public void StringValue()
//        {
//            string StringValueGet = configGet.GetStringValue(ConfigConst.KeyCacheType);
//            string StringValueLoad = configLoad.GetStringValue(ConfigConst.KeyAccessServiceType);
//            string StringValueRel = configRel.GetStringValue(ConfigConst.KeyLogType);
//            Assert.AreEqual(StringValueGet, "default");
//            Assert.AreEqual(StringValueLoad, "default");
//            Assert.AreEqual(StringValueRel, "Debug");
//        }

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get bool value of some keys")]
//        public void BoolValue()
//        {
//            bool BoolValueGet = configGet.GetBoolValue(ConfigConst.KeyCollect);
//            bool BoolValueLoad = configLoad.GetBoolValue(ConfigConst.KeyMachine);
//            bool BoolValueRel = configRel.GetBoolValue(ConfigConst.KeyMachine);
//            Assert.AreEqual(BoolValueGet, false);
//            Assert.AreEqual(BoolValueLoad, false);
//            Assert.AreEqual(BoolValueRel, false);
//        }

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get initial time value")]
//        public void InitialTimeValue()
//        {
//            int InitialTimeValueGet = configGet.GetIntValue(ConfigConst.KeyInterval);
//            int InitialTimeValueLoad = configLoad.GetIntValue(ConfigConst.KeyInterval);
//            Assert.AreEqual(InitialTimeValueGet, Int32.Parse("60000"));
//            Assert.AreEqual(InitialTimeValueGet, Int32.Parse("60000"));
//            int InitialTimeValueRel = configRel.GetIntValue(ConfigConst.KeyTryDelay);
//            Assert.AreEqual(InitialTimeValueRel, Int32.Parse("10"));
//        }

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get component list")]
//        public void ComponentList()
//        {
//            List<string> ComponentListGet = configGet.GetComponentList();
//            List<string> ComponentListLoad = configLoad.GetComponentList();
//            Assert.AreEqual(ComponentListGet.Count, Int32.Parse("4"));
//            Assert.AreEqual(ComponentListGet.Count, Int32.Parse("4"));
//            List<string> ComponentListRel = configRel.GetComponentList();
//            Assert.AreEqual(ComponentListRel.Count, Int32.Parse("4"));
//        }

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("get component source list")]
//        public void ComponentSourceList()
//        {
//            List<string> ComponentSourceListGet = configGet.GetComponentSourceList("DefaultComponent");
//            List<string> ComponentSourceListLoad = configLoad.GetComponentSourceList("about");
//            Assert.AreEqual(ComponentSourceListGet.Count, Int32.Parse("1"));
//            Assert.AreEqual(ComponentSourceListLoad.Count, Int32.Parse("1"));
//            List<string> ComponentSourceListRel = configRel.GetComponentSourceList("contact");
//            Assert.AreEqual(ComponentSourceListRel.Count, Int32.Parse("1"));
//        }
        

//        [TestMethod]
//        [Priority(1)]
//        [TestCategory("")]
//        [Description("Get resource text")]
//        public void ResourceText()
//        {
//            //string Product = config.GetProduct();
//            //Console.WriteLine(Product);
//            //Assert.AreEqual(Product, "CSharpClient");

//            string ResourceTextGet = configGet.ReadResourceText("DefaultComponent");
//            string ResourceTextLoad = configLoad.ReadResourceText("DefaultComponent");
//            Assert.IsTrue(ResourceTextGet.Contains("messages.welcome = Welcome"));
//            Assert.IsTrue(ResourceTextLoad.Contains("messages.welcome = Welcome"));
//            string ResourceTextRel = configRel.ReadResourceText("DefaultComponent");
//            Assert.IsTrue(ResourceTextRel.Contains("messages.welcome = Welcome"));

            
//        }



        
//    }
//}
