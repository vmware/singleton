/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.multcomponent;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vmware.vip.common.i18n.dto.MultiComponentsDTO;

/**
 * Data Object for translation which will be used for cache and data transform.
 */
@Entity
public class TranslationDTO extends MultiComponentsDTO implements Serializable {

    private static final long serialVersionUID = -8133779271648219583L;

    protected long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
