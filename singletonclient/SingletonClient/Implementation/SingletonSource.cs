/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation
{
    public class SingletonSource : ISource
    {
        private readonly string _component;
        private readonly string _key;
        private readonly string _source;
        private readonly string _comment;

        public SingletonSource(
            string component, string key, string source, string comment = null)
        {
            _component = component;
            _key = key;
            _source = source;
            _comment = comment;
        }

        /// <summary>
        /// ISource
        /// </summary>
        public string GetComponent()
        {
            return _component;
        }

        /// <summary>
        /// ISource
        /// </summary>
        public string GetKey()
        {
            return _key;
        }

        /// <summary>
        /// ISource
        /// </summary>
        public string GetSource()
        {
            return _source;
        }

        /// <summary>
        /// ISource
        /// </summary>
        public string GetComment()
        {
            return _comment;
        }
    }
}

