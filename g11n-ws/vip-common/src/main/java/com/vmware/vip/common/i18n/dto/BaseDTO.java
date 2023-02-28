/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.io.Serializable;

/**
 * This class represents basic DTO.
 *
 */
public class BaseDTO  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8773007293081970618L;
	private String productName = "";
    private String version = "";

    // The origin of data, could be DB, bundle, cache, MT etc.
    private String dataOrigin = "";

    public String getDataOrigin() {
		return dataOrigin;
	}

	public void setDataOrigin(String dataOrigin) {
		this.dataOrigin = dataOrigin;
	}

	private boolean pseudo;
    private boolean machineTranslation;

    public boolean isMachineTranslation() {
		return machineTranslation;
	}

	public void setMachineTranslation(boolean machineTranslation) {
		this.machineTranslation = machineTranslation;
	}

	public BaseDTO() {
    }

    public BaseDTO(String productName, String version) {
        this.productName = productName;
        this.version = version;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVersion() {
        return version;
    }

    public boolean getPseudo() {
        return pseudo;
    }

    public void setPseudo(boolean pseudo) {
        this.pseudo = pseudo;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
