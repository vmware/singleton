/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Threading;

namespace CSharp
{
    [TestClass]
    public class GetComponentList
    {
        

        private IReleaseMessages PM;
        private IConfig CM;
        private ITranslation Translation;
        private ISource Sourcetest;


        public GetComponentList()
        {
            Utiloffline_disk.Init();         
            PM = Utiloffline_disk.Messages();
            CM = Utiloffline_disk.Config();
            Translation = Utiloffline_disk.Translation();
            Sourcetest = Translation.CreateSource("RESX", "RESX.ARGUMENT");

        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get component list")]
        public void ProductComponentList_bug659()
        {

            Translation.SetCurrentLocale("ru");
            String result1 = Translation.GetString("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            Console.WriteLine(result1);
            List<string> componentlist1 = PM.GetLocaleMessages("ru").GetComponentList();
            String result2 = Common.ParseListStringContent(componentlist1);
            Console.WriteLine(result2);
            Assert.AreEqual("DefaultComponent, RESXPPP, RESX, about, contact", result2);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("Get translation for local no exist and local is't default_locale")]
        public void GetLocaleMessagesOfflinebug856()
        {

            Translation.SetCurrentLocale("da");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, Sourcetest, "obj");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("ddFügen Sie dem Objekt obj hinzu.", result1);
        }




    }
}
