/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.i18n.api.base;

import java.nio.ByteBuffer;

public class StreamImageResp {

    private static byte[] succStateStr = "{\r\n  \"response\": {\r\n    \"code\": 200,\r\n    \"message\": \"OK\",\r\n    \"serverTime\": \"\"\r\n  },\r\n  \"signature\": \"\",\r\n  \"data\": ".getBytes();

    private static byte[] endStr = "\r\n}".getBytes();

    private StreamImageResp(){ }

    public static ByteBuffer getRespStartBytes() {
        return ByteBuffer.wrap(succStateStr);
    }

    public static ByteBuffer getEndBytes() {
        return ByteBuffer.wrap(endStr);
    }


}
