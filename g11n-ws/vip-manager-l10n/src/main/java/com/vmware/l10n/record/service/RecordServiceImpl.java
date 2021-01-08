/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.source.crons.SourceSendingCron;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl implements RecordService{
	private static Logger logger = LoggerFactory.getLogger(RecordServiceImpl.class);

	@Autowired
	private SqlLiteDao sqlLite;
	
	@Autowired
	private SourceSendingCron sourceSendingCron;
	@Autowired
	private SourceDao sourceDao;

	@Override
	public List<RecordModel> getChangedRecords() {
		// TODO Auto-generated method stub
		return sqlLite.getChangedRecords();
	}

	@Override
	public int updateSynchSourceRecord(String product, String version, String component, String locale, int status) {
		// TODO Auto-generated method stub
		
		RecordModel record = new RecordModel();
		record.setProduct(product);
		record.setVersion(version);
		record.setComponent(component);
		record.setLocale(locale);
		record.setStatus(status);
		
		return sqlLite.updateSynchSourceRecord(record);
	}

	@Override
	public ComponentSourceModel getComponentSource(String product, String version, String component, String locale) {
		// TODO Auto-generated method stub
		SingleComponentDTO singleComponentDTO = new SingleComponentDTO();
		singleComponentDTO.setProductName(product);
		singleComponentDTO.setVersion(version);
		singleComponentDTO.setComponent(component);
		singleComponentDTO.setLocale(locale);


		String componentJSON = sourceDao.getFromBundle(singleComponentDTO);
		
		if (!StringUtils.isEmpty(componentJSON)) {
			  ObjectMapper mapper = new ObjectMapper();
			  ComponentSourceModel source = null;
			  try {
				 source= mapper.readValue(componentJSON, ComponentSourceModel.class);
				 source.setProduct(product);
				 source.setVersion(version);
				 source.setComponent(component);
				 source.setLocale(locale);
				 
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}
			return source;
		}

		return null;
	}

}
