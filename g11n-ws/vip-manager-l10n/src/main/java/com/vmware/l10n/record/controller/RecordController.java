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
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;

@RestController
public class RecordController {
  private Logger logger = LoggerFactory.getLogger(RecordController.class);
	@Autowired
	private RecordService recordService;
	
	
	private final static String GATEWAYPREF="/i18n"; 
	private final static String LOGURLSTR="The request url is {}";
	
	@GetMapping(GATEWAYPREF+L10NAPIV1.API_L10N+"/records")
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
	
	
	
	
	@PostMapping(GATEWAYPREF+L10NAPIV1.API_L10N+"/synchrecord")
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
	
	
	
	@GetMapping(GATEWAYPREF+L10NAPIV1.API_L10N+"/sourcecomponent/{product}/{version}/{component}/{locale}/")
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
