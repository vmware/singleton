/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService{
	private static Logger logger = LoggerFactory.getLogger(RecordServiceImpl.class);

	private ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	@Autowired
	private SqlLiteDao sqlLite;
	
	@Autowired
	private SourceDao sourceDao;
    
	/**
     * get the updated source record 
     */
	@Override
	public List<RecordModel> getChangedRecords() {
		// TODO Auto-generated method stub
		return sqlLite.getChangedRecords();
	}
	
	/**
	 * sync the update source record status after get the change update source record
	 */
	@Override
	public int updateSynchSourceRecord(String product, String version, String component, String locale, long status) {
		// TODO Auto-generated method stub
		
		RecordModel record = new RecordModel();
		record.setProduct(product);
		record.setVersion(version);
		record.setComponent(component);
		record.setLocale(locale);
		record.setStatus(status);
		
		return sqlLite.updateSynchSourceRecord(record);
	}

	/**
	 * get the update source content that cached in local file
	 */
	@Override
	public ComponentSourceModel getComponentSource(String product, String version, String component, String locale) {

		SingleComponentDTO singleComponentDTO = new SingleComponentDTO();
		singleComponentDTO.setProductName(product);
		singleComponentDTO.setVersion(version);
		singleComponentDTO.setComponent(component);
		singleComponentDTO.setLocale(locale);


		String componentJSON = sourceDao.getFromBundle(singleComponentDTO);
		if (!StringUtils.isEmpty(componentJSON)) {

			  ComponentSourceModel source = null;
			  try {
				 source= this.mapper.readValue(componentJSON, ComponentSourceModel.class);
				 source.setProduct(product);
				 source.setVersion(version);
				 source.setComponent(component);
				 source.setLocale(locale);
				 
			} catch (JsonParseException e) {

				logger.error(e.getMessage(), e);
			} catch (JsonMappingException e) {

				logger.error(e.getMessage(), e);
			} catch (IOException e) {

				logger.error(e.getMessage(), e);
			}
			return source;
		}

		return null;
	}

	/**
	 * get the updated source record when source store in S3
	 */
	@Override
	public List<RecordModel> getChangedRecordsS3(String productName, String version, long lastModifyTime)
			throws L10nAPIException {
		// TODO Auto-generated method stub
		return sourceDao.getUpdateRecords(productName, version, lastModifyTime);
	}

}
