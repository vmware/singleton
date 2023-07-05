/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.service;

import java.util.List;

import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

public interface RecordService {
	public List<RecordModel> getChangedRecords(String productName, String version, long lastModifyTime) throws L10nAPIException;

	public ComponentSourceModel getComponentSource(String product, String version, String component, String locale);
}
