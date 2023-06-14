/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vmware.vip.common.utils.RegExpValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.vmware.i18n.l2.service.pattern.IPatternService;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.TransWithPatternDataScope;
import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.i18n.dto.TranslationWithPatternDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;
import com.vmware.vip.i18n.api.base.utils.ParameterValidationUtility;
import com.vmware.vip.i18n.api.base.utils.VersionMatcher;

public class TranslationWithPatternAction extends BaseAction {

	   public static Logger logger = LoggerFactory.getLogger(TranslationWithPatternAction.class);

	   @Autowired
	   IOneComponentService singleComponentService;
      
	   @Autowired
	   IProductService productService;
	  
	   @Autowired
	   IPatternService patternService;

	   /**
	    * According to TranslationWithPatternDTO object get the pattern and translation or only get the
	    * pattern
	    * 
	    */
	   public APIResponseDTO getTransPattern(TranslationWithPatternDTO data) throws Exception {
	      logger.info("begin getTransPattern");
	      boolean validateResult;
	      try {
	    	  validateResult = validate(data);
	      }catch(VIPAPIException excep) {
	    	  return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(),
	    			  excep.getMessage(), null);
	      }
	      if (validateResult) {
              if (!CommonUtil.isEmpty(data.getScopeFilter()) && !!RegExpValidatorUtils.startLetterAndCommValidchar(data.getScopeFilter())) {
                  return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.SCOPE_FILTER_NOT_VALIDATE, null);
              }

	    	  List<String> categories = CommonUtility.getCategoriesByEnum(data.getScope(), true);
	    	  if (CommonUtil.isEmpty(categories)) {
	        	  return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.PATTERN_NOT_VALIDATE, null);
		      }
	          List<String> availableVersions = null;
	          try {
	        	  availableVersions = productService.getSupportVersionList(data.getProductName());
	          }catch(L3APIException e) {
	          }
	         String oldVersion = data.getVersion();
             data.setVersion(VersionMatcher.getMatchedVersion(data.getVersion(), availableVersions));
	         Map<String, Object> pattern = getPattern(data, categories);
	         List<ComponentMessagesDTO> components = getTranslation(data);
	         Map<String, Object> map = new HashMap<String, Object>();
	         map.put(ConstantsKeys.PATTERN, pattern);
	         map.put(ConstantsKeys.COMPONENTS, components);
	         return super.handleVersionFallbackResponse(oldVersion, data.getVersion(), map);
	      } else {
	         return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(),
	                    ConstantsMsg.PARAM_NOT_VALIDATE, null);
	      }
	   }

    /**
     * According to object get the pattern and translation or only get the pattern when use GET
     * method
     * 
     */
    public APIResponseDTO getTransPattern(int combine, String productName, String version,
            String components, String language, String scope, String region, String pseudo, String scopeFilter)
            throws Exception {
        // TODO Auto-generated method stub
        TranslationWithPatternDTO data = new TranslationWithPatternDTO();
        data.setCombine(combine);
        data.setLanguage(language);
        data.setScope(scope);
        data.setProductName(productName);
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        if (CommonUtil.isEmpty(categories)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.PATTERN_NOT_VALIDATE, null);
        }

        if (!CommonUtil.isEmpty(scopeFilter) && !RegExpValidatorUtils.startLetterAndCommValidchar(scopeFilter)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.SCOPE_FILTER_NOT_VALIDATE, null);
        }

        String newversion=null;
        try {
        	newversion = VersionMatcher.getMatchedVersion(version, productService.getSupportVersionList(productName));
        }catch(Exception e) {
        	newversion = version;
        }
        data.setVersion(newversion);
        if (!StringUtils.isEmpty(components)) {
            data.setComponents(Arrays.asList(components.split(ConstantsChar.COMMA)));
        }
        data.setRegion(region);
        data.setPseudo(pseudo);
		data.setScopeFilter(scopeFilter);
        if (validateCombineType(data)) {
            Map<String, Object> pattern = getPattern(data, categories);
            List<ComponentMessagesDTO> compList = getTranslation(data);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ConstantsKeys.PATTERN, pattern);
            map.put(ConstantsKeys.COMPONENTS, compList);
            return super.handleVersionFallbackResponse(version, newversion, map);
        } else {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(),
                    ConstantsMsg.PARAM_NOT_VALIDATE, null);
        }
    }
       /**
        * This is use to validate the request data of get Translation with Pattern when mehod is GET
        * 
        * 
        */
       private boolean validateCombineType(TranslationWithPatternDTO data) {
           
           if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_WITH_REGION
                 .getValue()) {
              return isPatternTransaltionWithRegion(data);
           } else if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_NO_REGION
                 .getValue()) {
              return isPatternTranslationNoRegion(data);
           }else {
               return false;    
           }
       }
       

	   /**
	    * This is use to validate the request data of get Translation with Pattern
	    * 
	    * 
	    */
	   public boolean validate(TranslationWithPatternDTO data) throws VIPAPIException {
		   ParameterValidationUtility.validateTranslationWithPatternAPI(data);
	      if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_WITH_REGION
	            .getValue()) {
	         return isPatternTransaltionWithRegion(data);
	      } else if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_NO_REGION
	            .getValue()) {
	         return isPatternTranslationNoRegion(data);
	      } else if (data.getCombine() == TransWithPatternDataScope.ONLY_PATTERN_WITH_REGION
	            .getValue()) {
	         return isPatternWithRegion(data);
	      } else if (data.getCombine() == TransWithPatternDataScope.ONLY_PATTERN_NO_REGION.getValue()) {
	         return isPatternNoRegion(data);
	      } else {
	         return false;
	      }

	   }

	   /**
	    * Validate request Object of get the pattern and translation when combine field value have
	    * region parameter
	    *
	    * 
	    */
	   public boolean isPatternTransaltionWithRegion(TranslationWithPatternDTO data) {
	      return StringUtils.isNoneEmpty(data.getLanguage()) && StringUtils.isNoneEmpty(data.getScope())
	            && StringUtils.isNoneEmpty(data.getProductName())
	            && StringUtils.isNoneEmpty(data.getVersion())
	            && StringUtils.isNoneEmpty(data.getRegion()) && (data.getComponents().size() > 0);
	   }

	   /**
	    * Validate request object of get the pattern and translation when request data no region
	    * parameter
	    *
	    * 
	    */
	   public boolean isPatternTranslationNoRegion(TranslationWithPatternDTO data) {
	      return StringUtils.isNoneEmpty(data.getLanguage()) && StringUtils.isNoneEmpty(data.getScope())
	            && StringUtils.isNoneEmpty(data.getProductName())
	            && StringUtils.isNoneEmpty(data.getVersion()) && (data.getComponents().size() > 0);
	   }

	   /**
	    * Validate get the pattern request Object when request data has region parameter
	    *
	    * 
	    */
	   public boolean isPatternWithRegion(TranslationWithPatternDTO data) {
	      return StringUtils.isNoneEmpty(data.getLanguage()) && StringUtils.isNoneEmpty(data.getScope())
	            && StringUtils.isNoneEmpty(data.getRegion());
	   }

	   /**
	    * Validate get the pattern request Object when request data no region parameter
	    *
	    * 
	    */
	   public boolean isPatternNoRegion(TranslationWithPatternDTO data) {
	      return StringUtils.isNoneEmpty(data.getLanguage())
	            && StringUtils.isNoneEmpty(data.getScope());
	   }

	   /**
	    * According to TranslationWithPatternDTO object get the pattern that is use to combine the
	    * translation with pattern response
	    *
	    */
	   @SuppressWarnings("unchecked")
	   public Map<String, Object> getPattern(TranslationWithPatternDTO data, List<String> categories) throws Exception {
	      Map<String, Object> pattern = null;
	      if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_WITH_REGION.getValue()
	            || data.getCombine() == TransWithPatternDataScope.ONLY_PATTERN_WITH_REGION.getValue()) {
	         pattern = patternService.getPatternWithLanguageAndRegion(data.getLanguage(),
	               data.getRegion(), categories, data.getScopeFilter());
	      } else if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_NO_REGION
	            .getValue()
	            || data.getCombine() == TransWithPatternDataScope.ONLY_PATTERN_NO_REGION.getValue()) {
	         pattern = patternService.getPattern(data.getLanguage(), categories, data.getScopeFilter());
	      }
	      if (pattern == null) {
	         pattern = new HashMap<String, Object>();
	      }
	      if (!pattern.containsKey(ConstantsKeys.IS_EXIST_PATTERN)) {
	         boolean existPatternFlag = false;
	         Map<String, Object> cats = (Map<String, Object>) pattern.get(ConstantsKeys.CATEGORIES);
	         if (cats != null) {
	            for (Entry<String, Object> entry : cats.entrySet()) {
	               if (entry.getValue() != null) {
	                  if (!(entry.getValue() instanceof String)) {
	                     existPatternFlag = true;
	                     break;
	                  } else if (StringUtils.isNotEmpty((String) entry.getValue())) {
	                     existPatternFlag = true;
	                     break;
	                  }
	               }
	            }
	         }
	         pattern.put(ConstantsKeys.IS_EXIST_PATTERN, existPatternFlag);
	      }
	      return pattern;
	   }

	   /**
	    * This is get the translation by use TranslationWithPatternDTO for combine the translation with
	    * pattern response
	    *
	    *
	    */
	   public List<ComponentMessagesDTO> getTranslation(TranslationWithPatternDTO data) {
	      List<ComponentMessagesDTO> components = null;
	      if (data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_WITH_REGION.getValue()
	            || data.getCombine() == TransWithPatternDataScope.TRANSLATION_PATTERN_NO_REGION
	                  .getValue()) {
	         components = new ArrayList<ComponentMessagesDTO>();
	         for (String component : data.getComponents()) {
	            try {
	               ComponentMessagesDTO result = getSingleComponentTrans(data.getProductName(),
	                     component, data.getVersion(), data.getLanguage(), data.getPseudo());
	               components.add(result);
	            } catch (Exception e) {
	               continue;
	            }
	         }
	         if (components.size() < 1) {
	            components = null;
	         }
	      }
	      return components;
	   }

	   public ComponentMessagesDTO getSingleComponentTrans(String productName, String component,
	         String version, String locale, String pseudo) throws L3APIException {
	      ComponentMessagesDTO c = new ComponentMessagesDTO();
	      c.setProductName(productName);
	      c.setComponent(component == null ? ConstantsKeys.DEFAULT : component.trim());
	      c.setVersion(version);
	      if (new Boolean(pseudo)) {
	         c.setLocale(ConstantsKeys.LATEST);
	      } else {
	         c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
	      }
	      c.setPseudo(new Boolean(pseudo));
	      c = singleComponentService.getComponentTranslation(c);
	      return c;
	   }
}


