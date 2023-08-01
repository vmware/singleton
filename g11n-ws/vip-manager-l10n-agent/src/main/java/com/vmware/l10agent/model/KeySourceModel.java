/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.model;

import java.io.Serializable;

public class KeySourceModel implements Serializable {

    private static final long serialVersionUID = 5884041534893413221L;

    private String source = "";
    private String key = "";
    private String commentForSource = "";
    private String sourceFormat = "";


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommentForSource() {
        return commentForSource;
    }

    public void setCommentForSource(String commentForSource) {
        this.commentForSource = commentForSource;
    }

    public String getSourceFormat() {
        return sourceFormat;
    }

    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

}
