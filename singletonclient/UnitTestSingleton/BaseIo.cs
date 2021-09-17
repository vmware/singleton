/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using SingletonClient;
using SingletonClient.Implementation;
using SingletonClient.Implementation.Support;
using System;
using System.Collections;
using System.Reflection;
using System.Resources;
using System.Text.RegularExpressions;
using System.Threading;


namespace UnitTestSingleton
{
    class BaseIo : ISingletonBaseIo, IAccessService
    {
        private static BaseIo _instance = new BaseIo();
        public static BaseIo obj()
        {
            return _instance;
        }

        private readonly ResourceManager resourceManager;
        private readonly Hashtable responseData = new Hashtable();

        private string lastConsoleText;

        public BaseIo()
        {
            Assembly assembly = typeof(BaseIo).Assembly;
            resourceManager = new ResourceManager("UnitTestSingleton.testdata.res.TestData", assembly);

            I18N.GetExtension().RegisterAccessService(this, "test");
        }

        public string LoadResourceText(string name)
        {
            string str;
            object obj = resourceManager.GetObject(name);
            string type = obj.GetType().ToString();
            if (type.Equals("System.Byte[]"))
            {
                str = SingletonUtil.ConvertToText((System.Byte[])obj);
            }
            else
            {
                str = (string)obj;
            }

            return str.Trim();
        }

        public void LoadResponse(string apiData)
        {
            string[] parts = Regex.Split(apiData, "---data---.*[\r|\n]*");
            string[] segs = Regex.Split(parts[0], "---header---.*[\r|\n]*");
            string[] lines = Regex.Split(segs[0], "\n");

            string key = lines[0].Trim();
            if(segs.Length > 1 && segs[1].Trim().Length > 2)
            {
                JObject header = JObject.Parse(segs[1]);
                if (header != null)
                {
                    string tail = JsonConvert.SerializeObject(header);
                    key += "<<headers>>" + tail;
                }
            }

            Hashtable response = new Hashtable();
            segs = Regex.Split(parts[1], "---header---.*[\r|\n]*");
            response["body"] = segs[0].Trim();
            if (segs.Length > 1 && segs[1].Trim().Length > 2)
            {
                string[] pieces = Regex.Split(segs[1], "---code---.*[\r|\n]*");
                JObject header = JObject.Parse(pieces[0]);
                response["headers"] = header;
                if (pieces.Length > 1)
                {
                    response["code"] = Convert.ToInt32(pieces[1]);
                }
            }
            responseData[key] = response;
        }

        public void LoadOneResponse(string fileName, string product, string version)
        {
            string response = (string)LoadResourceText(fileName.Substring(0, fileName.Length - 4));

            string text = response.Replace("$PRODUCT", product).Replace("$VERSION", version);
            string[] parts = Regex.Split(text, "---api---.*[\r|\n]*");
            for (int i = 0; i < parts.Length; i++)
            {
                if (parts[i].Trim().Length > 5)
                {
                    LoadResponse(parts[i]);
                }
            }
        }

        public void PrepareTestData(Hashtable testData, string resName)
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
                testData[name] = ht;

                for(int k=1; k<segs.Length; k++)
                {
                    Hashtable htTest = p.Parse(segs[k]);
                    ht.Add("_test_" + k, htTest);
                }
                ht["_test_count"] = segs.Length - 1;

                string dataFrom = (string)ht["DATAFROM"];
                if (!string.IsNullOrEmpty(dataFrom))
                {
                    Hashtable htFrom = (Hashtable)testData[dataFrom];
                    for (int k = 1; ; k++)
                    {
                        Hashtable oneData = (Hashtable)htFrom["_test_" + k];
                        if (oneData == null)
                        {
                            ht["_test_count"] = k - 1;
                            break;
                        }
                        ht["_test_" + k] = oneData;
                    }
                }
            }
        }

        public string GetLastConsoleText()
        {
            return lastConsoleText;
        }

        /// <summary>
        /// ISingletonBaseIo
        /// </summary>
        public void ConsoleWriteLine(string text)
        {
            lastConsoleText = text;
            Console.WriteLine(text);
        }

        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpGet(string url, Hashtable headers, int timeout, out string status, ILog logger = null)
        {
            Thread.Sleep(100);

            string text = this.GetResponse("[GET]" + url, headers);
            status = "";
            return text;
        }

        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpPost(string url, string text, Hashtable headers, int timeout, out string status, ILog logger = null)
        {
            status = "";
            return this.GetResponse("[POST]" + url, headers);
        }

        private string GetResponse(string key, Hashtable headers)
        {
            key = key.Replace("/locales/en-US/", "/locales/en/");
            Hashtable response = (Hashtable)responseData[key];
            string text = "";
            if (response != null)
            {
                text = (string)response["body"];
            }
            return text;
        }
    }
}
