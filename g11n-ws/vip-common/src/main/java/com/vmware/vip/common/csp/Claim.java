/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.csp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public class Claim {
    private String sub;
    private String iat;
    private String exp;
    private String domain;
    private String context;
    private String contextName;
    private String acct;
    private String[] perms;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @JsonProperty("context_name")
    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    public String[] getPerms() {
        return perms;
    }

    public void setPerms(String[] perms) {
        this.perms = perms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return Objects.equals(sub, claim.sub) &&
                Objects.equals(iat, claim.iat) &&
                Objects.equals(exp, claim.exp) &&
                Objects.equals(domain, claim.domain) &&
                Objects.equals(context, claim.context) &&
                Objects.equals(contextName, claim.contextName) &&
                Objects.equals(acct, claim.acct) &&
                Arrays.equals(perms, claim.perms);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sub, iat, exp, domain, context, contextName, acct, Arrays.hashCode(perms));
    }

    @Override
    public String toString() {
        return "Claim{" +
                "sub='" + sub + '\'' +
                ", iat='" + iat + '\'' +
                ", exp='" + exp + '\'' +
                ", domain='" + domain + '\'' +
                ", context='" + context + '\'' +
                ", context_name='" + contextName + '\'' +
                ", acct='" + acct + '\'' +
                ", perm='" + Arrays.toString(perms) + '\'' +
                '}';
    }
}
