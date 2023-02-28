/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.service;

import java.util.List;

import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;

/**
 * 
 *
 * @author shihu
 *
 */


public interface SynchService {
	
public List<String> updateTranslationBatch(List<ComponentMessagesDTO> comps);

}
