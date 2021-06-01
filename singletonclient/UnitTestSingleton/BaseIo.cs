/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Globalization;
using System.Reflection;
using System.Resources;
using System.Text.RegularExpressions;

namespace UnitTestSingleton
{
    class BaseIo : ISingletonBaseIo, IAccessService
    {
        private static BaseIo _instance = new BaseIo();
        public static BaseIo obj()
        {
            return _instance;
        }

        private ResourceManager resourceManager;
        private Hashtable responseData = new Hashtable();
        private Hashtable testData = new Hashtable();

        private string lastConsoleText;

        public BaseIo()
        {
            Assembly assembly = typeof(BaseIo).Assembly;
            resourceManager = new ResourceManager("UnitTestSingleton.testdata.TestData", assembly);

            CultureInfo cultureInfo = new System.Globalization.CultureInfo("en-US");
            ResourceSet resourceSet = resourceManager.GetResourceSet(cultureInfo, true, true);

            I18N.GetExtension().RegisterAccessService(this, "test");

            string raw = (string)resourceManager.GetObject("http_response");

            for (int k=1; k<2; k++)
            {
                string product = "CSHARP" + k;
                string text = raw.Replace("$PRODUCT", product).Replace("$VERSION", "1.0.0");
                string[] parts = Regex.Split(text, "---api---.*[\r|\n]*");
                for (int i = 0; i < parts.Length; i++)
                {
                    if (parts[i].Trim().Length == 0)
                    {
                        continue;
                    }
                    string[] segs = Regex.Split(parts[i], "---data---.*[\r|\n]*");
                    string[] lines = Regex.Split(segs[0], "\n");
                    responseData.Add(lines[0].Trim(), segs[1].Trim());
                }
            }

            PrepareTestData("test_define");
            PrepareTestData("test_define2");
        }

        private void PrepareTestData(string resName)
        {
            string raw = (string)resourceManager.GetObject(resName);
            string[] parts = Regex.Split(raw, "---test---.*[\r|\n]*");
            for (int i = 0; i < parts.Length; i++)
            {
                if (parts[i].Trim().Length == 0)
                {
                    continue;
                }
                string[] segs = Regex.Split(parts[i], "---data---.*[\r|\n]*");
                SingletonParserProperties p = new SingletonParserProperties();
                Hashtable ht = p.Parse(segs[0]);
                string name = (string)ht["NAME"];
                testData.Add(name, ht);

                for(int k=1; k<segs.Length; k++)
                {
                    Hashtable htTest = p.Parse(segs[k]);
                    ht.Add("_test_" + k, htTest);
                }
            }
        }

        public Hashtable GetTestData(string name)
        {
            return (Hashtable)testData[name];
        }

        public string GetTestResource(string name)
        {
            string str = null;
            object obj = resourceManager.GetObject(name);
            string type = obj.GetType().ToString();
            if (type.Equals("System.Byte[]"))
            {
                str = System.Text.Encoding.UTF8.GetString((System.Byte[])obj);
            }
            else
            {
                str = (string)obj;
            }

            return str.Trim();
        }

        public void ConsoleWriteLine(string text)
        {
            lastConsoleText = text;
            Console.WriteLine(text);
        }

        /// <summary>
        /// ISingletonBaseIo
        /// </summary>
        public string GetLastConsoleText()
        {
            return lastConsoleText;
        }

        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpGet(string url, Hashtable headers)
        {
            string response = (string)responseData["[GET]" + url];
            if (response == null) {
                response = "";
            }
            return response;
        }

        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpPost(string url, string text, Hashtable headers)
        {
            string reponse = (string)responseData["[POST]" + url];
            return reponse;
        }
    }
}
