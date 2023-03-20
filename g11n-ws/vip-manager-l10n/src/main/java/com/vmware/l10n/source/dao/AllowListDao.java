/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao;

import java.util.List;
import java.util.Map;

public interface AllowListDao {
	public Map<String, List<String>> getAllowList();
}
