/**
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.messages.data.conf.S3Cient;
import com.vmware.vip.messages.data.conf.S3Config;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.util.S3Utils;

@Repository
public class S3ProductDaoImpl implements IProductDao {

   private static Logger logger = LoggerFactory.getLogger(S3ProductDaoImpl.class);

   @Autowired
   private S3Cient s3Client;

   @Autowired
   private S3Config config;

   /**
    * get the compose list from S3 server
    */
   @Override
   public List<String> getComponentList(String productName, String version) throws DataException {
      List<String> componentList = new ArrayList<String>();
      String filePathPrefix = S3Utils.genProductVersionS3Path(productName, version);
      ListObjectsV2Result result =
            s3Client.getS3Client().listObjectsV2(config.getBucketName(), filePathPrefix);
      if (result == null) {
         throw new DataException("Can't find S3 resource from " + productName + "\\" + version);
      }
      List<S3ObjectSummary> objects = result.getObjectSummaries();
      if (objects == null || objects.size() < 1) {
         throw new DataException("S3 Component list is empty.");
      }
      for (S3ObjectSummary s3os : objects) {
         String resultKey =
               (s3os.getKey().replace(filePathPrefix, "")).split(S3Utils.S3FILE_SEPARATOR)[0];
         if (!componentList.contains(resultKey)) {
            componentList.add(resultKey);
         }
      }
      return componentList;
   }

   /**
    * get locale list from S3 server
    */
   @Override
   public List<String> getLocaleList(String productName, String version) throws DataException {
      List<String> localeList = new ArrayList<String>();
      String filePathPrefix = S3Utils.genProductVersionS3Path(productName, version);
      ListObjectsV2Result result =
            s3Client.getS3Client().listObjectsV2(config.getBucketName(), filePathPrefix);
      if (result == null) {
         throw new DataException("Can't find S3 resource from " + productName + "\\" + version);
      }
      List<S3ObjectSummary> objects = result.getObjectSummaries();
      if (objects == null || objects.size() < 1) {
         throw new DataException("S3 Component list is empty.");
      }
      for (S3ObjectSummary s3os : objects) {
         String resultKey =
               (s3os.getKey().replace(filePathPrefix, "")).split(S3Utils.S3FILE_SEPARATOR)[1];
         String localeKey = S3Utils.getLocaleByFileName(resultKey);
         if (!localeList.contains(localeKey)) {
            localeList.add(localeKey);
         }
      }
      return localeList;
   }

   /**
    * get bundle version from s3 server
    */
   @Override
   public String getVersionInfo(String productName, String version) throws DataException {
      String filePath =
            S3Utils.genProductVersionS3Path(productName, version) + ConstantsFile.VERSION_FILE;
      S3Object o = s3Client.getS3Client().getObject(config.getBucketName(), filePath);
      String result = null;
      if (o != null) {
         try {
            result = S3Utils.S3Obj2Str(o);
         } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            throw new DataException("File is not existing: " + filePath);
         }
      } else {
         throw new DataException("File is not existing: " + filePath);
      }
      if (result == null) {
         throw new DataException("File is not existing: " + filePath);
      }
      return result;
   }

}
