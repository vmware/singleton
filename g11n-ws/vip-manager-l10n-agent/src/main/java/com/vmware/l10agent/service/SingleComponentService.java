/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
/**
 * 
 *
 * @author shihu
 *
 */
public interface SingleComponentService {
	public boolean writerComponentFile(ComponentSourceModel sourceModel);
	
	public ComponentSourceModel getSourceComponentFile(RecordModel record);
	
	public boolean delSourceComponentFile(RecordModel record);
	
	public boolean synchComponentFile2Internal(RecordModel record);
	
	

}
