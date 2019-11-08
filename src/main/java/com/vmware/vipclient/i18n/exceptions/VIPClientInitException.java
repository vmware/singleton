package com.vmware.vipclient.i18n.exceptions;

public class VIPClientInitException extends Exception {

    private static final long serialVersionUID = 6254417482349002417L;

    public VIPClientInitException() {
    }

    public VIPClientInitException(String msg) {
        super(msg);
    }

    public VIPClientInitException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}