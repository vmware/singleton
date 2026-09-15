/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.string;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.vmware.vip.common.i18n.dto.StringBasedDTO;

/**
 * Dto objects for String base data encapsulation
 */
@Entity
public class StringDTO extends StringBasedDTO implements Serializable {

    private static final long serialVersionUID = -5130487688889311356L;

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
