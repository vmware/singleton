/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;
import java.util.*;


import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.string.IStringService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.i18n.api.base.utils.VersionMatcher;


public class TranslationProductAction  extends BaseAction {
    private final static String PRODUCT_NAME = "productName";
    private final static String VERSION = "version";
    private static Logger logger = LoggerFactory.getLogger(TranslationProductAction.class);
    @Autowired
    IProductService productService;

    @Autowired
    IMultComponentService multipleComponentsService;
    @Autowired
    IStringService stringBasedService;

    public String getProductTrans(String productName, String version, String locale, String pseudo,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        locale = locale == null ? ConstantsUnicode.EN : getMappingLocale(productName, version, locale.trim());
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

    private TranslationDTO getAllCompTrans(String productName, String version, boolean pseudo,
                                           HttpServletRequest req) throws Exception {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setProductName(productName);
        List<String> components = productService
                .getComponentNameList(productName, version);
        List<String> localeList = productService.getSupportedLocaleList(productName, version);
        translationDTO.setComponents(components);
        translationDTO.setVersion(version);
        if (pseudo) {
            localeList = new ArrayList<String>();
            localeList.add(ConstantsKeys.LATEST);
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(pseudo);
        } else {
            translationDTO.setLocales(localeList);
            translationDTO.setPseudo(pseudo);
        }
        translationDTO = multipleComponentsService.getMultiComponentsTranslation(translationDTO);
        return translationDTO;
    }


    public APIResponseDTO getMultTrans(String productName, String version, String componentsStr, String localesStr, String pseudo,
                                       HttpServletRequest req) throws Exception {
        return getPartialComTrans(productName, componentsStr, version, localesStr, pseudo, req);
    }


    private TranslationDTO getResultTranslationDTO(String productName,
                                                   String version, String components, String locales, String pseudo,
                                                   HttpServletRequest req) throws Exception {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setProductName(productName);
        version = VersionMatcher.getMatchedVersion(version, productService.getSupportVersionList(productName));
        translationDTO.setVersion(version);
        List<String> componentList = null;
        if (StringUtils.isEmpty(components)) {
            componentList = productService.getComponentNameList(productName, version);
        } else {
            componentList = new ArrayList<String>();
            for (String component : components.split(",")) {
                componentList.add(component.trim());
            }
        }

        translationDTO.setComponents(componentList);
        List<String> localeList = new ArrayList<String>();
        if (Boolean.parseBoolean(pseudo)) {
            localeList.add(ConstantsKeys.LATEST);
        } else if (!StringUtils.isEmpty(locales)) {
            List<String> supportedLocaleList = productService
                    .getSupportedLocaleList(productName, version);
            for (String locale : locales.split(",")) {
                localeList.add(getFormatLocale(productName, version, locale.trim(), supportedLocaleList));
            }
        } else {
            localeList = productService.getSupportedLocaleList(productName, version);
        }
        translationDTO.setLocales(localeList);
        translationDTO.setPseudo(Boolean.parseBoolean(pseudo));
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
        Iterator<JSONObject> objectIterator = array.iterator();

        while (objectIterator.hasNext()) {
            JSONObject object = objectIterator.next();
            String fileLocale = (String) object.get(ConstantsKeys.lOCALE);
            String fileComponent = (String) object.get(ConstantsKeys.COMPONENT);
            if (locale.equals(fileLocale) && component.equals(fileComponent)) {
                return object;
            }
        }

        return null;
    }


    /**
     * get the API v2 mult-component translation
     */
    @SuppressWarnings("unchecked")
    public APIResponseDTO getPartialComTrans(String productName,
                                             String components, String version, String locales, String pseudo,
                                             HttpServletRequest req) throws Exception {
        String oldVersion = version;
        version = super.getAvailableVersion(productName, oldVersion);
        TranslationDTO resulttranslationDTO = getResultTranslationDTO(productName, version, components, locales, pseudo, req);
        TranslationDTO allTranslationDTO = getAllCompTrans(resulttranslationDTO.getProductName(), resulttranslationDTO.getVersion(), resulttranslationDTO.getPseudo(), req);
        List<String> reqLocales = resulttranslationDTO.getLocales();
        List<String> reqComponents = resulttranslationDTO.getComponents();

        List<JSONObject> jsonNullList = new ArrayList<JSONObject>();
        JSONArray ja = new JSONArray();

        for (String component : reqComponents) {
            if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(component)) {
                throw new ValidationException(ValidationMsg.COMPONENT_NOT_VALIDE);
            }

            for (String locale : reqLocales) {
                JSONObject jsonObj = getBundle(component, locale, allTranslationDTO);
                if (jsonObj != null) {
                    ja.add(jsonObj);
                } else {
                    jsonNullList.add(getNUllBundle(component, locale));
                }

            }
        }
        int reqLocaleSize = reqLocales.size();
        int reqComponentSite = reqComponents.size();

        if (ja.isEmpty()) {
            throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
        } else if (ja.size() == (reqLocaleSize * reqComponentSite)) {
            resulttranslationDTO.setBundles(ja);
            return super.handleVersionFallbackResponse(oldVersion, version, resulttranslationDTO);
        } else {
            for (JSONObject jsonNullObj : jsonNullList) {
                ja.add(jsonNullObj);
            }
            resulttranslationDTO.setBundles(ja);
            if (oldVersion.equals(version)) {
                return handleResponse(APIResponseStatus.MULTTRANSLATION_PART_CONTENT, resulttranslationDTO);
            } else {
                return handleResponse(APIResponseStatus.VERSION_FALLBACK_TRANSLATION, resulttranslationDTO);
            }
        }

    }

    /**
     * this function use to locale fallback
     */
    public String getMappingLocale(String productName, String version,
                                   String inputLocale) throws L3APIException {
        List<String> supportedLocaleList = productService
                .getSupportedLocaleList(productName, version);
        return getFormatLocale(productName, version, inputLocale, supportedLocaleList);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public APIResponseDTO getSLocales(String productName, String version,
                                      HttpServletRequest request) throws Exception {
        String newVersion = super.getAvailableVersion(productName, version);
        List<String> localeList = productService.getSupportedLocaleList(
                productName, newVersion);
        Map data = new HashMap();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, newVersion);
        data.put("locales", localeList);
        return super.handleVersionFallbackResponse(version, newVersion, data);
    }

    public APIResponseDTO getCNameList(String productName, String version,
                                       HttpServletRequest request) throws Exception {
        String newVersion = super.getAvailableVersion(productName, version);
        List<String> componentList = productService.getComponentNameList(productName, newVersion);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, newVersion);
        data.put("components", componentList);
        return super.handleVersionFallbackResponse(version, newVersion, data);
    }

    public APIResponseDTO getVersionInfo(String productName, String version) throws L3APIException {
        String availableVersion = super.getAvailableVersion(productName, version);
        DropVersionDTO versioninfo = productService.getVersionInfo(productName, availableVersion);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(PRODUCT_NAME, productName);
        data.put(VERSION, availableVersion);
        data.put("versioninfo", versioninfo);
        return super.handleVersionFallbackResponse(version, availableVersion, data);
    }

    protected APIResponseDTO getVersionList(String productName) throws L3APIException {
        List<String> versionList = productService.getSupportVersionList(productName);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(PRODUCT_NAME, productName);
        data.put("versions", versionList);
        return super.handleResponse(APIResponseStatus.OK, data);
    }

    protected APIResponseDTO getVersionsTransByGet(String productName, String versions, String component, String locale,String key) throws L3APIException {
        String[] versionArr = null;
        if (versions.contains(ConstantsChar.COMMA)) {
            versionArr = versions.split(ConstantsChar.COMMA);
        } else {
            versionArr = new String[]{versions};
        }
        ComponentMessagesDTO compReq = new ComponentMessagesDTO();
        compReq.setProductName(productName);
        compReq.setComponent(component);
        compReq.setLocale(locale);
        compReq.setPseudo(false);

        List<StringBasedDTO> data = stringBasedService.getMultiVersionKeyTranslation(compReq, Arrays.asList(versionArr), key);
        return super.handleResponse(APIResponseStatus.OK, data);
    }

}
