/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import org.junit.Test;

import com.vmware.l10n.expt.ExceptionHandle;
import com.vmware.l10n.expt.L10nAPIException;

public class TestExceptions {

    @Test
    public void TestComponentSource() {
        ExceptionHandle eh = new ExceptionHandle();
        eh.handler(new RuntimeException("this is a unit test"));
        eh.handler(new L10nAPIException("test L10nAPIException"));
    }
}
