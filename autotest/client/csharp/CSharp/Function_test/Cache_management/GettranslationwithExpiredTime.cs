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
    public class GetLocaleMessagesfromcachewithExpiredTime
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


        public GetLocaleMessagesfromcachewithExpiredTime()
        {

            UtilForCacheExpired.Init();
            PM = UtilForCacheExpired.Messages();
            //Release = Util.Release();
            Translation = UtilForCacheExpired.Translation();
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
        [Description("cache is persisted when it is expired but service doesn't has updates")]
        public void Cache_persisted_service_noupdatewith_argument()
        {
            Translation.SetCurrentLocale("es");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("es ransaltion1: {0}", result1);
            //Assert.AreEqual("test_value", result1);
            Thread.Sleep(55000);
            String result2 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("es transaltion1: {0}", result2);
            Thread.Sleep(5000);
            String result3 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("es transaltion1: {0}", result3);
            // Assert.AreEqual("test_value", result2);
            Process proc = null;
            try
            {
                Console.WriteLine(TestDataConstant.bat_path);
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_es.bat";
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

            Thread.Sleep(5000);
            Translation.SetCurrentLocale("es");
            String Currentlocale3 = Translation.GetCurrentLocale();
            String result4 = Translation.Format(Currentlocale3, SourceAbout, "aaa");
            Console.WriteLine("es transaltion1: {0}", result4);
            Assert.AreEqual("test_value", result3);
            Thread.Sleep(5000);
            String result5 = Translation.Format(Currentlocale1, SourceAbout, "aaa");

            Thread.Sleep(40000);
            String result6 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Thread.Sleep(5000);
            String result7 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("es transaltion1: {0}", result7);
            try
            {
                Assert.AreEqual("test_value", result2);
                Assert.AreEqual("test_value", result5);
                Assert.AreEqual("test_value_change", result7);
            }
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_es.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }
            }

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("cache is persisted when it is expired but service doesn't has updates")]
        public void Cache_persisted_service_noupdatewith_FullParameters()
        {
            Translation.SetCurrentLocale("da");
            String result1 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Thread.Sleep(55000);
            Translation.SetCurrentLocale("da");
            String result2 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result2);
            Thread.Sleep(5000);
            String result3 = Translation.GetString("about", "about.message");
            //Assert.AreEqual("test_value", result2);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_da.bat";
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

            Thread.Sleep(5000);
            Translation.SetCurrentLocale("da");
            String result4 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result4);
            //Assert.AreEqual("test_value", result3);
            Thread.Sleep(5000);
            Translation.SetCurrentLocale("da");
            String result5 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result5);
            Thread.Sleep(40000);
            Translation.SetCurrentLocale("da");
            String result6 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result6);
            Thread.Sleep(5000);
            Translation.SetCurrentLocale("da");
            String result7 = Translation.GetString("about", "about.message");
            Console.WriteLine("ar transaltion1: {0}", result7);
            try
            {
                Assert.AreEqual("test_value", result3);
                Assert.AreEqual("test_value", result5);
                Assert.AreEqual("test_value_change", result7);
            }
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_da.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }
            }
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("cache is persisted when it is expired but service doesn't has updates")]
        public void Cache_persisted_service_noupdatewith_TwoParameters()
        {
            String result1 = Translation.GetString("pl", SourceAbout);
            Console.WriteLine("fr-BE transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Thread.Sleep(55000);
            String result2 = Translation.GetString("pl", SourceAbout);
            Console.WriteLine("fr-BE transaltion1: {0}", result2);
            //Assert.AreEqual("test_value", result2);
            Thread.Sleep(5000);
            String result3 = Translation.GetString("pl", SourceAbout);
            Console.WriteLine("fr-BE transaltion1: {0}", result3);
            //Assert.AreEqual("test_value", result3);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_pl.bat";
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
            Thread.Sleep(5000);
            String result4 = Translation.GetString("pl", SourceAbout);
            Console.WriteLine("fr-BE transaltion12: {0}", result4);
            Thread.Sleep(5000);
            String result5 = Translation.GetString("pl", SourceAbout);
            Console.WriteLine("fr-BE transaltion13: {0}", result5);
            //Assert.AreEqual("test_value", result3);
            Thread.Sleep(40000);
            String result6 = Translation.GetString("pl", SourceAbout); ;
            Console.WriteLine("fr-BE transaltion1: {0}", result6);
            Thread.Sleep(5000);
            String result7 = Translation.GetString("pl", SourceAbout); ;
            Console.WriteLine("fr-BE transaltion1: {0}", result7);
            try
            {
                Assert.AreEqual("test_value", result3);
                Assert.AreEqual("test_value", result5);
                Assert.AreEqual("test_value_change", result7);
            }
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_pl.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }
            }
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_TwoParameters_cacheexpiration()
        {
            String result1 = Translation.GetString("ja", SourceAbout);
            Console.WriteLine("ja transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_ja.bat";
                proc.StartInfo.Arguments = string.Format("20");//this is argument
                //proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }
            Thread.Sleep(50000);

            String result10 = Translation.GetString("ja", SourceAbout);
            Console.WriteLine("zh-Hans transaltion1: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("ja", SourceAbout);
            Console.WriteLine("zh-Hans transaltion1: {0}", result11);
            //Assert.AreEqual("应用程序说明页。", result10);
            try
            {
                Assert.AreEqual("test_value_change", result11);
            }

            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_ja.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }

            }
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("cache is persisted when it isn't expired")]
        public void GetLocaleMessages_ExistingLanguage_TwoParameters_cachenotexpiration()
        {
            String result1 = Translation.GetString("zh-Hant", SourceAbout);
            Console.WriteLine("zh-Hant transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_zh-Hant.bat";
                proc.StartInfo.Arguments = string.Format("20");//this is argument
                //proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }
            Thread.Sleep(5000);

            String result10 = Translation.GetString("zh-Hant", SourceAbout);
            Console.WriteLine("zh-Hant transaltion1: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("zh-Hant", SourceAbout);
            Console.WriteLine("zh-Hans transaltion1: {0}", result11);
            //Assert.AreEqual("应用程序说明页。", result10);
            try
            {
                Assert.AreEqual("test_value", result11);
            }

            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_zh-Hant.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }

            }
        }


        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cacheexpiration()
        {

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result1 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_de.bat";
                proc.StartInfo.Arguments = string.Format("20");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }

            Thread.Sleep(50000);

            Translation.SetCurrentLocale("de");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result11);

            try
            {
                Assert.AreEqual("test_value_change", result11);
            }
            //catch (Exception ex)
            //{
            //    Console.WriteLine(ex.Message);
            //}
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_de.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }

            }
        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("cache is persisted when it isn't expired")]
        public void GetLocaleMessages_ExistingLanguage_FullParameters_cachenotexpiration()
        {

            Translation.SetCurrentLocale("fr");
            String result1 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result1);
            Assert.AreEqual("test_value", result1);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_fr.bat";
                proc.StartInfo.Arguments = string.Format("20");//this is argument
                proc.StartInfo.CreateNoWindow = true;
                proc.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                proc.Start();
                proc.WaitForExit();
            }
            catch (Exception ex)
            {
                Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
            }

            Thread.Sleep(5000);

            Translation.SetCurrentLocale("fr");
            //String result1 = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
            String result10 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result10);
            Thread.Sleep(5000);
            String result11 = Translation.GetString("about", "about.message");
            Console.WriteLine("full param transaltion: {0}", result11);
            //Thread.Sleep(20000);
            //String result12 = Translation.GetString("about", "about.message");
            //Console.WriteLine("full param transaltion: {0}", result12);

            try
            {
                Assert.AreEqual("test_value", result11);
            }
            //catch (Exception ex)
            //{
            //    Console.WriteLine(ex.Message);
            //}
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_fr.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }

            }
        }
        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("new cache is downloaded when it is expired and service has updates")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format_cacheexpiration()
        {

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);

            //List<ISource> srcList = new List<ISource>();
            //srcList.Add(UtilForCache.Source("about", "about.message", "tioxxx"));
            //UtilForCache.Translation().SendSource(srcList);

            //Thread.Sleep(20000);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_zh-Hans.bat";
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

            Thread.Sleep(10000);

            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale10 = Translation.GetCurrentLocale();
            String result10 = Translation.Format(Currentlocale10, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion10: {0}", result10);
            Thread.Sleep(5000);
            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale12 = Translation.GetCurrentLocale();
            String result12 = Translation.Format(Currentlocale12, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion10: {0}", result12);
            Thread.Sleep(25000);
            Translation.SetCurrentLocale("zh-Hans");
            String Currentlocale13 = Translation.GetCurrentLocale();
            String result13 = Translation.Format(Currentlocale13, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion10: {0}", result13);
            Thread.Sleep(3000);
            String Currentlocale11 = Translation.GetCurrentLocale();
            String result11 = Translation.Format(Currentlocale11, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion11: {0}", result11);
            try
            {
                Assert.AreEqual("test_value_change", result11);
            }
            //catch (Exception ex)
            //{
            //    Console.WriteLine(ex.Message);
            //}
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_zh-Hans.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }
            }

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("get translation with nonexistentlanguale and update whith expiration")]
        public void GetLocaleMessages_nonexistentLanguage_Argument_Format_cacheexpiration()
        {

            Translation.SetCurrentLocale("tr");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion1: {0}", result1);
            //Assert.AreEqual("Your application description page.", result1);

            //List<ISource> srcList = new List<ISource>();
            //srcList.Add(UtilForCache.Source("about", "about.message", "tioxxx"));
            //UtilForCache.Translation().SendSource(srcList);

            //Thread.Sleep(20000);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_tr.bat";
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

            Thread.Sleep(35000);

            Translation.SetCurrentLocale("tr");
            String Currentlocale10 = Translation.GetCurrentLocale();
            String result10 = Translation.Format(Currentlocale10, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion10: {0}", result10);
            //Assert.AreEqual("test_value", result10);
            Thread.Sleep(5000);
            Translation.SetCurrentLocale("tr");
            String Currentlocale11 = Translation.GetCurrentLocale();
            String result11 = Translation.Format(Currentlocale11, SourceAbout, "aaa");
            Console.WriteLine("zh-Hans transaltion11: {0}", result11);
            Assert.AreEqual("test_value_change", result11);

        }

        [TestMethod]
        [Priority(1)]
        [TestCategory("")]
        [Description("cache is persisted when it isn't expired")]
        public void GetLocaleMessages_ExistingLanguage_Argument_Format_cachenotexpiration()
        {

            Translation.SetCurrentLocale("ko");
            String Currentlocale1 = Translation.GetCurrentLocale();
            String result1 = Translation.Format(Currentlocale1, SourceAbout, "aaa");
            Console.WriteLine("ko transaltion1: {0}", result1);
            Assert.AreEqual("test_value", result1);

            //List<ISource> srcList = new List<ISource>();
            //srcList.Add(UtilForCache.Source("about", "about.message", "tioxxx"));
            //UtilForCache.Translation().SendSource(srcList);

            //Thread.Sleep(20000);
            Process proc = null;
            try
            {
                string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                proc = new Process();
                proc.StartInfo.WorkingDirectory = targetDir;
                proc.StartInfo.FileName = "ModifyString_ko.bat";
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

            Thread.Sleep(5000);

            Translation.SetCurrentLocale("ko");
            String Currentlocale10 = Translation.GetCurrentLocale();
            String result10 = Translation.Format(Currentlocale10, SourceAbout, "aaa");
            Console.WriteLine("ko transaltion10: {0}", result10);
            Thread.Sleep(5000);
            String Currentlocale11 = Translation.GetCurrentLocale();
            String result11 = Translation.Format(Currentlocale11, SourceAbout, "aaa");
            Console.WriteLine("ko transaltion11: {0}", result11);
            try
            {
                Assert.AreEqual("test_value", result11);
            }
            //catch (Exception ex)
            //{
            //    Console.WriteLine(ex.Message);
            //}
            finally
            {
                Process proc1 = null;
                try
                {
                    string targetDir = string.Format(TestDataConstant.bat_path);//this is where testChange.bat lies
                    proc1 = new Process();
                    proc1.StartInfo.WorkingDirectory = targetDir;
                    proc1.StartInfo.FileName = "RevertString_ko.bat";
                    // proc1.StartInfo.Arguments = string.Format("3");//this is argument
                    proc1.StartInfo.CreateNoWindow = true;
                    proc1.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;//Set the DOS window not to show here
                    proc1.Start();
                    proc1.WaitForExit();
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Exception Occurred :{0},{1}", ex.Message, ex.StackTrace.ToString());
                }
            }

        }

        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation for fallback language")]
        //public void GetLocaleMessagesForFallbackLanguage_Bug_2280()
        //{
        //    //Source = Translation.CreateSource("about", "about.message");

        //    //String Currentlocale = Translation.GetCurrentLocale();
        //    //Console.WriteLine(Currentlocale);

        //    String resultzhCN = Translation.GetString("zh-CN", SourceAbout);
        //    Console.WriteLine("zh-CN transaltion: {0}", resultzhCN);
        //    Assert.AreEqual("应用程序说明页。", resultzhCN);
        //    Translation.SetCurrentLocale("zh-CN");
        //    String resultzhCNFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
        //    Assert.AreEqual("应用程序说明页。", resultzhCNFull);
        //    String resultzhCNFormat = Translation.Format("zh-CN", SourceArgument, "+", "moto");
        //    Assert.AreEqual("运算符'+'不支持属性'moto'。", resultzhCNFormat);


        //    String resultfrCA = Translation.GetString("fr-CA", SourceAbout);
        //    Console.WriteLine("fr-CA transaltion: {0}", resultfrCA);
        //    Assert.AreEqual("La page Description de l'application.", resultfrCA);
        //    Translation.SetCurrentLocale("fr-CA");
        //    String resultfrCAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
        //    Assert.AreEqual("La page Description de l'application.", resultfrCAFull);
        //    String resultfrFormat = Translation.Format("fr-CA", SourceArgument, args);
        //    Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultfrFormat);
        //}


        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation with upper case language")]
        //public void GetLocaleMessages_UpperCaseLanguage_FR_Bug_2287()
        //{

        //    String resultFR = Translation.GetString("FR", SourceAbout);
        //    Console.WriteLine("FR transaltion: {0}", resultFR);
        //    Assert.AreEqual("La page Description de l'application.", resultFR);
        //    Translation.SetCurrentLocale("FR");
        //    String resultFRFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
        //    Console.WriteLine("FR transaltion: {0}", resultFRFull);
        //    Assert.AreEqual("La page Description de l'application.", resultFRFull);
        //    String resultFRFormat = Translation.Format("FR", SourceArgument, args);
        //    Assert.AreEqual("Der Operator '+' unterstützt die Eigenschaft 'moto' nicht.", resultFRFormat);


        //}






        //[TestMethod]
        //[Priority(1)]
        //[TestCategory("")]
        //[Description("Get translation for null language")]
        //public void GetLocaleMessages_Null_Language()
        //{


        //    String resultDA = Translation.GetString(null, SourceAbout);
        //    Console.WriteLine("DA transaltion: {0}", resultDA);
        //    Assert.AreEqual("Your application description page.", resultDA);
        //    Translation.SetCurrentLocale(null);
        //    String resultDAFull = Translation.GetString("about", "about.message", "Your application description page.", "this is comment.");
        //    Assert.AreEqual("Your application description page.", resultDAFull);
        //    String resultDAFormat = Translation.Format(null, SourceArgument, "+", "moto");
        //    Assert.AreEqual("Operator '+' is not support for property 'moto'.", resultDAFormat);

        //}



        //[TestMethod]
        //[Priority(2)]
        //[TestCategory("")]
        //[Description("Get translation for ISource without source param about specail string")]
        //public void GetLocaleMessages_ExistingLanguage_SpecailString_Bug_2257()
        //{

        //    Translation.SetCurrentLocale("zh-Hans");
        //    String Currentlocale1 = Translation.GetCurrentLocale();
        //    String result1 = Translation.GetString(Currentlocale1, SourceHTMLTag);
        //    Console.WriteLine("zh-Hans transaltion1: {0}", result1);
        //    Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result1);
        //    String result2 = Translation.GetString("DefaultComponent", "message.url");
        //    Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result2);
        //    String result3 = Translation.Format(Currentlocale1, SourceHTMLTag);
        //    Assert.AreEqual("<html><body><p><span style=\"color: rgb(255,0,0);\"><strong>预定的维护已开始。</strong></span></p><p>有关维护的重要信息，可以在这里找到： <a class=\"external-link\" href=\"http://www.vmware.com\">https://www.vmware.com</a><strong><br/></strong></p></body></html>", result3);


        //}

    }
}