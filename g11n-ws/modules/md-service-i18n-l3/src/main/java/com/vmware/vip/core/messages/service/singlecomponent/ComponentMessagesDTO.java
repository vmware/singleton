/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.singlecomponent;

import java.io.Serializable;

import javax.persistence.Entity;

import com.vmware.vip.common.i18n.dto.SingleComponentDTO;

/**
 * Data Object for Component
 */
@Entity
public class ComponentMessagesDTO extends SingleComponentDTO implements Serializable {

	private static final long serialVersionUID = -7136314788163962399L;

}
