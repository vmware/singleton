/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.common;

public class ConstantsMsg {

    public static final String WRONG_PARAMETER      = "Wrong parameter(s)! ";
    public static final String EXCEPTION_OCCUR      = "An exception occured! ";
    public static final String SERVER_RETURN_EMPTY  = "Server returned empty.";
    public static final String SERVER_RETURN_ERROR  = "Server returned error! Status: %d. Message: %s";
    public static final String SERVER_CONTENT_ERROR = "The content from server is wrong!";
    public static final String UNKNOWN_ERROR        = "Unknown error.";
    public static final String GET_MESSAGES_FAILED  = "Failed to get messages for component {0}, locale: {1}, data source: {2}.";
    public static final String GET_MESSAGES_FAILED_ALL  = "Failed to get messages for component {0}, locale: {1} from any available data source";
    public static final String GET_MESSAGE_FAILED   = "Failed to get any message for key: {0} of component {1}, requested locale: {2}";
    public static final String GET_LANGUAGES_FAILED   = "Failed to get supported languages from {0} data source";
    public static final String GET_LANGUAGES_FAILED_ALL   = "Failed to get supported languages from any data source";
    public static final String GET_COMPONENTS_FAILED   = "Failed to get list of components from {0} data source";
    public static final String GET_LOCALES_FAILED   = "Failed to get list of locales from {0} data source";
}
