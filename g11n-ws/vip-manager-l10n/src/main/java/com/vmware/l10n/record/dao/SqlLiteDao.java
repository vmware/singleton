/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.dao;

import java.util.List;

import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.record.model.SyncRecordModel;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

public interface SqlLiteDao {
	public int createSourceRecord(SingleComponentDTO dto);
	public int updateModifySourceRecord(SingleComponentDTO dto);
	public int updateSynchSourceRecord(RecordModel dto);
	public List<RecordModel> getChangedRecords();
	
	public int createSyncRecord(ComponentSourceDTO csd, int type, long timestamp);
	public List<SyncRecordModel> getSynRecords(int type);
	
	
}
