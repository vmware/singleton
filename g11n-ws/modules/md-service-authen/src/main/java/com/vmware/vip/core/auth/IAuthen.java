/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth;

public interface IAuthen {
	public void authen() throws AuthenException;
}
