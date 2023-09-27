//Copyright 2019-2023 VMware, Inc.
//SPDX-License-Identifier: EPL-2.0
package com.vmware.l10n.source.dao.impl;

import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.l10n.conf.S3Cfg;
import com.vmware.l10n.conf.S3Client;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Profile("s3")
public class S3SourceDaoImpl implements SourceDao {
    private static Logger logger = LoggerFactory.getLogger(S3SourceDaoImpl.class);
    private static long deadLineTime = 1024*32;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private S3Cfg config;


    /**
     * the path of local resource file,can be configured in spring config file
     **/
    @Value("${source.bundle.file.basepath}")
    private String basePath;

    @Value("${source.sync.s3.compare.version.count:3}")
    private int compareVersionNum = 3;
    private ObjectWriter objectWriter = new ObjectMapper().writer(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).withDefaultPrettyPrinter();

    @PostConstruct
    private void init() {
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }else if(basePath.startsWith(ConstantsChar.DOT+ConstantsChar.BACKSLASH)) {
            basePath = basePath.replace(ConstantsChar.DOT, "").replace(ConstantsChar.BACKSLASH, "");
        }
        if (!basePath.isEmpty() && !basePath.endsWith(ConstantsChar.BACKSLASH)) {
            basePath += ConstantsChar.BACKSLASH;
        }
        basePath += ConstantsFile.L10N_BUNDLES_PATH;
        basePath = basePath.replace("\\", ConstantsChar.BACKSLASH);
    }


    @Override
    public String getFromBundle(SingleComponentDTO componentMessagesDTO) {

        logger.debug("Read content from file: {}/{}", componentMessagesDTO.getLocale(), componentMessagesDTO.getComponent());
        String bundlePath = getBundleFilePath(basePath, componentMessagesDTO);
        return s3Client.readObject(bundlePath);

    }

    @Override
    public boolean updateToBundle(ComponentMessagesDTO componentMessagesDTO) throws JsonProcessingException{

        String bundlePath = getBundleFilePath(basePath, componentMessagesDTO);
        ListVersionsRequest lvr = new ListVersionsRequest();
        lvr.setBucketName(config.getBucketName());
        lvr.setPrefix(bundlePath);
        lvr.setMaxResults(compareVersionNum);

        long sleepTime = 512L;
        boolean flag = true;
        do {
            S3Object reqS3Obj = null;
            try {
                reqS3Obj = s3Client.getS3Client().getObject(config.getBucketName(), bundlePath);
            }catch (Exception ex){
                logger.info("create new key:{}", bundlePath);
                reqS3Obj = null;
            }

            String sourceVersionId = null;
            String content = null;
            String updatedVersionId = null;
            if (reqS3Obj == null){
                content = getOrderBundleJson(componentMessagesDTO);
                logger.info(content);
                if (!s3Client.isObjectExist(bundlePath)){
                    PutObjectResult putResult = s3Client.getS3Client().putObject(config.getBucketName(), bundlePath, content);
                    updatedVersionId = putResult.getVersionId();
                    VersionListing versionListing = s3Client.getS3Client().listVersions(lvr);
                    String latestVersionId = versionListing.getVersionSummaries().get(0).getVersionId();
                    if (updatedVersionId.equals(latestVersionId)) {
                        return true;
                    }else {
                       s3Client.getS3Client().deleteVersion(config.getBucketName(), bundlePath, updatedVersionId);
                       logger.warn("index 0, summarySize: {}, delete key: {},  no source version: {} ", versionListing.getVersionSummaries().size(), bundlePath, updatedVersionId);
                       logger.warn(content);
                    }
                }

            }else {
                sourceVersionId = reqS3Obj.getObjectMetadata().getVersionId();
                String existingBundle = null;
                try {
                    existingBundle = convertS3Obj2Str(reqS3Obj);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
                SingleComponentDTO latestDTO = SourceUtils.mergeCacheWithBundle(componentMessagesDTO, existingBundle);
                content = getOrderBundleJson(latestDTO);
                logger.info(content);
                VersionListing sourceVersionListing = s3Client.getS3Client().listVersions(lvr);
                String preVersionId = sourceVersionListing.getVersionSummaries().get(0).getVersionId();
                if (sourceVersionId.equals(preVersionId)) {
                    PutObjectResult putResult = s3Client.getS3Client().putObject(config.getBucketName(), bundlePath, content);
                    updatedVersionId = putResult.getVersionId();
                    VersionListing updatedVersionListing = s3Client.getS3Client().listVersions(lvr);
                    List<S3VersionSummary> updateVersionSummary = updatedVersionListing.getVersionSummaries();
                    boolean isNotBreak = true;
                    for (int i = 0; i < updateVersionSummary.size(); i++) {
                        if (i == 0) {
                            if (!updatedVersionId.equals(updateVersionSummary.get(i).getVersionId())) {
                                s3Client.getS3Client().deleteVersion(config.getBucketName(), bundlePath, updatedVersionId);
                                isNotBreak = false;
                                logger.warn("index{}, delete key: {},  no source version: {} ",i, bundlePath, updatedVersionId);
                                logger.warn(content);
                                break;
                            }
                        } else {
                            if (!updateVersionSummary.get(i).getVersionId().equals(sourceVersionListing.getVersionSummaries().get(i-1).getVersionId())) {
                                s3Client.getS3Client().deleteVersion(config.getBucketName(), bundlePath, updatedVersionId);
                                logger.warn("delete key: {},  no source version: {} ", bundlePath, updatedVersionId);
                                logger.warn("index{}, updated {}, source{}", i, updateVersionSummary.get(i).getVersionId(),  sourceVersionListing.getVersionSummaries().get(i-1).getVersionId());
                                logger.warn(content);
                                isNotBreak = false;
                                break;
                            }
                        }
                    }
                    if (isNotBreak){
                        return true;
                    }
                }
            }

            if (sleepTime > deadLineTime){
                flag = false;
            }else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
                sleepTime = sleepTime << 1;
            }

        }while(flag);

        return false;

    }

    @Override
    public List<RecordModel> getUpdateRecords(String productName, String version, long lastModifyTime) throws L10nAPIException{

        List<RecordModel> records = new ArrayList<RecordModel>();
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(config.getBucketName());

        String latestJsonFile = ConstantsFile.LOCAL_FILE_SUFFIX+ConstantsChar.UNDERLINE+ ConstantsKeys.LATEST+ConstantsFile.FILE_TPYE_JSON;
        StringBuilder prefix = new StringBuilder();
        prefix.append(this.basePath);
        if (!StringUtils.isEmpty(productName)) {
            prefix.append(productName);
            prefix.append(ConstantsChar.BACKSLASH);
        }
        if (!StringUtils.isEmpty(version)) {
            prefix.append(version);
            prefix.append(ConstantsChar.BACKSLASH);
        }
        logger.info("begin getUpdateRecords lastModyTime: {}, prefix: {}", lastModifyTime, prefix.toString());
        req.setPrefix(prefix.toString());

        ListObjectsV2Result result;
        do {
            result = s3Client.getS3Client().listObjectsV2(req);
            for (S3ObjectSummary oSy : result.getObjectSummaries()) {
                String keyStr = oSy.getKey();
                long  currentModifyTime = oSy.getLastModified().getTime();
                if(keyStr.endsWith(latestJsonFile)
                        && currentModifyTime>lastModifyTime) {
                    logger.info("Need Update:{}:{}", keyStr, currentModifyTime);
                    records.add(SourceUtils.parseKeyStr2Record(keyStr,this.basePath, currentModifyTime));
                }

            }
            String token = result.getNextContinuationToken();
            req.setContinuationToken(token);
        } while (result.isTruncated());
        return records;
    }

    private static String convertS3Obj2Str(S3Object s3Obj) throws IOException {
        S3ObjectInputStream s3is = s3Obj.getObjectContent();
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        byte[] read_buf = new byte[1024];
        int read_len = 0;
        try {
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            return fos.toString(ConstantsUnicode.UTF8);
        } finally {
            s3is.close();
            fos.close();

        }
    }

    private String getBundleFilePath(String basePath, SingleComponentDTO dto) {
        if (StringUtils.isEmpty(dto.getComponent())) {
            dto.setComponent(ConstantsFile.DEFAULT_COMPONENT);
        }
        return genProductVersionS3Path(basePath, dto.getProductName(), dto.getVersion()) + dto.getComponent()
                + ConstantsChar.BACKSLASH + ResourceFilePathGetter.getLocalizedJSONFileName(dto.getLocale());
    }

    private String genProductVersionS3Path(String basePath, String productName, String version) {
        StringBuilder path = new StringBuilder();
        path.append(basePath);
        path.append(productName);
        path.append(ConstantsChar.BACKSLASH);
        path.append(version);
        path.append(ConstantsChar.BACKSLASH);
        return path.toString();
    }
    private String getOrderBundleJson(SingleComponentDTO componentDTO) throws JsonProcessingException {
        Map<String, Object> json = new HashMap<>();
        json.put(ConstantsKeys.COMPONENT, componentDTO.getComponent());
        json.put(ConstantsKeys.lOCALE, componentDTO.getLocale());
        json.put(ConstantsKeys.MESSAGES, componentDTO.getMessages());
        json.put(ConstantsKeys.ID, componentDTO.getId());
        return this.objectWriter.writeValueAsString(json);
    }
}
