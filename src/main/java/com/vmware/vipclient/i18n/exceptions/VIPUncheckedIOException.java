/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.exceptions;

public class VIPUncheckedIOException extends RuntimeException {
    private static final long serialVersionUID = 1210263498513384449L;

    public VIPUncheckedIOException() {
        // TODO Auto-generated constructor stub
    }

    public VIPUncheckedIOException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            original exception (normally a {@link java.io.IOException})
     * @stable ICU 53
     */
    public VIPUncheckedIOException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            exception message string
     * @param cause
     *            original exception (normally a {@link java.io.IOException})
     * @stable ICU 53
     */
    public VIPUncheckedIOException(String message, Throwable cause) {
        super(message, cause);
    }

}
