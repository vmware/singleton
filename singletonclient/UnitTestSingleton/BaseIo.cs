/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using SingletonClient;
using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Reflection;
using System.Resources;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace UnitTestSingleton
{
    class BaseIo : ISingletonBaseIo, IAccessService
    {
        private static BaseIo _instance;
        public static BaseIo obj()
        {
            if (_instance == null)
            {
                _instance = new BaseIo();
            }
            return _instance;
        }

        private ResourceManager resourceManager;
        private Hashtable ht;
        private Hashtable responseData = new Hashtable();

        private string lastConsoleText;

        public BaseIo()
        {
            Assembly assembly = typeof(BaseIo).Assembly;
            resourceManager = new ResourceManager("UnitTestSingleton.testdata.TestData", assembly);

            CultureInfo cultureInfo = new System.Globalization.CultureInfo("en-US");
            ResourceSet resourceSet = resourceManager.GetResourceSet(cultureInfo, true, true);

            string text = (string)resourceManager.GetObject("http_list");
            SingletonParserProperties p = new SingletonParserProperties();
            ht = p.Parse(text);

            I18N.GetExtension().RegisterAccessService(this, "test");

            text = (string)resourceManager.GetObject("http_response");
            text = text.Replace("$PRODUCT", "CSHARP").Replace("$VERSION", "1.0.0");
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
