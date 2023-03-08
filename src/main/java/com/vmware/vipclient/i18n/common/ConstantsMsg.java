/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.common;

public class ConstantsMsg {

    public static final String WRONG_PARAMETER      = "Wrong parameter(s)! ";
    public static final String LOCALE_CANNOT_NULL   = "Locale can't be null!";
    public static final String COMPONENT_CANNOT_EMPTY   = "Component can't be empty!";
    public static final String EXCEPTION_OCCUR      = "An exception occured! ";
    public static final String SERVER_RETURN_EMPTY  = "Server returned empty.";
    public static final String SERVER_RETURN_ERROR  = "Server returned error! Status: %d. Message: %s";
    public static final String SERVER_CONTENT_ERROR = "The content from server is wrong!";
    public static final String UNKNOWN_ERROR        = "Unknown error.";
    public static final String GET_LOCALES_FAILED   = "Failed to get list of locales from {0} data source";
    public static final String GET_COMPONENTS_FAILED   = "Failed to get list of components from {0} data source";
    public static final String GET_MESSAGES_FAILED  = "Failed to get messages for component {0}, locale: {1}, data source: {2}.";
    public static final String GET_MESSAGES_FAILED_ALL  = "Failed to get messages for component {0}, locale: {1} from any available data source";
    public static final String GET_FALLBACK_MESSAGES_FAILED = "Failed to get fallback messages for component {0}, locale: {1}";
    public static final String GET_MESSAGE_FAILED   = "Failed to get any message for key: {0} of component {1}, requested locale: {2}";

    //failed messages for l2
    public static final String GET_LANGUAGES_FAILED   = "Failed to get supported languages for locale {0} from {1} data source";
    public static final String GET_LANGUAGES_FAILED_ALL   = "Failed to get supported languages for locale {0} from any data source";
    public static final String GET_REGIONS_FAILED   = "Failed to get regions for locale {0} from {1} data source";
    public static final String GET_REGIONS_FAILED_ALL   = "Failed to get regions for locale {0} from any data source";
    public static final String GET_PATTERNS_FAILED   = "Failed to get patterns for locale {0} from {1} data source";
    public static final String GET_PATTERNS_FAILED_ALL   = "Failed to get patterns for locale {0} from any data source";
    public static final String GET_PATTERNS_FAILED_1   = "Failed to get patterns for language {0}, region {1} from {2} data source";
    public static final String GET_PATTERNS_FAILED_ALL_1   = "Failed to get patterns for language {0}, region {1} from any data source";
}
