/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.status;


/**
 * This class define some constant status for API response.
 * 
 */
public class APIResponseStatus {
    public static final Response OK = new Response(200, "OK");
    public static final Response CREATED = new Response(201, "Created");
    public static final Response NO_CONTENT = new Response(204, "No Content");
    public static final Response TRANSLATION_NOT_READY = new Response(205, "Collected resource's translations is not ready");
    public static final Response TRANSLATION_READY = new Response(206, "Collected resource's translations have been ready");
    public static final Response MULTTRANSLATION_PART_CONTENT = new Response(207, "Part of the translation is available");
    public static final Response VERSION_FALLBACK_TRANSLATION = new Response(604, "The content of response have been version fallback");
    public static final Response TRANSLATION_COLLECT_SUCCESS = new Response(200, "Source is collected successfully");
    public static final Response TRANSLATION_COLLECT_FAILURE = new Response(704, "Source is not collected successfully");
    public static final Response BAD_REQUEST = new Response(400, "Bad Request");
    public static final Response UNAUTHORIZED = new Response(401, "Unauthorized");
    public static final Response FORBIDDEN = new Response(403, "Forbidden");
    public static final Response CONFLICT = new Response(409, "Conflict");
    public static final Response REQEUST_ENITY_TOO_LARGE = new Response(413, "Request Entity Too Large");
    public static final Response TOO_MANY_REQUESTS = new Response(429, "Too Many Requests");
    public static final Response INVALID_TOKEN = new Response(498, "Invalid Token");
    public static final Response TOKEN_REQUIRED = new Response(499, "Token Required");
    public static final Response INTERNAL_SERVER_ERROR = new Response(500,
            "Internal Server Error");
    public static final Response INTERNAL_NO_RESOURCE_ERROR = new Response(404,
            "Not Found");
    public static final Response UNKNOWN_ERROR = new Response(520, "Unknown Error");
    public static final Response TRANSLATION_COLLECT_REQUEST_SUCCESS = new Response(200, "Source collection request have received");
}
