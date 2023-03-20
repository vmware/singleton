/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

/**
 * This class represents key DTO which used to transfer information related to the key.
 * 
 */
public class AuthenKeyDTO extends BaseDTO {

    private String key = "";

    private String userID = "";

    public AuthenKeyDTO() {
    }

    public AuthenKeyDTO(String productName, String version, String key, String userID) {
        super(productName, version);
        this.key = key;
        this.userID = userID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
