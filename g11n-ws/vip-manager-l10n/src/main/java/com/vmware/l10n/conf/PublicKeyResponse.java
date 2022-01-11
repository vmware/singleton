/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.util.Objects;

public class PublicKeyResponse {

    private String alg;
    private String value;
    private String issuer;

    public String getAlg() {
        return alg;
    }

    public void setAlg(final String alg) {
        this.alg = alg;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicKeyResponse)) {
            return false;
        }
        PublicKeyResponse that = (PublicKeyResponse) o;
        return Objects.equals(alg, that.alg) && Objects.equals(value, that.value) && Objects.equals(issuer, that.issuer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alg, value, issuer);
    }

    @Override
    public String toString() {
        return "PublicKeyResponse{" + "alg='" + alg + '\'' + ", value='" + value + '\'' + ", issuer='" + issuer + '\'' + '}';
    }
}
