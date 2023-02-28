/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * Data Object for translation which will be used for cache and data transform.
 */
@Entity
public class SourceDTO extends ComponentSourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    protected long id;
    private String source = "";

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
