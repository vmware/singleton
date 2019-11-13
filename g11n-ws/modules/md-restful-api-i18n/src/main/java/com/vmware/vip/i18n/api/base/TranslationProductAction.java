/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.utils.LocaleUtility;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;


public class TranslationProductAction  extends BaseAction {
	private final static String PRODUCT_NAME = "productName";
	private final static String VERSION = "version";
	private static Logger logger = LoggerFactory.getLogger(TranslationProductAction.class);
	@Autowired
	IProductService productService;
	
	@Autowired
	IMultComponentService multipleComponentsService;

	public String getProductTrans(String productName, String version,String locale,String pseudo,
			HttpServletRequest request, HttpServletResponse response)  throws Exception {
		locale = locale == null ? ConstantsUnicode.EN : locale;
		List<String> componentList = productService
				.getComponentNameList(productName, version);
		String newURI = "";
		if (componentList.size() > 0) {
			StringBuffer componentBuffer = new StringBuffer();
			for (String component : componentList) {
				componentBuffer.append(component);
				componentBuffer.append(ConstantsChar.COMMA);
			}
			String components = componentBuffer.toString().substring(0,
					componentBuffer.toString().length() - 1);
			newURI = "/i18n/api/v1/translation/components?components="
					+ components + "&locales=" + locale;
		} else {
			newURI = "/i18n/api/v1/translation/components?locales=" + locale;
		}
		try {
			request.getRequestDispatcher(newURI).forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		return null;
	}	

    private TranslationDTO getAllCompTrans(String productName, String version,String pseudo,
            HttpServletRequest req)  throws Exception {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setProductName(productName);
		List<String> components = productService
				.getComponentNameList(productName, version);
		List<String> localeList = productService.getSupportedLocaleList(productName, version);
        translationDTO.setComponents(components);
        translationDTO.setVersion(version);
        if(new Boolean(pseudo)) {
        	localeList = new ArrayList<String>();
        	localeList.add(ConstantsKeys.LATEST);
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(new Boolean(pseudo));
        } else {
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(new Boolean(pseudo));
        }
        translationDTO = multipleComponentsService.getMultiComponentsTranslation(translationDTO);
        return translationDTO;
    }
    
    
    public APIResponseDTO getMultTrans(String productName, String version, String componentsStr, String localesStr, String pseudo,
            HttpServletRequest req)  throws Exception {
            return getPartialComTrans(productName, componentsStr,  version,  localesStr,  pseudo, req);
    }
   
    
     private TranslationDTO getResultTranslationDTO(String productName,
             String version,String components,String locales, String pseudo,
             HttpServletRequest req) throws Exception {
         TranslationDTO translationDTO = new TranslationDTO();
         translationDTO.setProductName(productName);
         version = CommonUtility.getMatchedVersion(productName, version,  productService.getProductsAndVersions());
         translationDTO.setVersion(version);
         List<String> componentList = null;
         if (StringUtils.isEmpty(components)) {
             componentList = productService.getComponentNameList(productName, version);
         }else {
             componentList = new ArrayList<String>();
             for (String component : components.split(",")) {
                 componentList.add(component.trim());
             }
         }
         translationDTO.setComponents(componentList);
         List<String> localeList = new ArrayList<String>();
         if (new Boolean(pseudo)) {
             localeList.add(ConstantsKeys.LATEST);
         } else if (!StringUtils.isEmpty(locales)) {
             for (String locale : locales.split(",")) {
                 localeList.add(getMappingLocale(productName, version, locale.trim()));
             }      
         } else {
             localeList = productService.getSupportedLocaleList(productName,version);
         }
         translationDTO.setLocales(localeList);
         translationDTO.setPseudo(new Boolean(pseudo));
         return translationDTO;
         
     }
    
     
     
     
    
     
    
     @SuppressWarnings("unchecked")
    private JSONObject getNUllBundle(String component, String locale) {
         JSONObject object = new JSONObject();
         object.put("locale", locale);
         object.put("component", component);
         object.put("messages", null);
         return object;
         
     }
     
     
     private JSONObject getBundle(String component, String locale, TranslationDTO allTranslationDTO) {
         
         JSONArray array = allTranslationDTO.getBundles();
         @SuppressWarnings("unchecked")
        Iterator<JSONObject> objectIterator =  array.iterator();
         
         while(objectIterator.hasNext()) {
             JSONObject object = objectIterator.next();
             String fileLocale = (String) object.get(ConstantsKeys.lOCALE);
             String fileComponent = (String) object.get(ConstantsKeys.COMPONENT);
             if(locale.equals(fileLocale)&& component.equals(fileComponent)) {    
                 return object;
             }
         }
         
        return null; 
     }

     
    /**
     *  get the API v2 mult-component translation
     */
      @SuppressWarnings("unchecked")
    public APIResponseDTO getPartialComTrans(String productName,
               String components, String version, String locales, String pseudo,
               HttpServletRequest req) throws Exception {
          
          TranslationDTO resulttranslationDTO = getResultTranslationDTO( productName, version,components, locales,  pseudo, req);
          TranslationDTO allTranslationDTO  =  getAllCompTrans( resulttranslationDTO.getProductName(),  resulttranslationDTO.getVersion(), pseudo, req);
          List<String> reqLocales = resulttranslationDTO.getLocales();
          List<String> reqComponents = resulttranslationDTO.getComponents();
          
          List<JSONObject> jsonNullList = new ArrayList<JSONObject>();
          JSONArray ja = new JSONArray(); 
          
          for (String component : reqComponents) {
              for (String locale : reqLocales) {
                  JSONObject jsonObj = getBundle( component,  locale,  allTranslationDTO);
                  if(jsonObj != null) {
                      ja.add(jsonObj);
                  }else {
                      jsonNullList.add(getNUllBundle(component, locale));
                  }
                  
                 }
              }
           int reqLocaleSize = reqLocales.size();
           int reqComponentSite = reqComponents.size();
           
          if(ja.isEmpty()) {
              throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
          }else if(ja.size() == (reqLocaleSize*reqComponentSite)) {
              resulttranslationDTO.setBundles(ja);
              return  super.handleResponse(APIResponseStatus.OK, resulttranslationDTO);
          }else {
              for(JSONObject jsonNullObj:jsonNullList) {
                  ja.add(jsonNullObj);
              }
               resulttranslationDTO.setBundles(ja);
              return  super.handleResponse(APIResponseStatus.MULTTRANSLATION_PART_CONTENT, resulttranslationDTO);
              
          }
  
       }
       
      /**
       *this function use to locale fallback 
       *
       */
      private String getMappingLocale(String productName, String version,
              String inputLocale) throws L3APIException {
          List<String> supportedLocaleList = productService
                  .getSupportedLocaleList(productName, version);
          List<Locale> supportedLocales = new ArrayList<Locale>();
          for (String supportedLocale : supportedLocaleList) {
              supportedLocale = supportedLocale.replace("_", "-");
              supportedLocales.add(Locale.forLanguageTag(supportedLocale));
          }
          String requestLocale = inputLocale.replace("_", "-");
          Locale fallbackLocale = LocaleUtility.pickupLocaleFromListNoDefault(
                  supportedLocales, Locale.forLanguageTag(requestLocale));
          return fallbackLocale.toLanguageTag();
      }
      
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public APIResponseDTO getSLocales(String productName,String version,
			HttpServletRequest request) throws Exception{
		List<String> localeList = productService.getSupportedLocaleList(
				productName, version);
		Map data = new HashMap();
		data.put(PRODUCT_NAME, productName);
		data.put(VERSION, version);
		data.put("locales", localeList);
		return super.handleResponse(APIResponseStatus.OK, data);
	}

    public APIResponseDTO getCNameList(String productName,String version,
            HttpServletRequest request)  throws Exception {
        List<String> componentList= productService.getComponentNameList(productName, version);
        Map<String,Object> data = new HashMap<String,Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, version);
        data.put("components", componentList);
        return super.handleResponse(APIResponseStatus.OK, data);
    }

    public APIResponseDTO getVersionInfo(String productName,String version) throws L3APIException{
        DropVersionDTO versioninfo = productService.getVersionInfo(productName, version);
        Map<String,Object> data = new HashMap<String,Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, version);
        data.put("versioninfo", versioninfo);
        return super.handleResponse(APIResponseStatus.OK, data);
    }
}
