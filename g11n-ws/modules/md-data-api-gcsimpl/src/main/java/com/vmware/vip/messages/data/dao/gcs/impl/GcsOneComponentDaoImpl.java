/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.gcs.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;
import com.vmware.vip.messages.data.gcs.conf.GcsClient;
import com.vmware.vip.messages.data.gcs.conf.GcsConfig;
import com.vmware.vip.messages.data.gcs.util.GcsUtils;

/**
 * This java class is used to handle translation bundle file or translation
 */
@Repository
@Profile("gcs")
public class GcsOneComponentDaoImpl implements IOneComponentDao {

    @Autowired
    private GcsClient gcsClient;

    @Autowired
    private GcsConfig config;

    static final String GCS_NOT_EXIST_STR = "GC Storage File doesn't exist: ";
    private static Logger logger = LoggerFactory.getLogger(GcsOneComponentDaoImpl.class);

    /**
     * get one component bundle files from s3 server and convert to ResultI18Message
     * Object
     */
    @Override
    public ResultI18Message get(String productName, String version, String component, String locale)
            throws DataException {
        String jsonStr = get2JsonStr(productName, version, component, locale);
        ObjectMapper mapper = new ObjectMapper();
        ResultI18Message result = null;
        try {
            result = mapper.readValue(jsonStr, ResultI18Message.class);
        } catch (IOException e) {
            String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
            logger.error(errorLog, e);
            throw new DataException(GCS_NOT_EXIST_STR);
        }
        if (result != null) {
            result.setProduct(productName);
            result.setVersion(version);
            result.setComponent(component);
            result.setLocale(locale);
        } else {
            throw new DataException(GCS_NOT_EXIST_STR);
        }
        return result;
    }

    /**
     * get one component bundle files from s3 server as json String
     */
    @Override
    public String get2JsonStr(String productName, String version, String component, String locale)
            throws DataException {
        logger.debug("GcsOneComponentDaoImpl.get2JsonStr()");
        String filePath = GcsUtils.genProductVersionGcsPath(productName, version) + component + ConstantsChar.BACKSLASH
                + ResourceFilePathGetter.getLocalizedJSONFileName(locale);
        BlobId blobId = BlobId.of(config.getBucketName(), filePath);
        Blob blob = gcsClient.getGcsStorage().get(blobId);
        if (blob != null) {
            byte[] content = blob.getContent();
            if (content != null) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }

        throw new DataException(GCS_NOT_EXIST_STR + filePath);
    }

    @Override
    public boolean add(String productName, String version, String component, String locale,
            Map<String, String> messages) throws DataException {
        return false;
    }

    /**
     * update the component bundle file to remote S3 server
     */
    @Override
    public boolean update(String productName, String version, String componentParam, String locale,
            Map<String, String> messages) throws DataException {
        logger.debug("GcsOneComponentDaoImpl.update()-> productName={}, component={}, version={}, locale={}, messages={}",
                productName, componentParam, version, locale, messages);
        String component = componentParam;
        if (StringUtils.isEmpty(component)) {
        	component = ConstantsFile.DEFAULT_COMPONENT;
        }
        String filePath = GcsUtils.genProductVersionGcsPath(productName, version) + component + ConstantsChar.BACKSLASH
                + ResourceFilePathGetter.getLocalizedJSONFileName(locale);
        Map<String, Object> json = new HashMap<String, Object>();
        json.put(ConstantsKeys.COMPONENT, component);
        json.put(ConstantsKeys.lOCALE, locale);
        json.put(ConstantsKeys.MESSAGES, messages);
        String content;
        try {
            content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new DataException(ConstantsKeys.FATA_ERROR + "Failed to convert content to file: " + filePath + ".",
                    e);
        }
        BlobId blobId = BlobId.of(config.getBucketName(), filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Storage.BlobTargetOption precondition = Storage.BlobTargetOption
                .generationMatch(gcsClient.getGcsStorage().get(config.getBucketName(), filePath).getGeneration());
        gcsClient.getGcsStorage().create(blobInfo, content.getBytes(StandardCharsets.UTF_8), precondition);
        return true;
    }

    @Override
    public boolean delete(String productName, String version, String component, String locale) throws DataException {
        return false;
    }

}
