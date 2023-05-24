/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.image;

import java.nio.channels.FileChannel;

public interface ICountryFlagDao {
    public FileChannel getCountryFlagChannel(String scale, String shortName) throws Exception;
}
