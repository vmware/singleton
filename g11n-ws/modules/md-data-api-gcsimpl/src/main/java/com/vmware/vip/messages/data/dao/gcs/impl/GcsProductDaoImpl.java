/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.gcs.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage.BlobListOption;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.gcs.conf.GcsClient;
import com.vmware.vip.messages.data.gcs.conf.GcsConfig;
import com.vmware.vip.messages.data.gcs.util.GcsUtils;

/**
 * this class use to get the properties of a version bundle files
 */
@Repository
@Profile("gcs")
public class GcsProductDaoImpl implements IProductDao {

    private static Logger logger = LoggerFactory.getLogger(GcsProductDaoImpl.class);

    @Autowired
    private GcsClient gcsClient;

    @Autowired
    private GcsConfig config;

    /**
     * get the compose list from gcs service
     */
    @Override
    public List<String> getComponentList(String productName, String version) throws DataException {
        List<String> componentList = new ArrayList<String>();
        String filePathPrefix = GcsUtils.genProductVersionGcsPath(productName, version);
        Page<Blob> blobs = gcsClient.getGcsStorage().list(config.getBucketName(), BlobListOption.prefix(filePathPrefix),
                BlobListOption.currentDirectory());
        if (blobs == null) {
            throw new DataException("Can't find Gcs resource from " + productName + "\\" + version);
        }

        for (Blob blob : blobs.iterateAll()) {
            logger.debug("componentList->blob.getName()= {}", blob.getName());
            if (blob.isDirectory()) {
                String dir = blob.getName().replace(filePathPrefix, "");
                // remove the trailing slash in dir
                componentList.add(dir.substring(0, dir.length() - 1));
            }
        }

        if (componentList.size() == 0) {
            throw new DataException("Gcs Component list is empty.");
        }

        return componentList;
    }

    /**
     * get locale list from gcs service
     */
    @Override
    public List<String> getLocaleList(String productName, String version) throws DataException {
        List<String> localeList = new ArrayList<String>();
        String filePathPrefix = GcsUtils.genProductVersionGcsPath(productName, version);
        Page<Blob> blobs = gcsClient.getGcsStorage().list(config.getBucketName(),
                BlobListOption.prefix(filePathPrefix));
        if (blobs == null) {
            throw new DataException("Can't find Gcs resource from " + productName + "\\" + version);
        }

        for (Blob blob : blobs.iterateAll()) {
            logger.debug("localeList->blob.getName()= {}", blob.getName());
            String gcsobKey = blob.getName().replace(filePathPrefix, "");
            if (!gcsobKey.equals("") && (!gcsobKey.startsWith(ConstantsFile.CREATION_INFO))
                    && (!gcsobKey.startsWith(ConstantsFile.VERSION_FILE))) {
                String resultKey = gcsobKey.split(ConstantsChar.BACKSLASH)[1];
                String localeKey = GcsUtils.getLocaleByFileName(resultKey);
                if (localeKey != null && !localeList.contains(localeKey)) {
                    localeList.add(localeKey);
                }
            }
        }

        if (localeList.size() == 0) {
            throw new DataException("Gcs Locale list is empty.");
        }
        return localeList;
    }

    /**
     * get bundle version from gcs service
     */
    @Override
    public String getVersionInfo(String productName, String version) throws DataException {
        String filePath = GcsUtils.genProductVersionGcsPath(productName, version) + ConstantsFile.VERSION_FILE;
        String result = null;
        try {
            byte[] content = gcsClient.getGcsStorage().readAllBytes(config.getBucketName(), filePath);
            if (content != null) {
                result = new String(content, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new DataException("File is not existing: " + filePath);
        }
        if (result == null) {
            throw new DataException("File is not existing: " + filePath);
        }
        return result;
    }

    /**
     * get one product's all available versions
     */
    @Override
    public List<String> getVersionList(String productName) throws DataException {
        String basePath = GcsUtils.GCS_L10N_BUNDLES_PATH + productName + ConstantsChar.BACKSLASH;
        Page<Blob> blobs = gcsClient.getGcsStorage().list(config.getBucketName(), BlobListOption.prefix(basePath),
                BlobListOption.currentDirectory());

        if (blobs == null) {
            throw new DataException(productName + " no available version in gcs");
        }

        List<String> versionList = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            logger.debug("versionList->blob.getName()= {}", blob.getName());
            if (blob.isDirectory()) {
                String dir = blob.getName().replace(basePath, "");
                // remove the trailing slash in dir
                versionList.add(dir.substring(0, dir.length() - 1));
            }
        }

        if (versionList.size() == 0) {
            throw new DataException(productName + " no available version in gcs");
        }

        return versionList;
    }

    /**
     * Get the content of the Allow List by gcs service
     */
    @Override
    public String getAllowProductListContent(String gcsPath) throws DataException {
        BlobId blobId = BlobId.of(config.getAllowListBucketName(), gcsPath);
        Blob blob = gcsClient.getGcsStorage().get(blobId);
        if (blob != null) {
            byte[] content = blob.getContent();
            if (content != null) {
                return new String(content, StandardCharsets.UTF_8);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
