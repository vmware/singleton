/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.multcomponent;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.CachedKeyGetter;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.utils.PseudoConfig;
import com.vmware.vip.core.messages.utils.PseudoMessagesUtils;
import com.vmware.vip.messages.data.dao.api.IMultComponentDao;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * This class handles the translation by single component.
 *
 */
@Service
public class MultComponentService implements IMultComponentService {

	private static Logger LOGGER = LoggerFactory.getLogger(MultComponentService.class);

	// @Autowired
	IProductService productService;

	@Resource
	private IMultComponentDao multipleComponentsDao;
	
    @Autowired
    private IOneComponentDao oneComponentDao;

	@Autowired
	private PseudoConfig pseudoConfig;

	@Override
	public TranslationDTO getMultiComponentsTranslation(
			TranslationDTO translationDTO) throws L3APIException {
		TranslationDTO result = null;
		String key = CachedKeyGetter
				.getMultiComponentsCachedKey(translationDTO);
		try {
			result =  TranslationCache3.getCachedObject(CacheName.MULTCOMPONENT, key, TranslationDTO.class);
			
			
			if (StringUtils.isEmpty(result)) {
				LOGGER.info("Not found in cache, try to get data from local");
				result = this.getTranslation(translationDTO);
				TranslationCache3.addCachedObject(CacheName.MULTCOMPONENT, key,TranslationDTO.class,
						result);
			} else {
				LOGGER.info("Found data from cache["+ key + "]");
			}
		} catch (VIPCacheException e) {
			throw new L3APIException(
					"Faild to get translation from data for  "
							+ translationDTO.getProductName() + ConstantsChar.BACKSLASH
							+ translationDTO.getVersion(), e);
		} catch (ParseException e) {
			throw new L3APIException(ConstantsKeys.FATA_ERROR + "Parse error when get translation for "
					+ translationDTO.getProductName() + ConstantsChar.BACKSLASH
					+ translationDTO.getVersion(), e);
		} catch (DataException e) {
			throw new L3APIException(
					"Faild to get translation from data for "
							+ translationDTO.getProductName() + ConstantsChar.BACKSLASH
							+ translationDTO.getVersion(), e);
		}
		// handle pseudo
		if(translationDTO.getPseudo()) {
			pseudoConfig.setEnabled(translationDTO.getPseudo());
			return PseudoMessagesUtils.getPseudoMessages2(result, pseudoConfig);
		}
		return result;
	}

	@SuppressWarnings({"unchecked" })
	private TranslationDTO getTranslation(TranslationDTO translationDTO)
			throws ParseException, DataException {
		List<String> locales = translationDTO.getLocales();
		List<String> components = translationDTO.getComponents();
		List<String> bundles = multipleComponentsDao.get2JsonStrs(
				translationDTO.getProductName(), translationDTO.getVersion(),
				components, locales);
		JSONArray ja = new JSONArray();
		for (int i = 0; i < bundles.size(); i++) {
			String s = (String) bundles.get(i);
			if (s.equalsIgnoreCase("")) {
				continue;
			}
			JSONObject jo = (JSONObject) new JSONParser().parse(s);
			ja.add(jo);
		}
		translationDTO.setBundles(ja);
		return translationDTO;
	}
	
	
	@SuppressWarnings("unchecked")
    private TranslationDTO getV2Translation(TranslationDTO translationDTO)  throws DataException{
	    List<String> locales = translationDTO.getLocales();
        List<String> components = translationDTO.getComponents();
        if (components == null || locales == null) {
            throw new DataException("No component or locale");
        }
        
        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        JSONArray ja = new JSONArray();
        for (String component : components) {
            for (String locale : locales) {
    
                try {
                    String json = oneComponentDao.get2JsonStr(translationDTO.getProductName(), translationDTO.getVersion(),component, locale);
                    if (StringUtils.isEmpty(json)) {
                        jsonObjectList.add(getNUllBundle(component, locale));
                        continue;
                    }
                    JSONObject jo = (JSONObject) new JSONParser().parse(json);
                    ja.add(jo);
                }catch(Exception e) {
                    jsonObjectList.add(getNUllBundle(component, locale));
                }
            }
        }
        
        
        
 
        if(ja.size()<1) {
           throw new DataException("multcomponent no translation");      
        }else {
            for(JSONObject jsonNullObj:jsonObjectList) {
                ja.add(jsonNullObj);
            }
        }
        translationDTO.setBundles(ja);
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

    /* (non-Javadoc) * @see com.vmware.vip.core.messages.service.multcomponent.IMultComponentService#getV2MultiComponentsTranslation(com.vmware.vip.core.messages.service.multcomponent.TranslationDTO) */
    @Override
    public TranslationDTO getV2MultiComponentsTranslation(TranslationDTO translationDTO)
            throws L3APIException {
        TranslationDTO result = null;
        String key = CachedKeyGetter
                .getMultiComponentsCachedKey(translationDTO);
        try {
            result =  TranslationCache3.getCachedObject(CacheName.MULTCOMPONENT, key, TranslationDTO.class);
            
            if (StringUtils.isEmpty(result)) {
                LOGGER.info("Not found in cache, try to get data from local");
                result = getV2Translation(translationDTO);
                TranslationCache3.addCachedObject(CacheName.MULTCOMPONENT, key,TranslationDTO.class,
                        result);
            } else {
                LOGGER.info("Found data from cache["+ key + "]");
            }
        } catch (VIPCacheException e) {
            throw new L3APIException(
                    "Faild to get translation from data for  "
                            + translationDTO.getProductName() + ConstantsChar.BACKSLASH
                            + translationDTO.getVersion(), e);
        } catch (DataException e) {
            throw new L3APIException(
                    "Faild to get translation from data for "
                            + translationDTO.getProductName() + ConstantsChar.BACKSLASH
                            + translationDTO.getVersion(), e);
        }
        // handle pseudo
        if(translationDTO.getPseudo()) {
            pseudoConfig.setEnabled(translationDTO.getPseudo());
            return PseudoMessagesUtils.getPseudoMessages2(result, pseudoConfig);
        }
        return result;
    }

}
