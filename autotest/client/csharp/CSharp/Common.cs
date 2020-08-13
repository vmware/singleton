/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System.Collections.Generic;
using System.Reflection;
using System;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System.Net;
using System.IO;
using System.Text;

namespace CSharp
{
 

    public class Common
    {
        private const string VipPathHead = "/i18n/api/v2/translation/products/{0}/versions/{1}/";
        private const string VipParameter = "pseudo=true&machineTranslation=false&checkTranslationStatus=false";
        private const string VipGetComponent = "locales/{0}/components/{1}?";
        private const string VipSendSource = "locales/en-US/components/{0}/keys/{1}?collectSource={2}&";



        public static string ParseListStringContent(List<string> name)
        {
            return string.Join(", ", name.ToArray());
        }



        public static string GetComponentApi(string component, string locale)
        {
            string head = string.Format(VipPathHead, "CSharpClient", "2.1");
            string path = string.Format(VipGetComponent, locale, component);
            string para = string.Format(VipParameter);
            string api = string.Format("{0}{1}{2}{3}", "http://localhost:8091", head, path, para);
            return api;
        }

        public static string GetComponentApi1(string component, string locale)
        {
            string head = string.Format(VipPathHead, "CSharpClient", "2.0.0");
            string path = string.Format(VipGetComponent, locale, component);
            string para = string.Format(VipParameter);
            string api = string.Format("{0}{1}{2}{3}", "http://localhost:8091", head, path, para);
            return api;
        }

        public static JObject ConvertToDict(string text)
        {
            JObject dict = JObject.Parse(text);
            return dict;
        }

        public static string HttpGet(string url)
        {
            string result = "";

            try
            {
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                req.Method = "GET";
                HttpWebResponse resp = (HttpWebResponse)req.GetResponse();
                Stream stream = resp.GetResponseStream();

                using (StreamReader reader = new StreamReader(stream, Encoding.UTF8))
                {
                    result = reader.ReadToEnd();
                }
            }
            catch (Exception e)
            {
                return null;
            }
            return result;
        }

        public static JObject HttpGetJson(string url)
        {
            string text = HttpGet(url);
            JObject dict = ConvertToDict(text);

            JObject obj = new JObject();
            obj.Add("result", dict);
            return obj;
        }

        public static string ParserJsonStringContent(JObject jo, string key)
        {
            string value = jo["result"]["data"]["messages"][key].ToString();
            return value;

        }

    }
}

