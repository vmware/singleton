/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dto;

import com.vmware.vip.common.i18n.status.Response;

/**
 * This class defines several response status of GRM API.
 */
public interface GRMAPIResponseStatus {
	
    public static final Response CREATED = new Response(201, "Created");
    public static final Response INVALID_REQUEST = new Response(400, "Invalid Request");
    public static final Response UNAUTHORIZED = new Response(401, "Unauthorized");
    public static final Response INTERNAL_SERVER_ERROR = new Response(500, "Internal Server Error");
}
