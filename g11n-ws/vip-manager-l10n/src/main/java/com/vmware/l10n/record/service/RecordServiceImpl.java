/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private SourceDao sourceDao;


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
	public List<RecordModel> getChangedRecords(String productName, String version, long lastModifyTime)
			throws L10nAPIException {
		// TODO Auto-generated method stub
		return sourceDao.getUpdateRecords(productName, version, lastModifyTime);
	}

}
