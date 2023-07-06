/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.http.HTTPRequester;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;

/**
 * Interceptor for collection new resource
 */
public class APISourceInterceptor implements HandlerInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(APISourceInterceptor.class);
    private static String PARAM = "param";
    private static String URL = "url";

    /**
     * l10n server url
     */
    private String sourceCacheServerUrl;
    private int sourceReqBodySize = 10485760;

    /**
     * Construction method for init sourceCacheFlag and sourceCacheServerUrl
     */
    public APISourceInterceptor(String sourceCacheServerUrl, int sourceReqSize) {
        this.sourceCacheServerUrl = sourceCacheServerUrl;
        if(sourceReqSize >0) {
        	this.sourceReqBodySize =  sourceReqSize;
        }
    }

    @SuppressWarnings("unchecked")
    public void handleParams(HttpServletRequest request) throws IOException {
        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String productName = pathVariables.get(ConstantsKeys.PRODUCTNAME) == null ? request
                .getParameter(ConstantsKeys.PRODUCTNAME) : pathVariables
                .get(ConstantsKeys.PRODUCTNAME);
        String version = pathVariables.get(ConstantsKeys.VERSION) == null ? request
                .getParameter(ConstantsKeys.VERSION) : pathVariables
                .get(ConstantsKeys.VERSION);
        String component = pathVariables.get(ConstantsKeys.COMPONENT) == null ? request
                .getParameter(ConstantsKeys.COMPONENT) : pathVariables
                .get(ConstantsKeys.COMPONENT);
        if (StringUtils.isEmpty(component)) {
            component = ConstantsKeys.DEFAULT;
        }
        String key = null;
        
                if(pathVariables.get(ConstantsKeys.KEY) == null ) {
                	key = request.getParameter(ConstantsKeys.KEY) == null ? (String) request.getAttribute(ConstantsKeys.KEY) : 
                		(String) request.getParameter(ConstantsKeys.KEY);
                }else {
                	key = pathVariables.get(ConstantsKeys.KEY);
                }
                
        String commentForSource = request
                .getParameter(ConstantsKeys.COMMENT_FOR_SOURCE) == null ? ConstantsKeys.EMPTY_STRING
                : request.getParameter(ConstantsKeys.COMMENT_FOR_SOURCE);
        commentForSource = URLEncoder.encode(commentForSource,
                ConstantsUnicode.UTF8);
        String sourceFormat = request.getParameter(ConstantsKeys.SOURCE_FORMAT) == null ? ConstantsKeys.EMPTY_STRING
                : request.getParameter(ConstantsKeys.SOURCE_FORMAT);
        StringBuilder urlStr = new StringBuilder(sourceCacheServerUrl);
        urlStr.append(L10NAPIV1.CREATE_SOURCE_POST
                .replace("{" + APIParamName.PRODUCT_NAME + "}", productName)
                .replace("{" + APIParamName.COMPONENT + "}", component)
                .replace("{" + APIParamName.KEY2 + "}",
                        URLEncoder.encode(key, ConstantsUnicode.UTF8)));
        Map<String, String> param = new HashMap<String, String>();
        param.put(ConstantsKeys.VERSION, version);
        param.put(ConstantsKeys.COMMENT_FOR_SOURCE, commentForSource);
        param.put(ConstantsKeys.SOURCE_FORMAT, sourceFormat);
        request.setAttribute(PARAM, param);
        request.setAttribute(URL, urlStr.toString());
        String source = request.getAttribute(ConstantsKeys.SOURCE) == null ? request
                .getParameter(ConstantsKeys.SOURCE) : (String) request
                .getAttribute(ConstantsKeys.SOURCE);
        request.setAttribute(ConstantsKeys.SOURCE, source);
    }

    @SuppressWarnings("unchecked")
    public void sendSource(HttpServletRequest request) throws IOException {
        this.handleParams(request);
        if ((request.getAttribute(ConstantsKeys.SOURCE) == null && request
                .getAttribute(PARAM) == null)
                || request.getAttribute(URL) == null) {
            return;
        }
        String source = request.getAttribute(ConstantsKeys.SOURCE) == null ? ""
                : (String) request.getAttribute(ConstantsKeys.SOURCE);
        source = URLEncoder.encode(source, ConstantsUnicode.UTF8);
        Map<String, String> param = (HashMap<String, String>) request
                .getAttribute(PARAM);
        param.put(ConstantsKeys.SOURCE, source);
        String urlStr = (String) request.getAttribute(URL);
        try {
            if(!StringUtils.isEmpty(source)) {
                LOGGER.info("-----Start to send source----------");
                LOGGER.info(source);
                HTTPRequester.postFormData(param, urlStr);
            }
        } catch (VIPHttpException e) {
            LOGGER.error("Failed to send request for source collection");
        } finally {
            request.removeAttribute(ConstantsKeys.KEY);
            request.removeAttribute(ConstantsKeys.SOURCE);
            request.removeAttribute(PARAM);
            request.removeAttribute(URL);
        }
    }

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	
		String contentLength = request.getHeader("content-length");
		if (StringUtils.equalsIgnoreCase(request.getParameter(ConstantsKeys.COLLECT_SOURCE), ConstantsKeys.TRUE)
				&& (contentLength != null) && Integer.valueOf(contentLength) > this.sourceReqBodySize) {
			throw new ValidationException(String.format(ValidationMsg.COLLECTSOURCE_REQUEST_BODY_NOT_VALIDE,
					this.sourceReqBodySize, request.getHeader("content-length")));
		}

		return true;

	}
   
    
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (StringUtils.equalsIgnoreCase(
                request.getParameter(ConstantsKeys.COLLECT_SOURCE),
                ConstantsKeys.TRUE)) {
            this.sendSource(request);
        }
        Object obj = request.getAttribute(ConstantsKeys.UPDATEDTO);
        if (obj != null) {
            this.sendTranslation(request);
        }
    }

    @SuppressWarnings("unchecked")
    public void sendTranslation(HttpServletRequest request)
            throws VIPHttpException {
        UpdateTranslationDTO updateTranslationDTO = (UpdateTranslationDTO) request
                .getAttribute(ConstantsKeys.UPDATEDTO);
        if (updateTranslationDTO.getRequester().equalsIgnoreCase(
                ConstantsKeys.GRM) 
                // release this flag when need send MT translation to l10n server
                //|| updateTranslationDTO.getRequester().equalsIgnoreCase(ConstantsKeys.MT)
                ) {
            ObjectMapper mapper = new ObjectMapper();
            String requestJson = "";
            try {
                requestJson = mapper.writeValueAsString(updateTranslationDTO);
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
            Map<String, String> pathVariables = (Map<String, String>) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String productName = pathVariables.get(ConstantsKeys.PRODUCTNAME) == null ? request
                    .getParameter(ConstantsKeys.PRODUCTNAME) : pathVariables
                    .get(ConstantsKeys.PRODUCTNAME);
            String version = pathVariables.get(ConstantsKeys.VERSION) == null ? request
                    .getParameter(ConstantsKeys.VERSION) : pathVariables
                    .get(ConstantsKeys.VERSION);
            StringBuilder urlStr = new StringBuilder(sourceCacheServerUrl);
            String uri = L10NAPIV1.UPDATE_TRANSLATION_L10N;
            uri = uri.replace("{" + APIParamName.PRODUCT_NAME + "}",
                    productName).replace("{" + APIParamName.VERSION2 + "}",
                    version);
            try {
                LOGGER.info("-----Start to send translation----------");
                LOGGER.info(requestJson);
                HTTPRequester.postJSONStr(requestJson, urlStr.append(uri)
                        .toString());
            } catch (VIPHttpException e) {
                LOGGER.error("Failed to send translation for source collection", e);
            } finally {
                request.removeAttribute(ConstantsKeys.UPDATEDTO);
            }
        }
    }
}
