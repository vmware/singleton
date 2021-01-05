/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.service.version;

import com.vmware.vip.common.constants.ConstantsChar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.core.about.exception.AboutAPIException;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.product.ProductService;

@Service
public class VersionService implements IVersionService{
    @Value("${build.name}")
    private String name;

    @Value("${build.author}")
    private String author;

    @Value("${build.version}")
    private String version;

    @Value("${build.createdBy}")
    private String createdBy;

    @Value("${build.number.builddate}")
    private String buildDate;

    @Value("${build.number.buildnumber}")
    private String buildNumber;

    @Autowired
    ProductService productService;

    @Override
    public BuildVersionDTO getBuildVersion(String productName, String version) throws AboutAPIException {
        BuildVersionDTO buildVersionDTO = new BuildVersionDTO();
        buildVersionDTO.setService(getServiceVersion());
        if(!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(version)){
            buildVersionDTO.setBundle(getBundleVersion(productName, version));
        }
        return buildVersionDTO;
    }

    /**
     * Get Singleton service's version
     *
     * @return
     */
    private ServiceVersionDTO getServiceVersion(){
        ServiceVersionDTO serviceVersionDTO = new ServiceVersionDTO();
        serviceVersionDTO.setName(name);
        serviceVersionDTO.setAuthor(author);
        serviceVersionDTO.setVersion(version);
        serviceVersionDTO.setCreatedBy(createdBy);
        serviceVersionDTO.setBuildDate(buildDate);
        serviceVersionDTO.setChangeId(buildNumber);
        return serviceVersionDTO;
    }

    /**
     * Get product's translation's version
     *
     * @param productName
     * @param version
     * @return
     * @throws AboutAPIException
     */
    private BundleVersionDTO getBundleVersion(String productName, String version) throws AboutAPIException {
        BundleVersionDTO bundleVersionDTO = new BundleVersionDTO();
        bundleVersionDTO.setProductName(productName);
        bundleVersionDTO.setVersion(version);
        DropVersionDTO dropVersionDTO = null;
        try {
            dropVersionDTO = productService.getVersionInfo(productName, version);
        } catch (L3APIException e) {
            throw new AboutAPIException("[FATAL ERROR]Failed to get version info for "+ productName + ConstantsChar.BACKSLASH + version, e);
        }
        bundleVersionDTO.setChangeId(dropVersionDTO.getDropId());
        return bundleVersionDTO;
    }
}
