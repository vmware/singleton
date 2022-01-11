/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.service;

import java.util.List;

import com.vmware.l10agent.model.ComponentSourceModel;
import com.vmware.l10agent.model.RecordModel;
/**
 * 
 *
 * @author shihu
 *
 */
public interface RecordService  {
	public void getRecordModelsByRemoteV1();
	public List<RecordModel> getRecordModelsByRemoteS3(String product, String version, long lastModifyTime);
	public List<RecordModel> getRecordModelsByRemote();
	public boolean synchRecordModelsByRemote(RecordModel record);
	public ComponentSourceModel getComponentByRemote(RecordModel record);

}
