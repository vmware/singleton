/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	
	@GetMapping(L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV1)
	public APIResponseDTO getRecoredModel(HttpServletRequest request){
		logger.info("begin get the changed record");
		logger.info(LOGURLSTR, request.getRequestURL());
		APIResponseDTO responseDto = null;
		 List<RecordModel>  list =  recordService.getChangedRecords();
		 
		 if((list != null) && (list.size()>0)) {
			 responseDto = new APIResponseDTO();
			 responseDto.setData(list);
			 
		 }else {
			 responseDto = new APIResponseDTO();
			 responseDto.setResponse(APIResponseStatus.NO_CONTENT);
		 }
		 logger.info("end get the changed record");
		 
		return responseDto;
		
	}
	
	@GetMapping(L10nI18nAPI.SOURCE_SYNC_RECORDS_APIV2)
	public APIResponseDTO getRecoredV2Model(HttpServletRequest request) throws L10nAPIException{
		logger.info("begin get the changed V3 record");
		logger.info(LOGURLSTR, request.getRequestURL());
		String record = request.getParameter("record");
		APIResponseDTO responseDto = null;
		List<RecordModel>  recordList = null;
		if(record != null && record.equalsIgnoreCase("s3")) {
			String lastModify = request.getParameter("lastModify");
			long lastModifyTime =0;
			try {
				lastModifyTime = Long.parseLong(lastModify);
			}catch(Exception e) {
				logger.warn("parse lastModify error: {}", lastModify);
				lastModifyTime=0;
			}
		    recordList =  recordService.getChangedRecordsS3(lastModifyTime);
		    logger.info("s3records size {}",recordList.size());
		
		}else {
			recordList =  recordService.getChangedRecords();
		    for(RecordModel rm : recordList) {
			   recordService.updateSynchSourceRecord(rm.getProduct(), rm.getVersion(), rm.getComponent(), rm.getLocale(), rm.getStatus());
		    }
		}
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
	
	@PostMapping(L10nI18nAPI.SOURCE_SYNC_RECORD_STATUS_APIV1)
	public Response synchRecoredModel(@RequestParam String product, @RequestParam String version, @RequestParam String component, @RequestParam String locale, @RequestParam String status,
			HttpServletRequest request){
		logger.info("begin synch record");
		logger.info(LOGURLSTR, request.getRequestURL());
		Response response = null;
		int statusInt = Integer.parseInt(status.trim());
		int result = recordService.updateSynchSourceRecord(product, version, component, locale, statusInt);
		
		if(result >0) {
			response = APIResponseStatus.OK;
		}else {
			response = APIResponseStatus.NO_CONTENT;
		}
		logger.info("end  synch record");
		return response;
		
	}
	
	
	
	@GetMapping(L10nI18nAPI.SOURCE_SYNC_RECORD_SOURCE_APIV1)
	public APIResponseDTO getSourceComponentModel(@PathVariable String product, @PathVariable String version, @PathVariable String component, @PathVariable String locale,
			HttpServletRequest request){
		APIResponseDTO responseDto = null;
		logger.info("begin get  component content");
		logger.info(LOGURLSTR, request.getRequestURL());
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
