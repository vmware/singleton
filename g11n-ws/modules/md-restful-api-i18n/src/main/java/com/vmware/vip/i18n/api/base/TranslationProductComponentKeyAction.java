/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmware.vip.common.utils.SourceFormatUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.KeySourceCommentDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.mt.IMTService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.string.IStringService;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
public class TranslationProductComponentKeyAction extends BaseAction {
	@Autowired
	IStringService stringBasedService;

	@Autowired
	IMTService mtService;

	public APIResponseDTO getTransByGet(String productName, String version,
			String locale, String component, String key, String source, String sourceFormat,
			String pseudo) throws L3APIException {

		StringBasedDTO stringBasedDTO = getTransByKey( productName, version,
		 locale, component, key, source, pseudo);
		
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}

	/*
	 * Get the translation by string-based
	 */
	public APIResponseDTO getStringBasedTranslation(String productName,
			String version, String component, String locale, String key,
			String source, String pseudo, String machineTranslation,
			String sourceFormat, String checkTranslationStatus) throws L3APIException {
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(component);
		c.setVersion(version);
		c.setPseudo(new Boolean(pseudo));
		c.setLocale(locale == null ? ConstantsUnicode.EN : locale);

		StringBasedDTO stringBasedDTO = null;
		if (new Boolean(machineTranslation)) {
			stringBasedDTO = mtService.getStringMTTranslation(c, key, source);
			stringBasedDTO
					.setMachineTranslation(new Boolean(machineTranslation));
		} else {
			stringBasedDTO = stringBasedService.getStringTranslation(c, key,
					source);
		}
		if(new Boolean(checkTranslationStatus)) {
			if(stringBasedDTO.getStatus().indexOf(ConstantsMsg.TRANS_IS_NOT_FOUND) != -1 || stringBasedDTO.getStatus().equals(String.format(ConstantsMsg.EN_NOT_SOURCE, ConstantsMsg.TRANS_FOUND_RETURN))) {
				return super.handleResponse(APIResponseStatus.TRANSLATION_NOT_READY, stringBasedDTO);
			} else {
				return super.handleResponse(APIResponseStatus.TRANSLATION_READY, stringBasedDTO);
			}
		}
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}

	public APIResponseDTO getTransByPost(String productName, String version,
			String locale, String component, String key, String source,
			String commentForSource, String sourceFormat, String collectSource,
			String pseudo, String machineTranslation, String checkTranslationStatus, 
			HttpServletRequest request, HttpServletResponse response)
			throws L3APIException, IOException {
		// find the source by this order: parameter-attribute-body
		if (StringUtils.isEmpty(source)) {
			source = request.getAttribute(ConstantsKeys.SOURCE) == null ? source
					: (String) request.getAttribute(ConstantsKeys.SOURCE);
		}
		if (StringUtils.isEmpty(source)) {
			source = IOUtils.toString(request.getInputStream(),
					ConstantsUnicode.UTF8);
			source = source.equalsIgnoreCase(ConstantsKeys.EMPTY_JSON) ? ConstantsChar.EMPTY
					: source;
		}
		if (!StringUtils.isEmpty(source)) {
			request.setAttribute(ConstantsKeys.SOURCE, source);
		}
		return this.getStringBasedTranslation(productName, version, component,
				locale, key, source, pseudo, machineTranslation, sourceFormat, checkTranslationStatus);
	}

	
	private StringBasedDTO getTransByKey(String productName, String version,
			String locale, String component, String key, String source,
			String pseudo) throws L3APIException {
		
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(StringUtils.isEmpty(component) ? ConstantsKeys.DEFAULT
				: component);
		c.setVersion(version);
		c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		if (ConstantsKeys.TRUE.equalsIgnoreCase(pseudo)) {
			c.setPseudo(new Boolean(pseudo));
		}

		return stringBasedService.getStringTranslation(c, key, source);
		
	}

  
	/**
	 *
	 * Get the translation by mult-key-based
	 *
	 * @param productName
	 * @param version
	 * @param locale
	 * @param component
	 * @param keys
	 * @param pseudo
	 * @return
	 * @throws L3APIException
	 */
	@SuppressWarnings("rawtypes")
	protected APIResponseDTO getMultTransByGet(String productName, String version, String locale, String component,
			String keys, String pseudo) throws L3APIException {
		

		String[] keyArr = null;
		if(keys.contains(ConstantsChar.COMMA)) {
			keyArr = keys.split(ConstantsChar.COMMA);
		}else {
			keyArr = new String[]{keys};
		}
		
		
		SingleComponentDTO compDTo = getTransByKeys( productName, version,
       			 locale, component, keyArr, pseudo);
       
		
       if(compDTo == null || ((Map) compDTo.getMessages()).size()<1) {
       	 throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
       }
      
       if (((Map) compDTo.getMessages()).size() != keyArr.length){
       	return handleResponse(APIResponseStatus.MULTTRANSLATION_PART_CONTENT, compDTo);
       }else {
       	return super.handleResponse(APIResponseStatus.OK, compDTo);
       }
	}
  
	private SingleComponentDTO getTransByKeys(String productName, String version,
			String locale, String component, String[] keyArr,
			String pseudo) throws L3APIException {
		
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(StringUtils.isEmpty(component) ? ConstantsKeys.DEFAULT
				: component);
		c.setVersion(version);
		c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		if (ConstantsKeys.TRUE.equalsIgnoreCase(pseudo)) {
			c.setPseudo(new Boolean(pseudo));
		}

		return stringBasedService.getMultKeyTranslation(c, keyArr);
		
	}
	
	
	protected void validateSourceSetAndKey(List<KeySourceCommentDTO> sourceSet) throws ValidationException {
	
		for(KeySourceCommentDTO ksc:sourceSet) {
			String sft = ksc.getSourceFormat().toUpperCase();
			String key = ksc.getKey();

			if (!StringUtils.isEmpty(sft) && SourceFormatUtils.isBase64Encode(sft)){
				sft = SourceFormatUtils.formatSourceFormatStr(sft);
			}

			if (!StringUtils.isEmpty(sft) && !ConstantsKeys.SOURCE_FORMAT_LIST.contains(sft)) {
				
				throw new ValidationException(String.format(ValidationMsg.SOURCEFORMAT_NOT_VALIDE_FORMAT, sft, key));
			}
			
			if(!RegExpValidatorUtils.isAscii(key)) {
				throw new ValidationException(String.format(ValidationMsg.KEY_NOT_VALIDE_FORMAT, key));
			}
			
			
		}
					
	}

}
