/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

public interface RemoteSyncService {
	public void ping(String remoteURL) throws L10nAPIException;

	public void send(ComponentSourceDTO componentSourceDTO, String remoteURL)
			throws L10nAPIException;
}
