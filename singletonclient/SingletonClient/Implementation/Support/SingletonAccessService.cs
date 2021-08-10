/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

using System;
using System.Collections;
using System.IO;
using System.Net;
using System.Text;

namespace SingletonClient.Implementation.Support
{
    public class SingletonAccessService : IAccessService
    {
        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpGet(string url, Hashtable headers, int timeout, out string status, ILog logger = null)
        {
            string result = "";
            status = "";

            try
            {
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                if (timeout > 0)
                {
                    req.Timeout = timeout * 1000;
                }
                req.Method = "GET";

                if (headers != null)
                {
                    foreach (DictionaryEntry item in headers)
                    {
                        req.Headers.Add((string)item.Key, (string)item.Value);
                    }
                }

                HttpWebResponse resp = (HttpWebResponse)req.GetResponse();
                Stream stream = resp.GetResponseStream();

                using (StreamReader reader = new StreamReader(stream, Encoding.UTF8))
                {
                    result = reader.ReadToEnd();
                }

                if (headers != null)
                {
                    headers.Clear();

                    string[] keys = resp.Headers.AllKeys;
                    for (int i = 0; i < keys.Length; i++)
                    {
                        headers.Add(keys[i].ToLower(), resp.Headers.Get(keys[i]));
                    }
                }
            }
            catch (WebException e)
            {
                HttpWebResponse resp = (HttpWebResponse)e.Response;
                if (headers != null && resp != null)
                {
                    headers.Clear();
                    headers.Add(SingletonConst.HeaderResponseCode, resp.StatusCode.ToString());
                }
                status = "[STATUS]" + e.Status.ToString();
                if (logger != null)
                {
                    logger.Log(LogType.Info, status);
                }
                return null;
            }
            catch (Exception e)
            {
                status = "[MESSAGE]" + e.Message;
                if (logger != null)
                {
                    logger.Log(LogType.Info, status);
                }
                return null;
            }
            return result;
        }

        /// <summary>
        /// IAccessService
        /// </summary>
        public string HttpPost(string url, string text, Hashtable headers, int timeout, out string status, ILog logger = null)
        {
            status = "";
            return "";
        }
    }
}
