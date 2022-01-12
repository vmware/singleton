/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.service.version;

import java.io.Serializable;

/**
 * DTO for storing version information of Singletion service code
 */
public class ServiceVersionDTO implements Serializable {
    //Singletion service's name
    private String name;

    //Singletion service's author
    private String author;

    //Singletion service's build tool
    private String createdBy;

    //Singletion service's version
    private String version;

    //Singletion service's build date
    private String buildDate;

    //Singletion service code's changeId
    private String changeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }
}
