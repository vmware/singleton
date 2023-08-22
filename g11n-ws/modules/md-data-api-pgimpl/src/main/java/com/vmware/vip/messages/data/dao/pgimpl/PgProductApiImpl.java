/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmware.vip.messages.data.dao.api.IProductDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.pgimpl.balance.PgDataNodeBalancerAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IDocOperate;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
@Service
public class PgProductApiImpl implements IProductDao {

	 private static Logger logger = LoggerFactory.getLogger(PgProductApiImpl.class);
	@Autowired
	private PgDataNodeBalancerAdapter datanodes;

	@Autowired
	private IDocOperate docOperate;

	@Override
	public List<String> getComponentList(String productName, String version) throws DataException {
		// TODO Auto-generated method stub
	List<String> result = docOperate.getComponentList(productName, version, datanodes.getDataNodeByProduct(productName));
	if(result ==null || result.size()==0) {
		String errInfo = String.format("this product: %s version: %s no component!",productName, version);
		logger.warn(errInfo);
		throw new DataException(errInfo);
	}
	return result;
	}

	@Override
	public List<String> getLocaleList(String productName, String version) throws DataException {
		// TODO Auto-generated method stub
		List<String> result = docOperate.getLocaleList(productName, version, datanodes.getDataNodeByProduct(productName));
		List<String> result_filer=null;
		
		if(result ==null || result.size()==0) {
			String errInfo = String.format("this product: %s version: %s no support locale!", productName, version);
			logger.warn(errInfo);
			throw new DataException(errInfo);
		}
		
		if(result != null) {
			result_filer = new ArrayList<String>(); 
		}
		
		for(String locale: result) {
			if(!(locale.equals("latest"))) {
				result_filer.add(locale);
			
			}
			
		}
		
		result = result_filer;
		
	
		return result;
	
	}

	@Override
	public String getVersionInfo(String productName, String version) throws DataException {
		return "";
	}

       /**
        * get one product's all available versions
        */
       @Override
        public List<String> getVersionList(String productName) throws DataException {
             List<String> result = docOperate.getVersionList(productName, datanodes.getDataNodeByProduct(productName));
             if(result != null && result.size()>0) {
                return result;
             }else {
            throw new DataException(productName + " no available version in pgDB");   
             }
        }

   /**
    * get the allow list content from pg db
    */
    @Override
    public String getAllowProductListContent(String path) throws DataException {
        return null;
    }


}
