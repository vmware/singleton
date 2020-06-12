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
    public class SourceCollectTrue
    {


        private ITranslation Translation;
        private IRelease Release;


        public SourceCollectTrue()
        {

            UtilCollectTrue.Init();
            Release = UtilCollectTrue.Release();
            Translation = UtilCollectTrue.Translation();

        }

        [TestMethod]
        [Priority(0)]
        [TestCategory("")]
        [Description("Source Collection_One string")]
        public void SourceCollection_OneString_GetString()
        {
            
            List<ISource> srcList = new List<ISource>();
            srcList.Add(UtilCollectTrue.Source("CollectSourceComponentTrue", "SourceList_collect.message.ture", "Source list application"));
            UtilCollectTrue.Translation().SendSource(srcList);

            Thread.Sleep(30000);

            String url = Common.GetComponentApi("CollectSourceComponentTrue", "latest");
            JObject jo = Common.HttpGetJson(url);
            String value = Common.ParserJsonStringContent(jo, "SourceList_collect.message.ture");

            Console.WriteLine("Received source: {0}", value);
            Assert.AreEqual("#@Source list application#@", value);



        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source Collection_multiple strings")]
        public void SourceCollection_MultiString_GetString()
        {
            
            
            List<ISource> srcList = new List<ISource>();
            srcList.Add(UtilCollectTrue.Source("CollectSourceComponentTrue", "SourceList_collect.argument", "Source list {0} is the {1} day of a week."));
            srcList.Add(UtilCollectTrue.Source("CollectSourceComponentTrue", "SourceList_collect.00000000-0000-0000-0000-000000000000.templates_Draas-SSLCertificateRenew-completed.text", "<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager.</p></body></html>", "this is a comment."));//</html>
            UtilCollectTrue.Translation().SendSource(srcList);

            Thread.Sleep(30000);


            String url = Common.GetComponentApi("CollectSourceComponentTrue", "latest");
            JObject jo = Common.HttpGetJson(url);
            String value1 = Common.ParserJsonStringContent(jo, "SourceList_collect.argument");
            Assert.AreEqual("#@Source list {0} is the {1} day of a week.#@", value1);
            String value2 = Common.ParserJsonStringContent(jo, "SourceList_collect.00000000-0000-0000-0000-000000000000.templates_Draas-SSLCertificateRenew-completed.text");
            Assert.AreEqual("#@<html><body><p>Replacement of SSL Certificates of the Site Recovery Manager.</p></body></html>#@", value2);


            
        }

        



    }
}