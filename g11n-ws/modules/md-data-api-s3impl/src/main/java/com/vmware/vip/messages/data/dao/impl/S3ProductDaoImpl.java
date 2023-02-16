/**
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.messages.data.conf.S3Client;
import com.vmware.vip.messages.data.conf.S3Config;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.util.S3Utils;

/**
 * this class use to get the properties of a version bundle files
 */
@Repository
@Profile("s3")
public class S3ProductDaoImpl implements IProductDao {

   private static Logger logger = LoggerFactory.getLogger(S3ProductDaoImpl.class);

   @Autowired
   private S3Client s3Client;

   @Autowired
   private S3Config config;

   /**
    * get the compose list from S3 server
    */
   @Override
   public List<String> getComponentList(String productName, String version) throws DataException {
      List<String> componentList = new ArrayList<String>();
      String filePathPrefix = S3Utils.genProductVersionS3Path(productName, version);
      ObjectListing result =
            s3Client.getS3Client().listObjects(config.getBucketName(), filePathPrefix);
      if (result == null) {
         throw new DataException("Can't find S3 resource from " + productName + "\\" + version);
      }
      List<S3ObjectSummary> objects = result.getObjectSummaries();
      if (objects == null || objects.size() < 1) {
         throw new DataException("S3 Component list is empty.");
      }
      while(result.isTruncated()) {
      	   result =s3Client.getS3Client().listNextBatchOfObjects(result);
      	   objects.addAll(result.getObjectSummaries());
      }
      
      for (S3ObjectSummary s3os : objects) {
         String resultKey =
               (s3os.getKey().replace(filePathPrefix, "")).split(ConstantsChar.BACKSLASH)[0];
         if (!componentList.contains(resultKey) && (!resultKey.endsWith(ConstantsFile.FILE_TPYE_JSON))) {
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
      ObjectListing result =
            s3Client.getS3Client().listObjects(config.getBucketName(), filePathPrefix);
      if (result == null) {
         throw new DataException("Can't find S3 resource from " + productName + "\\" + version);
      }
      
      List<S3ObjectSummary> objects = result.getObjectSummaries();
      if (objects == null || objects.size() < 1) {
         throw new DataException("S3 Component list is empty.");
      }
      while(result.isTruncated()) {
   	   result =s3Client.getS3Client().listNextBatchOfObjects(result);
   	   objects.addAll(result.getObjectSummaries());
      }
      
      for (S3ObjectSummary s3os : objects) {
         String s3obKey = s3os.getKey().replace(filePathPrefix, "");
         if(!s3obKey.equals("") && (!s3obKey.startsWith(ConstantsFile.CREATION_INFO)) && (!s3obKey.startsWith(ConstantsFile.VERSION_FILE))) {
            String resultKey =s3obKey.split(ConstantsChar.BACKSLASH)[1];
            String localeKey = S3Utils.getLocaleByFileName(resultKey);
            if (localeKey != null && !localeList.contains(localeKey)) {
               localeList.add(localeKey);
            }
            
         }
         
         
      }
      return localeList;
   }

   /**
    * get bundle version from s3 server
    */
	@Override
	public String getVersionInfo(String productName, String version) throws DataException {
		String filePath = S3Utils.genProductVersionS3Path(productName, version) + ConstantsFile.VERSION_FILE;
		String result = null;
		try {
			S3Object o = s3Client.getS3Client().getObject(config.getBucketName(), filePath);
			if (o != null) {
				result = S3Utils.convertS3Obj2Str(o);
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
    String basePath = S3Utils.S3_L10N_BUNDLES_PATH+productName +  ConstantsChar.BACKSLASH;
    ObjectListing versionListResult = s3Client.getS3Client().listObjects(config.getBucketName(),basePath);
    
    if(versionListResult != null) {
        List<S3ObjectSummary> versionListSummary = versionListResult.getObjectSummaries();
        while(versionListResult.isTruncated()) {
        	versionListResult =s3Client.getS3Client().listNextBatchOfObjects(versionListResult);
        	versionListSummary.addAll(versionListResult.getObjectSummaries());
        }
        
        Set<String> versionset = new HashSet<>();
        for (S3ObjectSummary s3productName : versionListSummary) {
            versionset.add(s3productName.getKey().replace(basePath, "").split(ConstantsChar.BACKSLASH)[0]);
          }
        List<String> result = new ArrayList<>();
        for(String version: versionset) {
            result.add(version);
        }
      return result;
      
    }else {
     throw new DataException(productName + " no available version in s3");
    }
}

/**
 * Get the content of the Allow List by s3 object
 */
@Override
public String getAllowProductListContent() throws DataException {
  String s3Path = S3Utils.S3_L10N_BUNDLES_PATH+ConstantsFile.ALLOW_LIST_FILE;
  if (s3Client.getS3Client().doesObjectExist(config.getBucketName(), s3Path)) {
      S3Object o = s3Client.getS3Client().getObject(config.getBucketName(), s3Path);
      if (o != null) {
         try {
             return S3Utils.convertS3Obj2Str(o);
         } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return null;
         }
      } else {
         return null;
      }
   }else {
       return null;
   }
}

}
