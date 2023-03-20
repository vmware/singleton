/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.conf;

import com.jcraft.jsch.UserInfo;
/**
 * 
 *
 * @author shihu
 *
 */
public class PassphraseUserInfo implements UserInfo {

    private String passphrase;

    public PassphraseUserInfo(String passphrase) {
        this.passphrase = passphrase;
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean promptPassword(String message) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        return false;
    }

    @Override
    public void showMessage(String message) {
    }
}
