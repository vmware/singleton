/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CSPTokenConfig {
    @Value("${csp.auth.issuer:}")
    private String issuer;
    @Value("${csp.auth.url:}")
    private String jwksUri;
    @Value("${csp.auth.refresh-interval-sec:30}")
    private int refreshIntervalSec;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    public int getRefreshIntervalSec() {
        return refreshIntervalSec;
    }

    public void setRefreshIntervalSec(int refreshIntervalSec) {
        this.refreshIntervalSec = refreshIntervalSec;
    }


}
