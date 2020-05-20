/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.l10n.translation.service.TranslationSyncServerService;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.CreationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;

/**
 *
 * Provide RESTful API to synchronize the latest translation.
 *
 */
@RestController
public class TranslationSyncServerController {
    private static Logger LOGGER = LoggerFactory
            .getLogger(TranslationSyncServerController.class);
    @Autowired
    TranslationSyncServerService translationSyncServerService;

    /** the path of local resource file,can be configed in spring config file **/
    @Value("${translation.bundle.file.basepath}")
    private String basePath;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public void processMethod(Exception e, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        LOGGER.error("abnormal server:{}", e.getLocalizedMessage());
        response.setCharacterEncoding(ConstantsUnicode.UTF8);
        response.setContentType("application/json; charset=utf-8");
        APIResponseDTO apiResponseDTO = new APIResponseDTO();
        apiResponseDTO.setResponse(APIResponseStatus.INTERNAL_SERVER_ERROR);
        ObjectMapper mapper = new ObjectMapper();
        String responseJson = mapper.writeValueAsString(apiResponseDTO);
        response.getWriter().printf(responseJson);
        response.flushBuffer();
    }

    private void saveCreationInfo(UpdateTranslationDTO updateTranslationDTO) {
        UpdateTranslationDataDTO transData = updateTranslationDTO.getData();

        String opId = "";
        CreationDTO creationDTO = transData.getCreation();
        if (creationDTO != null) {
            opId = creationDTO.getOperationid();
        }

        String filepath = basePath
                + ConstantsFile.L10N_BUNDLES_PATH
                + ResourceFilePathGetter.getProductVersionConcatName(transData)
                + ConstantsChar.BACKSLASH
                + ConstantsFile.CREATION_INFO;
       

        // read
        Map<String, Object> json = JSONUtils.getMapFromJsonFile(filepath);

        // set
        List<TranslationDTO> groups = transData.getTranslation();
        for (int i=0; i<groups.size(); i++) {
            json.put(groups.get(i).getLocale(), opId);
        }
        File pareFile = new File(filepath);
        if(!pareFile.getParentFile().exists()){
           pareFile.getParentFile().mkdirs(); 
        }
        // write
        JSONUtils.writeMapToJsonFile(filepath, json);
    }

    /**
     * Synchronize the latest translation from GRM or other third party.
     * <p>
     * Apply to On-Premise and SaaS.
     *
     * @param updateTranslationDTO
     *            This Java Bean represents request content from GRM or other
     *            third party. Base on product.
     * @param productName
     *            The name of product.
     * @param version
     *            The release version of product.
     * @param request
     *            Extends the ServletRequest interface to provide request
     *            information for HTTP servlets.
     * @return APIResponseDTO The object which represents response status.
     */
    @CrossOrigin
    @RequestMapping(value = L10NAPIV1.UPDATE_TRANSLATION_L10N, method = RequestMethod.POST, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO updateTranslation(
            @RequestBody UpdateTranslationDTO updateTranslationDTO,
            @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @PathVariable(APIParamName.VERSION) String version, HttpServletRequest request) {
        LOGGER.info("The request url is "
                + request.getRequestURL()
                + (request.getQueryString() == null ? "" : "?"
                        + request.getQueryString()));
    
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = "";
        try {
            requestJson = mapper.writeValueAsString(updateTranslationDTO);
        } catch (JsonProcessingException e) {
        	LOGGER.error(e.getMessage(), e);
          
        }
        LOGGER.info("The request content is {}", requestJson);
        APIResponseDTO response = new APIResponseDTO();
        if (StringUtils.isEmpty(updateTranslationDTO)
                || StringUtils.isEmpty(updateTranslationDTO.getData())
                || StringUtils.isEmpty(updateTranslationDTO.getData()
                        .getTranslation())) {
            response.setResponse(APIResponseStatus.BAD_REQUEST);
            return response;
        }
        UpdateTranslationDataDTO updateTranslationDataDTO = updateTranslationDTO
                .getData();
        List<TranslationDTO> translationList = updateTranslationDataDTO
                .getTranslation();
        if (StringUtils.isEmpty(updateTranslationDataDTO.getProductName())
                || !updateTranslationDataDTO.getProductName().equals(
                        productName)) {
            response.setResponse(APIResponseStatus.BAD_REQUEST);
            return response;
        }
        if (StringUtils.isEmpty(updateTranslationDataDTO.getVersion())
                || !updateTranslationDataDTO.getVersion().equals(version)) {
            response.setResponse(APIResponseStatus.BAD_REQUEST);
            return response;
        }
        List<ComponentMessagesDTO> componentMessagesDTOList = new ArrayList<ComponentMessagesDTO>();
        for (TranslationDTO translationDTO : translationList) {
            if (StringUtils.isEmpty(translationDTO)) {
                response.setResponse(APIResponseStatus.BAD_REQUEST);
                return response;
            }
            ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
            componentMessagesDTO.setProductName(updateTranslationDataDTO
                    .getProductName());
            componentMessagesDTO.setVersion(updateTranslationDataDTO
                    .getVersion());
            componentMessagesDTO.setComponent(translationDTO.getComponent());
            componentMessagesDTO.setLocale(translationDTO.getLocale());
            componentMessagesDTO.setMessages(translationDTO.getMessages());
            componentMessagesDTO.setId(System.currentTimeMillis());
            componentMessagesDTOList.add(componentMessagesDTO);
        }
        List<TranslationDTO> translationDTOList = null;
        try {
            translationDTOList = translationSyncServerService
                    .updateBatchTranslation(componentMessagesDTOList);
        } catch (L10nAPIException e) {
            response.setResponse(APIResponseStatus.INTERNAL_SERVER_ERROR);
            LOGGER.error(e.getMessage(), e);
         
        }
        if (translationDTOList != null && translationDTOList.size() > 0) {
            response.setData(translationDTOList);
            response.setResponse(APIResponseStatus.OK);
        }
        saveCreationInfo(updateTranslationDTO);
        return response;
    }
}
