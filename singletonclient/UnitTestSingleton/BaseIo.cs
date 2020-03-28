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

            I18n.GetExtension().SetAccessService(this, "test");
        }

        public string GetTestResource(string name)
        {
            string str = null;
            object obj = resourceManager.GetObject(name);
            string type = obj.GetType().ToString();
            if (type.Equals("System.Byte[]"))
            {
                str = System.Text.Encoding.Default.GetString((System.Byte[])obj);
            } else
            {
                str = (string)obj;
            }
            
            return str.Trim();
        }

        private void GetTestData()
        {
            string text = GetTestResource("http_list");

            SingletonParserProperties p = new SingletonParserProperties();
            Hashtable ht = p.Parse(text);

            string text2 = GetTestResource("get_localelist");
            ht = p.Parse(text);

            text2 = GetHttpInfo("get_url_1");
        }

        public string GetHttpInfo(string key)
        {
            string value = (string)ht[key];
            if (string.IsNullOrEmpty(value))
            {
                return "";
            }
            if (value.StartsWith("(res)"))
            {
                value = GetTestResource(value.Substring(5));
            }
            return value;
        }

        public void ConsoleWriteLine(string text)
        {
            lastConsoleText = text;
            Console.WriteLine(text);
        }

        public string GetLastConsoleText()
        {
            GetTestData();
            return lastConsoleText;
        }

        public string HttpGet(string url, Hashtable headers)
        {
            for (int i=1; i < 100; i++)
            {
                string text = GetHttpInfo("get_url_" + i);
                if (url.Equals(text))
                {
                    string mockText = GetHttpInfo("get_response_" + i);
                    Console.WriteLine(mockText);
                    return mockText;
                }
            }
            return "";
        }

        public string HttpPost(string url, string text, Hashtable headers)
        {
            for (int i = 1; i < 100; i++)
            {
                string postUrl = GetHttpInfo("post_url_" + i);
                string postText = GetHttpInfo("post_text_" + i);
                if (url.Equals(postUrl) && text.Equals(postText))
                {
                    string mockText = GetHttpInfo("post_response_" + i); 
                    Console.WriteLine(mockText);
                    return mockText;
                }
            }
            return null;
        }
    }
}
