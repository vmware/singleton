/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.dao;

import java.util.List;

import com.vmware.l10n.record.model.RecordModel;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;

public interface SqlLiteDao {
	public int createSourceRecord(SingleComponentDTO dto);
	public int updateModifySourceRecord(SingleComponentDTO dto);
	public int updateSynchSourceRecord(RecordModel dto);
	public List<RecordModel> getChangedRecords();
	
}
