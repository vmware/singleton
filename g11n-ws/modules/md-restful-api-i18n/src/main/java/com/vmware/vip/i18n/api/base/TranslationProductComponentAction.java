/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.vmware.vip.core.messages.exception.L3APIException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.translation.TranslationCompareUtil;
import com.vmware.vip.core.messages.service.mt.IMTService;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;

public class TranslationProductComponentAction extends BaseAction {
	@Autowired
	IOneComponentService singleComponentService;

	@Autowired
	IMultComponentService multipleComponentsService;

	@Autowired
	IProductService productService;

	@Autowired
	IMTService mtService;

	public APIResponseDTO getSingleComponentTrans(String productName,
			String component, String version, String locale, String pseudo,
			String mt, HttpServletRequest request) throws Exception {
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(component == null ? ConstantsKeys.DEFAULT : component);
		c.setVersion(this.getMatchedVersion(productName, version));
		if (new Boolean(pseudo)) {
			c.setLocale(ConstantsKeys.LATEST);
		} else {
			c.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		}
		c.setPseudo(new Boolean(pseudo));
		// get MT translation
		if (new Boolean(mt)) {
			c.setLocale(locale);
			c = mtService.getComponentMTTranslation(c);
			c.setMachineTranslation(true);
			// Construct UpdateTranslationDTO and send to git repository
			if (c.getMessages() != null) {
				UpdateTranslationDTO updateTranslationDTO = new UpdateTranslationDTO();
				updateTranslationDTO.setRequester(ConstantsKeys.MT);
				UpdateTranslationDataDTO data = new UpdateTranslationDataDTO();
				data.setProductName(c.getProductName());
				data.setVersion(c.getVersion());
				List<UpdateTranslationDataDTO.TranslationDTO> list = new ArrayList<UpdateTranslationDataDTO.TranslationDTO>();
				UpdateTranslationDataDTO.TranslationDTO translationDTO = new UpdateTranslationDataDTO.TranslationDTO();
				translationDTO.setComponent(c.getComponent());
				translationDTO.setLocale(c.getLocale());
				translationDTO.setMessages((Map<String, String>) c
						.getMessages());
				list.add(translationDTO);
				data.setTranslation(list);
				data.setMachineTranslation(true);
				updateTranslationDTO.setData(data);
				request.setAttribute(ConstantsKeys.UPDATEDTO,
						updateTranslationDTO);
			}
		} else {
			c = singleComponentService.getComponentTranslation(c);
		}
		return super.handleResponse(APIResponseStatus.OK, c);
	}

	public APIResponseDTO getMultipleComponentsTrans(String productName,
			String components, String version, String locales, String pseudo,
			HttpServletRequest req) throws Exception {
		TranslationDTO translationDTO = new TranslationDTO();
		translationDTO.setProductName(productName);
		translationDTO.setVersion(version);

		List<String> componentList = new ArrayList<String>();
		for (String component : components.split(",")) {
			componentList.add(component.trim());
		}
		translationDTO.setComponents(componentList);

		List<String> localeList = new ArrayList<String>();
		if (new Boolean(pseudo)) {
			localeList.add(ConstantsKeys.LATEST);
		} else if (!StringUtils.isEmpty(locales)) {
			for (String locale : locales.split(",")) {
				localeList.add(locale.trim());
			}
		} else {
			localeList = productService.getSupportedLocaleList(productName,
					version);
		}
		translationDTO.setLocales(localeList);
		translationDTO.setPseudo(new Boolean(pseudo));

		translationDTO = multipleComponentsService
				.getMultiComponentsTranslation(translationDTO);
		return super.handleResponse(APIResponseStatus.OK, translationDTO);
	}
	
	
	public APIResponseDTO checkTranslationResult(String productName, String component, String version, String locale, APIResponseDTO resp) {
		Map<String, String> r = checkTranslationStatus(productName, component, version, locale, resp);
		Object o = resp.getData();
		if(o instanceof ComponentMessagesDTO) {
			((ComponentMessagesDTO)o).setStatus(JSONObject.toJSONString(r));
		}
    	if(!r.isEmpty() && !r.containsValue("0")) {
    		return super.handleResponse(APIResponseStatus.TRANSLATION_READY, resp.getData());
    	} else {
    		return super.handleResponse(APIResponseStatus.TRANSLATION_NOT_READY, resp.getData());    	
    	}    	
    }
    
	@SuppressWarnings("unchecked")
	private Map<String, String> checkTranslationStatus(String productName,
			String component, String version, String locale, APIResponseDTO resp) {
		ComponentMessagesDTO c = new ComponentMessagesDTO();
		c.setProductName(productName);
		c.setComponent(component == null ? ConstantsKeys.DEFAULT : component);
		c.setVersion(version);
		ComponentMessagesDTO lastest_result = null;
		ComponentMessagesDTO en_result = null;
		Map<String, String> m = new HashMap<String, String>();
		if ((locale.substring(0, 2)).equalsIgnoreCase(ConstantsUnicode.EN)
				&& ((ComponentMessagesDTO) resp.getData()).getLocale()
						.equalsIgnoreCase(ConstantsUnicode.EN)) {
			en_result = (ComponentMessagesDTO) resp.getData();
			c.setLocale(ConstantsKeys.LATEST);
			try {
				lastest_result = singleComponentService
						.getTranslationFromDisk(c);
			} catch (Exception e) {
				lastest_result = null;
			}
		} else if ((!(locale.substring(0, 2))
				.equalsIgnoreCase(ConstantsUnicode.EN))
				&& ((ComponentMessagesDTO) resp.getData()).getLocale()
						.equalsIgnoreCase(ConstantsUnicode.EN)) {
			return m;
		} else {
			try {
				c.setLocale(ConstantsUnicode.EN);
				en_result = singleComponentService.getTranslationFromDisk(c);
				c.setLocale(ConstantsKeys.LATEST);
				lastest_result = singleComponentService
						.getTranslationFromDisk(c);
			} catch (Exception e) {
				en_result = null;
				lastest_result = null;
			}
		}
		if (en_result != null && lastest_result != null) {
				Map<String, Object> latest = (Map<String, Object>) lastest_result
						.getMessages();
				Map<String, Object> en = (Map<String, Object>) en_result
						.getMessages();
				m = TranslationCompareUtil.compareComponentMessage(latest,
						en);
		}
		return m;
	}

	/**
	 * Get the the matched version from the version list by comparing to the input version
	 *
	 * @param productName
	 * @param version
	 * @return a matched version, if there's no matched version then return input version
	 */
	private String getMatchedVersion(final String productName, final String version) throws L3APIException{
		Map<String, String[]> productsAndVersions = productService.getProductsAndVersions();
		String matchedVersion = "";
		if(productsAndVersions != null) {
			String[] versionList = productsAndVersions.get(productName);
			if(versionList != null) {
				if(Arrays.asList(versionList).contains(version)) {
					return version;
				}
				for(String s : versionList) {
					String f = filterVersion(s, version);
					String ss = s;
					String m = matchedVersion;
					if(!StringUtils.isEmpty(matchedVersion)) {
						if(ss.split("\\.").length > matchedVersion.split("\\.").length) {
							m = filterVersion(matchedVersion, ss);
						}
					}
					if((compare(f, version) == -1 || (compare(f, version) == 0 && s.length() < version.length())) && compare(ss, m) == 1) {
                        matchedVersion = s;
					}
				}
			}
		}
		return StringUtils.isEmpty(matchedVersion)? version : matchedVersion;
	}

	/**
	 * Compare source version and target version
	 *
	 * @param source
	 * @param target
	 * @return 0, equal; -1 less than; 1 bigger than
	 */
	private int compare(final String source, final String target) {
		if (StringUtils.equals(source, target)) {
			return 0;
		}
		if(!StringUtils.isEmpty(source) && StringUtils.isEmpty(target)) {
			return 1;
		}
		String[] s = source.split("\\.");
		String[] t = target.split("\\.");
		int b = -1;
		if(s.length == t.length) {
			for(int i = 0; i < s.length; i++) {
				if(Integer.parseInt(s[i]) > Integer.parseInt(t[i])) {
					b = 1;
					break;
				} else if(Integer.parseInt(s[i]) < Integer.parseInt(t[i])) {
					break;
				}
			}
		}
		return b;
	}

	/**
	 * Filter the version number, and append insufficient subversion or remove extra subversion
	 *
	 * @param originVersion
	 * @param requestVersion
	 * @return e.g  originVersion = 2.0, requestVersion = 1.0.0, will return 2.0.0;
	 *         e.g  originVersion = 2.0.0, requestVersion = 1.0, will return 2.0
	 */
	private String filterVersion(final String originVersion, final String requestVersion) {
		String filteredVersion = originVersion;
		int o = originVersion.split("\\.").length;
		int r = requestVersion.split("\\.").length;
		if(o < r) {
			for(int i=0; i < (r - o); i++) {
				filteredVersion = new StringBuilder(filteredVersion).append(".0").toString();
			}
		} else if (o > r) {
			for(int i=0; i < (o - r); i++) {
				filteredVersion = filteredVersion.substring(0, filteredVersion.lastIndexOf('.'));
			}
		}
		return filteredVersion;
	}
}
