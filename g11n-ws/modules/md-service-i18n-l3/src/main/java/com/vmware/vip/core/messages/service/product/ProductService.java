/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.CachedKeyGetter;
import com.vmware.vip.common.cache.SingletonCache;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.common.i18n.dto.DropVersionDTO.ComponentVersionDTO;
import com.vmware.vip.common.i18n.dto.DropVersionDTO.ComponentVersionDTO.VersionDTO;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ProductService implements IProductService {

	private static Logger logger = LoggerFactory
			.getLogger(ProductService.class);
	@Autowired
	private IProductDao productdao;

	@Autowired
	private IOneComponentDao oneComponentDao;

	@Autowired
	private SingletonCache singletonCache;

	@Override
	public List<String> getSupportedLocaleList(String productName,
			String version) throws L3APIException {
		List<String> supportedLocaleList = new ArrayList<String>();
		try {
			supportedLocaleList = productdao
					.getLocaleList(productName, version);
			if (supportedLocaleList != null) {
				supportedLocaleList.remove(ConstantsKeys.LATEST);
			}
		} catch (DataException e) {
			throw new L3APIException("Failed to get locale list for " + productName
					+ ConstantsChar.BACKSLASH + version, e);
		}
		return supportedLocaleList;
	}

	@Override
	public List<String> getComponentNameList(String productName, String version)
			throws L3APIException {
		List<String> componentList = new ArrayList<String>();
		try {
			componentList = productdao.getComponentList(productName, version);
		} catch (DataException e) {
			throw new L3APIException("Failed to get component list for " + productName
					+ ConstantsChar.BACKSLASH + version, e);
		}
		return componentList;
	}

	@Override
	public DropVersionDTO getVersionInfo(String productName, String version)
			throws L3APIException {
		DropVersionDTO versioninfo = new DropVersionDTO();
		String jsonStr;
		try {
			jsonStr = productdao.getVersionInfo(productName, version);
		} catch (DataException e) {
			throw new L3APIException("Failed to get drop version info for " + productName
					+ ConstantsChar.BACKSLASH + version, e);
		}
		JSONObject jo = JSONUtils.string2JSON(jsonStr);
		String dropId = jo.get(ConstantsKeys.DROP_ID) == null ? ""
				: (String) jo.get(ConstantsKeys.DROP_ID);
		versioninfo.setDropId(dropId);
		return getDropVersion( versioninfo, jo, version);
	}
	
	
	
	private DropVersionDTO getDropVersion(DropVersionDTO versioninfo,JSONObject jo, String version) {
		Object versionNode = jo.get(version);
		if (versionNode != null) {
			JSONObject t = (JSONObject) versionNode;
			Set<String> s = t.keySet();
			for (String componentName : s) {
				ComponentVersionDTO c = versioninfo.createComponentVersionDTO();
				c.setComponentName(componentName);
				Object versionObj = t.get(componentName);
				if (versionObj != null) {
					JSONObject versionJObj = (JSONObject) versionObj;
					Set<String> vset = versionJObj.keySet();
					for (String locale : vset) {
						VersionDTO v = c.createVersionDTO();
						v.setLocale(locale);
						v.setVersion(versionJObj.get(locale) == null ? ""
								: (String) versionJObj.get(locale));
						c.getVersionList().add(v);
					}
				}
				versioninfo.getComponentList().add(c);
			}
		}
		
		
		return versioninfo;
	}

	/**
	 * Batch synchronization.
	 *
	 * @param componentMessagesDTOList
	 *            The list of ComponentMessagesDTO.
	 * @return List<TranslationDTO> The list of Update failed.
	 *
	 */
	@Override
	public List<TranslationDTO> updateBatchTranslation(
			List<ComponentMessagesDTO> componentMessagesDTOList)
			throws L3APIException {
		List<TranslationDTO> translationDTOList = new ArrayList<TranslationDTO>();
		for (ComponentMessagesDTO componentMessagesDTO : componentMessagesDTOList) {
			try {
				updateTranslation(componentMessagesDTO);
			} catch (VIPCacheException e) {
				throw new L3APIException(
						"Cache occurs error when update translation.", e);
			} catch (DataException e) {
				logger.error(e.getMessage(), e);
				throw new L3APIException("Failed to update translation for "
						+ componentMessagesDTO.getProductName()
						+ ConstantsChar.BACKSLASH
						+ componentMessagesDTO.getVersion(), e);
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
				throw new L3APIException(ConstantsKeys.FATA_ERROR
						+ "Failed to parse content for "
						+ componentMessagesDTO.getProductName()
						+ ConstantsChar.BACKSLASH
						+ componentMessagesDTO.getVersion(), e);
			}
		}
		return translationDTOList;
	}
	
	/**
	 * Synchronize the translation in the componentMessagesDTO to the local
	 * bundle and cache.
	 *
	 * @param componentMessagesDTO
	 *            The object of ComponentMessagesDTO, containing the latest
	 *            translation.
	 * @return boolean Sync successfully, return true, otherwise return false.
	 * @throws DataException
	 * @throws ParseException
	 * @throws VIPCacheException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public boolean updateTranslation(ComponentMessagesDTO componentMessagesDTO)
			throws DataException, JSONException, VIPCacheException {
		String key = CachedKeyGetter
				.getOneCompnentCachedKey(componentMessagesDTO);
		boolean updateFlag = false;
		ComponentMessagesDTO result = null;
		result =  singletonCache.getCachedObject(CacheName.ONECOMPONENT, key,ComponentMessagesDTO.class);
		// merge with local bundle file
		componentMessagesDTO = mergeComponentMessagesDTOWithFile(componentMessagesDTO);
		if (StringUtils.isEmpty(result)) {// not exist in cache
			singletonCache.addCachedObject(CacheName.ONECOMPONENT, key,ComponentMessagesDTO.class, componentMessagesDTO);
			updateFlag = oneComponentDao.update(componentMessagesDTO.getProductName(),
					componentMessagesDTO.getVersion(),
					componentMessagesDTO.getComponent(),
					componentMessagesDTO.getLocale(),
					(Map<String, String>) componentMessagesDTO.getMessages());
		} else {
			updateFlag = oneComponentDao.update(
					componentMessagesDTO.getProductName(),
					componentMessagesDTO.getVersion(),
					componentMessagesDTO.getComponent(),
					componentMessagesDTO.getLocale(),
					(Map<String, String>) componentMessagesDTO.getMessages());
			singletonCache.updateCachedObject(CacheName.ONECOMPONENT, key,
					ComponentMessagesDTO.class, componentMessagesDTO);
		}
		return updateFlag;
	}

	/**
	 * Merge the translation in the componentMessagesDTO and in the local
	 * bundle.
	 *
	 * @param componentMessagesDTO
	 *            the object of ComponentMessagesDTO, containing the latest
	 *            translation.
	 * @return ComponentMessagesDTO a DTO object of ComponentMessagesDTO,
	 *         containing the all translation.
	 * @throws ParseException
	 * @throws DataException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ComponentMessagesDTO mergeComponentMessagesDTOWithFile(
			ComponentMessagesDTO componentMessagesDTO) throws DataException,
			JSONException {
		ComponentMessagesDTO paramComponentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(componentMessagesDTO,
				paramComponentMessagesDTO);
		ComponentMessagesDTO result = null;
		try {
			result = this.getLinkedTranslation(paramComponentMessagesDTO);
		} catch (DataException e1) {
			logger.warn(e1.getMessage(), e1);
		} catch (JSONException e2) {
			logger.error(e2.getMessage(), e2);
		}
		if(!StringUtils.isEmpty(result)) {
			Object messageObj = result.getMessages();
			if (!StringUtils.isEmpty(messageObj)) {
				Map<String, Object> messages = (Map<String, Object>) messageObj;
				Iterator<Map.Entry<String, Object>> it = ((Map) componentMessagesDTO
						.getMessages()).entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					messages.put(entry.getKey(), entry.getValue());
				}
				componentMessagesDTO.setMessages(messages);
			}
		}
		return componentMessagesDTO;
	}

	/**
	 * Get translation from local running environment, it maybe a jar, maybe a
	 * war
	 * 
	 * @param componentMessagesDTO
	 * @return ComponentMessagesDTO object
	 * @throws DataException
	 * @throws ParseException
	 * @see com.vmware.vip.core.translation.dao.BaseComponentDao#getTranslation(Object)
	 */
	private ComponentMessagesDTO getLinkedTranslation(
			ComponentMessagesDTO componentMessagesDTO) throws DataException,
			JSONException {
		SingleComponentDTO caseComponentMessagesDTO = new SingleComponentDTO();
		String result = oneComponentDao.get2JsonStr(
				componentMessagesDTO.getProductName(),
				componentMessagesDTO.getVersion(),
				componentMessagesDTO.getComponent(),
				componentMessagesDTO.getLocale());
		if (StringUtils.isEmpty(result)) {
			componentMessagesDTO.setMessages(result);
			componentMessagesDTO
					.setStatus(TranslationQueryStatusType.ComponentNotFound
							.toString());
			return componentMessagesDTO;
		}
		caseComponentMessagesDTO = SingleComponentDTO
				.getSingleComponentDTOWithLinkedMessages(result);
		caseComponentMessagesDTO.setProductName(componentMessagesDTO
				.getProductName());
		caseComponentMessagesDTO.setVersion(componentMessagesDTO.getVersion());
		caseComponentMessagesDTO.setStatus(componentMessagesDTO.getStatus());
		ComponentMessagesDTO msgDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(caseComponentMessagesDTO, msgDTO);
		return msgDTO;
	}

	public List<String> getSupportedLanguageList(String productName,
			String version) throws L3APIException {
		return getSupportedLocaleList(productName, version);
	}

    /**
     * get a products support versions
     * @return
     * @throws L3APIException
     */
    @Override
    public List<String> getSupportVersionList(String productName) throws L3APIException {
        try {
            return productdao.getVersionList(productName);
        } catch (DataException e) {
           throw new L3APIException(e.getMessage());
        }
    }

    /**
     * get the allow product list
     */
    @Override
    public Map<String, Object> getAllowProductList(String path){


        String content = null;
        try {
			if (path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)){
				content = getClasspathAllowList(path);
			}else{
				content = productdao.getAllowProductListContent(path);
			}

        } catch (DataException e1) {
            logger.warn(e1.getMessage());
            content =null;
        }
        if(!StringUtils.isEmpty(content)) {
            try {
                Map<String, Object> json = new HashMap<String, Object>();
                JsonNode node = new ObjectMapper().readTree(content);
                Iterator<String> names = node.fieldNames();
                for (Iterator<String> iter = names; iter.hasNext();) {
                    String locale = (String) iter.next();
                    json.put(locale, node.get(locale).asText());
                }
                return json;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }else {
            return null;
        }
    }
	private String getClasspathAllowList(String path){
		Resource allowResource = new PathMatchingResourcePatternResolver().getResource(path);
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(allowResource.getInputStream()))) {
			StringBuilder sb = new StringBuilder();
			String temp = "";
			while ((temp = bufferedReader.readLine()) != null) {
				sb.append(temp + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
