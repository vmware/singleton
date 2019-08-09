/**
 * 
 *
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 *
 * @author shihu
 *
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.conf.S3Cient;
import com.vmware.vip.messages.data.conf.S3Config;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;
import com.vmware.vip.messages.data.util.S3Utils;

@Repository
public class S3OneComponentDaoImpl implements IOneComponentDao {

   @Autowired
   private S3Cient s3Client;

   @Autowired
   private S3Config config;

   private static Logger logger = LoggerFactory.getLogger(S3OneComponentDaoImpl.class);

   @Override
   public ResultI18Message get(String productName, String version, String component, String locale)
         throws DataException {
      // TODO Auto-generated method stub
      String jsonStr = get2JsonStr(productName, version, component, locale);
      ObjectMapper mapper = new ObjectMapper();
      ResultI18Message result = null;
      try {
         result = mapper.readValue(jsonStr, ResultI18Message.class);
      } catch (JsonParseException e) {
         String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
         logger.error(errorLog, e);
         throw new DataException(e.getMessage());

      } catch (JsonMappingException e) {
         String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
         logger.error(errorLog, e);
         throw new DataException("File is not existing: ");
      } catch (IOException e) {
         String errorLog = ConstantsKeys.FATA_ERROR + e.getMessage();
         logger.error(errorLog, e);
         throw new DataException("File is not existing: ");

      }
      if (result != null) {
         result.setProduct(productName);
         result.setVersion(version);
         result.setComponent(component);
         result.setLocale(locale);
      } else {
         throw new DataException("File is not existing: ");
      }
      return result;

   }

   @Override
   public String get2JsonStr(String productName, String version, String component, String locale)
         throws DataException {
      // TODO Auto-generated method stub

      String filePath = S3Utils.genProductVersionS3Path(productName, version) + component
            + S3Utils.S3FILE_SEPARATOR + ResourceFilePathGetter.getLocalizedJSONFileName(locale);

      String result = null;
      if (s3Client.getS3Client().doesObjectExist(config.getBucketName(), filePath)) {
         S3Object o = s3Client.getS3Client().getObject(config.getBucketName(), filePath);
         if (o != null) {
            try {
               result = S3Utils.S3Obj2Str(o);
            } catch (IOException e) {
               // TODO Auto-generated catch block
               logger.warn(e.getMessage(), e);
               throw new DataException("S3File is not existing: " + filePath);
            }
         } else {
            throw new DataException("S3 File is not existing: " + filePath);
         }

      }

      if (result == null) {
         throw new DataException("S3 File is not existing: " + filePath);
      }

      return result;

   }

   @Override
   public boolean add(String productName, String version, String component, String locale,
         Map<String, String> messages) throws DataException {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean update(String productName, String version, String component, String locale,
         Map<String, String> messages) throws DataException {
      // TODO Auto-generated method stub
      if (StringUtils.isEmpty(component)) {
         component = ConstantsFile.DEFAULT_COMPONENT;
      }

      String filePath = S3Utils.genProductVersionS3Path(productName, version) + component
            + S3Utils.S3FILE_SEPARATOR + ResourceFilePathGetter.getLocalizedJSONFileName(locale);
      Map<String, Object> json = new HashMap<String, Object>();

      json.put(ConstantsKeys.COMPONENT, component);
      json.put(ConstantsKeys.lOCALE, locale);
      json.put(ConstantsKeys.MESSAGES, messages);

      String content;
      try {
         content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
      } catch (JsonProcessingException e) {
         // TODO Auto-generated catch block
         throw new DataException(
               ConstantsKeys.FATA_ERROR + "Failed to write content to file: " + filePath + ".", e);
      }

      PutObjectResult putResult =
            s3Client.getS3Client().putObject(config.getBucketName(), filePath, content);
      if (putResult != null) {
         return true;
      }
      return false;

   }

   @Override
   public boolean delete(String productName, String version, String component, String locale)
         throws DataException {
      // TODO Auto-generated method stub
      return false;
   }

}
