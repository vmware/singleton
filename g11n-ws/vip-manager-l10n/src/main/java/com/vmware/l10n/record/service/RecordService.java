/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.service;

import java.util.List;

import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.record.model.RecordModel;

public interface RecordService {
	public List<RecordModel> getChangedRecords();
	public int updateSynchSourceRecord( String product, String version, String component, String locale, int status);
	public ComponentSourceModel getComponentSource(String product, String version, String component, String locale);

}
