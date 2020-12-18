package com.vmware.vip.core.about.exception;

import com.vmware.vip.common.exceptions.VIPAPIException;

public class AboutAPIException extends VIPAPIException {

    public AboutAPIException() {
        super();
    }

    public AboutAPIException(String msg) {
        super(msg);
    }

    public AboutAPIException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
