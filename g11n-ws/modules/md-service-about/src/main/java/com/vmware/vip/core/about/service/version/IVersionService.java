/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.service.version;

import com.vmware.vip.core.about.exception.AboutAPIException;

/**
 * Class for defining methods about version information.
 */
public interface IVersionService {
    /**
     * Get the build's version information, including service's version info and product's translation's version info.
     *
     * @param productName
     * @param version
     * @return
     * @throws AboutAPIException
     */
    public BuildVersionDTO getBuildVersion(String productName, String version) throws AboutAPIException;
}
