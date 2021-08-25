/*
 * Copyright 2020-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

namespace SingletonClient.Implementation
{
    public static class SingletonConst
    {
        public const string KeyResult = "result";
        public const string KeyResponse = "response";
        public const string KeyCode = "code";
        public const string KeyError = "error";
        public const string KeyData = "data";
        public const string KeyPath = "path";
        public const string KeyMessages = "messages";
        public const string KeyLocales = "locales";
        public const string KeyComponent = "component";
        public const string KeyLocale = "locale";

        public const string HeaderEtag = "etag";
        public const string HeaderCacheControl = "cache-control";
        public const string HeaderRequestEtag = "If-None-Match";
        public const string HeaderResponseCode = "response-code";

        public const string StatusNotModified = "NotModified";

        public const string PlaceNoLocaleDefine = "$NO_LOCALE_DEFINE";
    }
}
