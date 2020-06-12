/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using System.Threading;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json.Linq;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class GetStringCollectTrue
    {


        private ITranslation Translation;
        //private IRelease Release;



        public GetStringCollectTrue()
        {
            UtilCollectTrue.Init();
            //Release = UtilCollectTrue.Release();
            Translation = UtilCollectTrue.Translation();

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Source Collection_One string_ISource")]
        public void SourceCollection_1_OneString_GetString_ISource()
        {
            ISource source1_1 = Translation.CreateSource("contact", "collect.message", "application");
            ISource source1_2 = Translation.CreateSource("CollectSourceComponent1", "collect.argument", "{0} is the {1} day of a week.");
            ISource source1_3 = Translation.CreateSource("CollectSourceComponent1", "collect.00000000-0000-0000-0000-000000000000.templates_Draas-SSLCertificateRenew-completed.text", "<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager and vSphere Replication (VR) appliances running in your VMC SDDC completed successfully.</p><ul><li><span style=\"color: #003366;\"><strong>ORG : <span style=\"color: #3366ff;\">{org_id}</span></strong></span></li><li><span style=\"color: #003366;\"><strong>SDDC : <span style=\"color: #3366ff;\">{sddc_id}</span></strong></span></li></ul><p><strong style=\"color: #003366;\">Customer actions required</strong></p><p>Due this operation the remote VR status will look disconnected in the SRM UI of the SDDCs which have VR pairing to this SDDC. This doesn't affect the existing replications, so your workloads continue to be protected. However, the UI might show stale data for the replications status and replication management operations across the two sites won't work. To resolve the issue, now you can execute either of the following actions:<strong style=\"color: #003366;\">:</strong></p><ul><li><span style=\"color: #003366;\">Reconnect the VR pairing. The steps how to do this are outlined in the VR documentation <a href=\"https://docs.vmware.com/en/vSphere-Replication/8.2/com.vmware.vsphere.replication-admin.doc/GUID-AF7E944C-D077-498E-88AC-C5E71AE7E5C0.html\">here</a>.</span></li><li><span style=\"color: #003366;\">Restart the on-prem VR appliance or the hms service inside it</span></li></ul><p><strong>Additional Information and Support:</strong></p><ul><li><span style=\"color: #000000;\">For additional support, please utilize the chat function within the VMC Console, or by filing a request from MyVMware at <a class=\"external-link\" href=\"http://my.vmware.com/\" rel=\"nofollow\">http://my.vmware.com</a>.</span></li></ul><p>Thank you,<br/> The VMware Site Recovery Team</p></body></html>", "this is a comment.");

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.GetString(Currentlocale1, source1_1);
            String result2 = Translation.GetString(Currentlocale1, source1_2);
            String result3 = Translation.GetString(Currentlocale1, source1_3);
            Console.WriteLine("Received source 1: {0}", result1);
            Console.WriteLine("Received source 2: {0}", result2);
            Assert.AreEqual(TestDataConstant.value1, result1);
            Assert.AreEqual(TestDataConstant.value2, result2);
            Assert.AreEqual(TestDataConstant.value3, result3);

            Thread.Sleep(30000);
            String url1 = Common.GetComponentApi("contact", "latest");
            JObject jo1 = Common.HttpGetJson(url1);
            String value1 = Common.ParserJsonStringContent(jo1, "collect.message");

            String url2 = Common.GetComponentApi("CollectSourceComponent1", "latest");
            JObject jo2 = Common.HttpGetJson(url2);
            String value2 = Common.ParserJsonStringContent(jo2, "collect.argument");
            String value3 = Common.ParserJsonStringContent(jo2, TestDataConstant.key3);

            //Console.WriteLine("Received source: {0}", value);
            System.Console.WriteLine(value1);
            System.Console.WriteLine(value2);
            System.Console.WriteLine(value3);

            Assert.AreEqual(TestDataConstant.value1PseudoFromService, value1);
            Assert.AreEqual(TestDataConstant.value2PseudoFromService, value2);
            Assert.AreEqual(TestDataConstant.value3PseudoFromService, value3);



        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Source Collection_One string_Full Parameters")]
        public void SourceCollection_2_OneString_GetString_FullParameters()
        {
            
            Translation.SetCurrentLocale("en-US");
            String result4 = Translation.GetString("contact", TestDataConstant.key4, TestDataConstant.value4);
            String result2 = Translation.GetString("CollectSourceComponent2", TestDataConstant.key2, TestDataConstant.value2);
            String result3 = Translation.GetString("CollectSourceComponent2", TestDataConstant.key3, TestDataConstant.value3);
            Console.WriteLine("Received source 4: {0}", result4);
            Console.WriteLine("Received source 2: {0}", result3);
            Assert.AreEqual(TestDataConstant.value4, result4);
            Assert.AreEqual(TestDataConstant.value2, result2);
            Assert.AreEqual(TestDataConstant.value3, result3);

            Thread.Sleep(30000);
            String url1 = Common.GetComponentApi("contact", "latest");
            JObject jo1 = Common.HttpGetJson(url1);
            String value1 = Common.ParserJsonStringContent(jo1, TestDataConstant.key4);

            String url2 = Common.GetComponentApi("CollectSourceComponent2", "latest");
            JObject jo2 = Common.HttpGetJson(url2);
            String value2 = Common.ParserJsonStringContent(jo2, TestDataConstant.key2);
            String value3 = Common.ParserJsonStringContent(jo2, TestDataConstant.key3);

            //Console.WriteLine("Received source: {0}", value);
            System.Console.WriteLine(value1);
            System.Console.WriteLine(value2);
            System.Console.WriteLine(value3);
            Assert.AreEqual(TestDataConstant.value4PseudoFromService, value1);
            Assert.AreEqual(TestDataConstant.value2PseudoFromService, value2);
            Assert.AreEqual(TestDataConstant.value3PseudoFromService, value3);

            
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source Collection_One string_Existing_NotSendRequest")]
        public void SourceCollection_3_OneString_Existing()
        {

            
            ISource source2_1 = Translation.CreateSource("CollectSourceComponent1", "collect.argument", "{0} is the {1} day of a week.");
            String result2 = Translation.GetString("de", source2_1);

            Translation.SetCurrentLocale("ja");
            String result4 = Translation.GetString("contact", TestDataConstant.key4, TestDataConstant.value4);

            Assert.AreEqual(TestDataConstant.value2, result2);
            Assert.AreEqual(TestDataConstant.value4, result4);
            //Thread.Sleep(50000);

            //Util2.Init();
            //Release = Util2.Release();
            //bool Pseudo = Release.GetConfig().GetBoolValue(ConfigConst.KeyPseudo);
            //Console.WriteLine("pseudo value: {0}", Pseudo);
            //bool CollecSource = Release.GetConfig().GetBoolValue(ConfigConst.KeyCollect);
            //Console.WriteLine("Collect source value: {0}", CollecSource);

            

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source Collection_One string_Existing_updateValue")]
        public void SourceCollection_4_UpdateValue()
        {
            ISource source2_1 = Translation.CreateSource("CollectSourceComponent1", "collect.argument", "{0} is the {1} day of a week update.");
            String result2 = Translation.GetString("de", source2_1);

            Translation.SetCurrentLocale("ja");
            String result4 = Translation.GetString("contact", TestDataConstant.key4, "application message update");

            Assert.AreEqual("{0} is the {1} day of a week update.", result2);
            Assert.AreEqual("application message update", result4);

            Thread.Sleep(30000);
            String url1 = Common.GetComponentApi("contact", "latest");
            JObject jo1 = Common.HttpGetJson(url1);
            String value1 = Common.ParserJsonStringContent(jo1, TestDataConstant.key4);
            System.Console.WriteLine(value1);

            String url2 = Common.GetComponentApi("CollectSourceComponent1", "latest");
            JObject jo2 = Common.HttpGetJson(url2);
            String value2 = Common.ParserJsonStringContent(jo2, "collect.argument");
            System.Console.WriteLine(value2);
            

            //Console.WriteLine("Received source: {0}", value);
            Assert.AreEqual("#@application message update#@", value1);
            Assert.AreEqual("#@{0} is the {1} day of a week update.#@", value2);
           

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]//nead change
        [Description("Source Collection_One string_Existing_InENFile_NoChange")]
        public void SourceCollection_5_ExistingInENFile_NoChange()
        {
            

            Translation.SetCurrentLocale("zh-Hans");
            String resultzhCNFull = Translation.GetString("RESX", TestDataConstant.keyArg, TestDataConstant.valueArg, "argument verification");
            Assert.AreEqual("将{0}添加到对象。", resultzhCNFull);


        }
    }
}