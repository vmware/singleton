/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Threading;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SingletonClient;
using System.Diagnostics;


namespace CSharp
{
    [TestClass]
    public class GetLocaleMessagesfromcacheofflineexpired
    {

        private IReleaseMessages PM;
        private ITranslation Translation;
        private IRelease Release;
        private ISource SourceAbout;
        private ISource SourceError;
        private ISource SourceArgument;
        private ISource SourceHTMLTag;
        private ISource SourceHTMLTagWithSource;
        private String[] args;


        public GetLocaleMessagesfromcacheofflineexpired()
        {

            Utiloffline_disk_cache.Init();
            PM = Utiloffline_disk_cache.Messages();
            //Release = Util.Release();
            Translation = Utiloffline_disk_cache.Translation();
            //SourceAbout = Translation.CreateSource("about", "about.message", "Your application description page.", "this is comment.");
            SourceAbout = Translation.CreateSource("about", "about.message");
            SourceError = Translation.CreateSource("RESX", "Resx.sample-subnet-wizard.more-than-one-subnet-vpc-subnet-wizard.more-than-one-subnet-selected-error");
            //SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument", "Operator '{0}' is not support for property '{1}'.");
            SourceArgument = Translation.CreateSource("DefaultComponent", "message.argument");
            SourceHTMLTag = Translation.CreateSource("DefaultComponent", "message.url");
            SourceHTMLTagWithSource = Translation.CreateSource("DefaultComponent", "message.url", "<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>The scheduled maintenance has started.</strong></span></p><p>Important information about maintenance can be found here: <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>");
            args = new string[] { "+", "moto" };


        }



        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration()
        {

            Translation.SetCurrentLocale("en");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", "Resx.only-offline");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            Translation.SetCurrentLocale("en");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result2 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result2);


            Thread.Sleep(50000);

            Translation.SetCurrentLocale("en");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result11);
            Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("en").GetString("RESX", "Resx.only-offline");
            Console.WriteLine(result7);


            //try
            //{
            //    Assert.AreEqual("test_value_change", result11);
            //}
            ////catch (Exception ex)
            ////{
            ////    Console.WriteLine(ex.Message);
            ////}
            //finally
            //{
            //    Process proc1 = null;
            //    try
            //    {
            //        string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
            //        proc1 = new Process();
            //        proc1.StartInfo.WorkingDirectory = targetDir;
            //        proc1.StartInfo.FileName = "RevertString_de.bat";
            //        // proc1.StartInfo.Arguments = string.Format("3");//this is argument
            //        proc1.StartInfo.CreateNoWindow = true;
            //        proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
            //        proc1.Start();
            //        proc1.WaitForExit();
            //    }
            //    catch (Exception ex)
            //    {
            //        Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            //    }

            //}
         }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration1()
        {

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", "Resx.only-offline");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            String result8 = PM.GetLocaleMessages("de").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result8);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path1);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "test.bat";
                //proc.StartInfo.Arguments = string.Format("3");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }


            Thread.Sleep(65000);

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("RESX", "Resx.only-offline");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("RESX", "Resx.only-offline");
            Console.WriteLine("full param transaltion: {0}", result11);
            Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.only-offline");
            Console.WriteLine(result7);


            //try
            //{
            //    Assert.AreEqual("test_value_change", result11);
            //}
            ////catch (exception ex)
            ////{
            ////    console.writeline(ex.message);
            ////}
            //finally
            //{
            //    Process proc1 = null;
            //    try
            //    {
            //        string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
            //        proc1 = new Process();
            //        proc1.StartInfo.WorkingDirectory = targetDir;
            //        proc1.StartInfo.FileName = "RevertString_de.bat";
            //        // proc1.StartInfo.Arguments = string.Format("3");//this is argument
            //        proc1.StartInfo.CreateNoWindow = true;
            //        proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
            //        proc1.Start();
            //        proc1.WaitForExit();
            //    }
            //    catch (Exception ex)
            //    {
            //        Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            //    }

            //}
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration2()
        {

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", "Resx.only-offline");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            String result8 = PM.GetLocaleMessages("de").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result8);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path1);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_de.bat";
                //proc.StartInfo.Arguments = string.Format("3");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }
            Thread.Sleep(2000);
            String result9 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.only-offline");
            Console.WriteLine(result9);

            Thread.Sleep(55000);

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result11);
            Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("de").GetString("RESX", "Resx.only-offline");
            Console.WriteLine(result7);


            //try
            //{
            //    Assert.AreEqual("test_value_change", result11);
            //}
            ////catch (exception ex)
            ////{
            ////    console.writeline(ex.message);
            ////}
            //finally
            //{
            //    Process proc1 = null;
            //    try
            //    {
            //        string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
            //        proc1 = new Process();
            //        proc1.StartInfo.WorkingDirectory = targetDir;
            //        proc1.StartInfo.FileName = "RevertString_de.bat";
            //        // proc1.StartInfo.Arguments = string.Format("3");//this is argument
            //        proc1.StartInfo.CreateNoWindow = true;
            //        proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
            //        proc1.Start();
            //        proc1.WaitForExit();
            //    }
            //    catch (Exception ex)
            //    {
            //        Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            //    }

            //}
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration3()
        {

            Translation.SetCurrentLocale("da");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            String result8 = PM.GetLocaleMessages("fr").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result8);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path1);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_fr.bat";
                //proc.StartInfo.Arguments = string.Format("3");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }
            Thread.Sleep(2000);
            String result9 = PM.GetLocaleMessages("fr").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result9);

            Thread.Sleep(55000);

            Translation.SetCurrentLocale("da");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result11);
            Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("fr").GetString("RESX", "RESX.ARGUMENT");
            Console.WriteLine(result7);


            //try
            //{
            //    Assert.AreEqual("test_value_change", result11);
            //}
            ////catch (exception ex)
            ////{
            ////    console.writeline(ex.message);
            ////}
            //finally
            //{
            //    Process proc1 = null;
            //    try
            //    {
            //        string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
            //        proc1 = new Process();
            //        proc1.StartInfo.WorkingDirectory = targetDir;
            //        proc1.StartInfo.FileName = "RevertString_de.bat";
            //        // proc1.StartInfo.Arguments = string.Format("3");//this is argument
            //        proc1.StartInfo.CreateNoWindow = true;
            //        proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
            //        proc1.Start();
            //        proc1.WaitForExit();
            //    }
            //    catch (Exception ex)
            //    {
            //        Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            //    }

            //}
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration6()
        {

            Translation.SetCurrentLocale("fr");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("addcomponent", "about.message");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            String result8 = PM.GetLocaleMessages("fr").GetString("addcomponent", "about.message");
            Console.WriteLine(result8);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "Addcomponent.bat";
                //proc.StartInfo.Arguments = string.Format("3");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }
            Thread.Sleep(2000);
            String result9 = PM.GetLocaleMessages("fr").GetString("addcomponent", "about.message");
            Console.WriteLine(result9);

            Thread.Sleep(2000);

            Translation.SetCurrentLocale("fr");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("addcomponent", "about.message");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("addcomponent", "about.message");
            Console.WriteLine("full param transaltion: {0}", result11);
            Thread.Sleep(2000);
            String result7 = PM.GetLocaleMessages("fr").GetString("addcomponent", "about.message");
            Console.WriteLine(result7);
            Thread.Sleep(30000);
            String result12 = Translation.GetString("addcomponent", "about.message");
            Console.WriteLine("full param transaltion: {0}", result12);
            Thread.Sleep(2000);
            String result13 = PM.GetLocaleMessages("fr").GetString("addcomponent", "about.message");
            Console.WriteLine(result13);
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("the component only exist in offline bundle")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration4()
        {

            Translation.SetCurrentLocale("es");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("RESXPPP", "RESX.ARGUMENT");
            Console.WriteLine("full param transaltion: {0}", result1);
            Thread.Sleep(2000);
            String result8 = PM.GetLocaleMessages("es").GetString("RESXPPP", "RESX.ARGUMENT");
            Console.WriteLine(result8);
   

        }




    }
}