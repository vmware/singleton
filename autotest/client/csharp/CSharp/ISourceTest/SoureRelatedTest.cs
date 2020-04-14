/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using System.Threading;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;

namespace CSharp
{
    [TestClass]
    public class SourceRelatedTest
    {


        private ITranslation Translation;
        private IRelease Release;
        private ISource Source;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;


        public SourceRelatedTest()
        {
            
            UtilAllFalse.Init();
            Release = UtilAllFalse.Release();
            Translation = UtilAllFalse.Translation();


            //SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", "Add {0} to the object.", "argument verification");
            ////SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            //SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            //SourceHTMLTag = Translation.CreateSource("RESX", "Resx-message.URL");
            //SourceHTMLTagWithSource = Translation.CreateSource("RESX", "Resx-message.URL", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source with required files: component and key")]
        public void Source_component_key()
        {

            SourceArgument = Translation.CreateSource("RESX", "Resx-message.URL");

            String component = SourceArgument.GetComponent();
            Assert.AreEqual("RESX", component);

            String key = SourceArgument.GetKey();
            Assert.AreEqual("Resx-message.URL", key);

            String source = SourceArgument.GetSource();
            Assert.AreEqual(TestDataConstant.valueURL, source);

            String comment = SourceArgument.GetComment();
            Assert.AreEqual(null, comment);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source with required files: component,key,value,comment")]
        public void Source_component_key_comment()
        {

            SourceArgument = Translation.CreateSource("RESX", "RESX.ARGUMENT", null, "argument verification");

            String component = SourceArgument.GetComponent();
            Assert.AreEqual("RESX", component);

            String key = SourceArgument.GetKey();
            Assert.AreEqual("RESX.ARGUMENT", key);

            String source = SourceArgument.GetSource();
            Assert.AreEqual(TestDataConstant.valueArg, source);

            String comment = SourceArgument.GetComment();
            Assert.AreEqual("argument verification", comment);


        }

        

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source with required files: component,key,value,comment")]
        public void Source_component_key_value_comment()
        {

            SourceArgument = Translation.CreateSource("RESX", TestDataConstant.keyError, TestDataConstant.valueError, null);

            String component = SourceArgument.GetComponent();
            Assert.AreEqual("RESX", component);

            String key = SourceArgument.GetKey();
            Assert.AreEqual(TestDataConstant.keyError, key);

            String source = SourceArgument.GetSource();
            Assert.AreEqual(TestDataConstant.valueError, source);

            String comment = SourceArgument.GetComment();
            Assert.AreEqual(null, comment);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source with required files: component,key are not existed")]
        public void Source_component_key_notExisting()
        {

            SourceArgument = Translation.CreateSource("ABC", "abc", TestDataConstant.valueError, null);

            String component = SourceArgument.GetComponent();
            Assert.AreEqual("ABC", component);

            String key = SourceArgument.GetKey();
            Assert.AreEqual("abc", key);

            String source = SourceArgument.GetSource();
            Assert.AreEqual(TestDataConstant.valueError, source);

            String comment = SourceArgument.GetComment();
            Assert.AreEqual(null, comment);


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source return null if required files are null and can't get source properties.")]
        public void Source_all_null_bug_2297()
        {

            SourceArgument = Translation.CreateSource(null, null, null, null);
            Assert.AreEqual(null, SourceArgument);

            try
            {
                String component = SourceArgument.GetComponent();
                String key = SourceArgument.GetKey();
                String source = SourceArgument.GetSource();
                String comment = SourceArgument.GetComment();
            }
            catch(System.NullReferenceException e)
            {
                Console.WriteLine("Source return null if required files are null and can't get source properties.");
            }


        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Source with required files: empty")]
        public void Source_all_empty()
        {

            SourceArgument = Translation.CreateSource("", "", "", "");

            String component = SourceArgument.GetComponent();
            Assert.AreEqual("", component);

            String key = SourceArgument.GetKey();
            Assert.AreEqual("", key);

            String source = SourceArgument.GetSource();
            Assert.AreEqual("", source);

            String comment = SourceArgument.GetComment();
            Assert.AreEqual("", comment);


        }

        
    }
}