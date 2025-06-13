/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.product;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.i18n.dto.DropVersionDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.messages.data.dao.exception.DataException;
/**
 * This class manipulates locales.
 *
 */
public interface IProductService {
    /**
     * Get supported locales by product name and version.
     *
     */
    public List<String> getSupportedLocaleList(String productName, String version)  throws L3APIException ;

    /**
     * Get component's names by product name and version.
     *
     */
    public List<String> getComponentNameList(String productName, String version)  throws L3APIException ;

    /**
     * Update translation,this is used for on premise environment
     *
     * @param componentMessagesDTO A ComponentMessagesDTO object
     * @return
     */
    public List<TranslationDTO> updateBatchTranslation(List<ComponentMessagesDTO> componentMessagesDTOList) throws L3APIException;
    
    public boolean updateTranslation(ComponentMessagesDTO componentMessagesDTO)
			throws DataException, JSONException, VIPCacheException;

    /**
     * Get supported language list
     * @param productName
     * @param version
     * @return language list
     */
    public List<String> getSupportedLanguageList(String productName, String version) throws L3APIException;

    /**
     * get version info
     * 
     * @param productName
     * @param version
     * @return
     * @throws L3APIException
     */
	public DropVersionDTO getVersionInfo(String productName, String version) throws L3APIException;

    /**
     * get a products support versions
     * @return
     * @throws L3APIException
     */
    public List<String> getSupportVersionList(String productName) throws L3APIException;
    
    /**
     * get the allow product List
     */
    public Map<String, Object> getAllowProductList(String path);

}
