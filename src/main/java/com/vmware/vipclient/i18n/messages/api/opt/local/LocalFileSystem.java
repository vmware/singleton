/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

public class LocalFileSystem {
    private LocalFileSystem() {}

    private static class LocalFileSystemHolder {
        static final LocalFileSystem INSTANCE = new LocalFileSystem();
    }

    public static LocalFileSystem getInstance() {
        return LocalFileSystemHolder.INSTANCE;
    }
}
