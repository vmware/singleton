/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.common;

/**
 * This class defines constants needed in test class.
 */
public class Constants {
    public static final String VERSION = "version";
    public static final String SOURCE = "source";
    public static final String COMMENT_FOR_SOURCE = "commentForSource";
    public static final String EXPECTRESULT = "{\"status\":{\"code\":200,\"message\":\"OK\"}}";
    public static final String UPDATETRANSLATIONEXPECTRESULT = "{\"response\":{\"code\":200,\"message\":\"OK\"},\"signature\":\"\",\"data\":null}";
    public static final String UPDATETRANSLATIONAPIREQUESTBODY = "{\"data\": {\"productName\": \"vCG\",\"pseudo\": true,\"translation\": [{\"component\": \"cim\",\"locale\": \"zh_CN\",\"messages\": {\"name\": \"name\"}}],\"version\": \"1.0.0\"},\"requester\": \"GRM\"}";
}
