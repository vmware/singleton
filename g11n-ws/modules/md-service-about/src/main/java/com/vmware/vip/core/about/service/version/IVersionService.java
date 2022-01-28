/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.service.version;

import com.vmware.vip.core.about.exception.AboutAPIException;

/**
 * Class for defining methods about version information.
 */
public interface IVersionService {
    /**
     * Get Singleton code's version
     *
     * @return
     */
    public ServiceVersionDTO getServiceVersion();

    /**
     * Get product's translation's version
     *
     * @param productName
     * @param version
     * @return
     * @throws AboutAPIException
     */
    public BundleVersionDTO getBundleVersion(String productName, String version) throws AboutAPIException;
}
