/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

/**
 * Enumerations of querying translation status.
 * 
 */
public enum TranslationQueryStatusType {
    Found("Found"), NotFound("Not Found"), ComponentFound("Component Found"), ComponentNotFound(
            "Component Not Found"), FileFound("File Found"), FileNotFound("File Not Found"), FoundAtDB(
            "Translation Found at DB"), NotFoundAtDB("Translation Not Found at DB"), FoundAtFile(
            "Translation Found in Resource File"), NotFoundAtFile(
            "Translation Not Found in Resource File");

    private String message;

    private TranslationQueryStatusType(String msg) {
        this.message = msg;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
