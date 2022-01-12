/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.dto;

import java.util.List;

/**
 * Dto objects for update translation
 */
public class UpdateListDTO {

    private String name;

   

    private List<Object> subList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

  

    public List<Object> getSubList() {
        return subList;
    }

    public void setSubList(List<Object> subList) {
        this.subList = subList;
    }
}
