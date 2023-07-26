/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.image;

import com.vmware.vip.core.messages.exception.L2APIException;

import java.nio.channels.FileChannel;

public interface ICountryFlagService {
    public FileChannel getCountryFlagChannel(String region,int scala)throws L2APIException;
}
