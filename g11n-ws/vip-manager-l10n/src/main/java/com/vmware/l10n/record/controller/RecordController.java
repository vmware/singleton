/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.controller;

import java.util.List;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.l10n.record.service.RecordService;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

@RestController
public class RecordController {
  private Logger logger = LoggerFactory.getLogger(RecordController.class);
	@Autowired
	private RecordService recordService;
	
	private final static String LOGURLSTR="The request url is {}";

	/**
	 * get the update source record from bundle or s3 storage
	 * @param productName
	 * @param version
	 * @param longDate
	 * @param request
	 * @return
	 * @throws L10nAPIException
	 */
	@GetMapping(L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV2)
	public APIResponseDTO getRecordV2Model(
			@RequestParam(value =APIParamName.PRODUCT_NAME, required=false)String productName, 
			@RequestParam(value =APIParamName.VERSION, required=false)String version, 
			@RequestParam(value =APIParamName.LONGDATE, required=false)String longDate, HttpServletRequest request) throws L10nAPIException{
		logger.info("start getting the changed s3 record");
		logger.info(LOGURLSTR, request.getRequestURL());
		long lastModifyTime = 0;
		try {
			lastModifyTime = Long.parseLong(longDate);
		} catch (Exception e) {
			logger.warn("parse lastModify error: {}", longDate);
		}
		logger.info("The parameters are: productName={}, version={},longDate={}", productName, version, longDate);
		List<RecordModel> recordList = recordService.getChangedRecords(productName, version, lastModifyTime);
		logger.info("s3records size {}", recordList.size());
		APIResponseDTO responseDto = null;
		if ((recordList != null) && (recordList.size() > 0)) {
			responseDto = new APIResponseDTO();
			responseDto.setData(recordList);
		} else {
			responseDto = new APIResponseDTO();
			responseDto.setResponse(APIResponseStatus.NO_CONTENT);
		}
		logger.info("end get the changed record V2 API");
		return responseDto;
	}

	/**
	 *  get the update source record from l10n
	 * @param product
	 * @param version
	 * @param component
	 * @param locale
	 * @param request
	 * @return
	 */
	@GetMapping(L10nI18nAPI.SOURCE_SYNC_RECORD_SOURCE_APIV1)
	public APIResponseDTO getSourceComponentModel(@PathVariable String product, @PathVariable String version, @PathVariable String component, @PathVariable String locale,
			HttpServletRequest request){
		APIResponseDTO responseDto = null;
		logger.info("begin get  component content");
		logger.info(LOGURLSTR, request.getRequestURL());
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}", product, version, component, locale);
		ComponentSourceModel model = recordService.getComponentSource(product, version, component, locale);
		 if(model != null) {
			 responseDto = new APIResponseDTO();
			 responseDto.setData(model);
			 
		 }else {
			 responseDto = new APIResponseDTO();
			 responseDto.setResponse(APIResponseStatus.NO_CONTENT);
		 }
		 
		 logger.info("end get component content");
		return responseDto;
		
	}
	
	
	
	
}
