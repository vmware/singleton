/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import org.junit.Test;

import com.vmware.l10n.expt.ExceptionHandle;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

public class TestExceptions {

    @Test
    public void TestExceptionHandle() {
        ExceptionHandle eh = new ExceptionHandle();
        eh.handler(new RuntimeException("this is a unit test"));
        eh.handler(new L10nAPIException("test L10nAPIException"));
    }
    @Test
    public void TestException1() {
        new L10nAPIException();
        new L10nAPIException("test L10nAPIException",new RuntimeException("this is a unit test"));
    }
}
