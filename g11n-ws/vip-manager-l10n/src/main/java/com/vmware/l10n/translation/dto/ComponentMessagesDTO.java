/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.dto;

import java.io.Serializable;


import com.vmware.vip.common.i18n.dto.SingleComponentDTO;

import javax.persistence.Entity;

/**
 * Data Object for Component
 */
@Entity
public class ComponentMessagesDTO extends SingleComponentDTO implements Serializable {

	private static final long serialVersionUID = -7136314788163962399L;

}
