/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.messages.data.dao.api.IPgVipDbTabApi;
import com.vmware.vip.messages.data.dao.api.IStringDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;
import com.vmware.vip.messages.data.dao.pgimpl.balance.PgDataNodeBalancerAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.exception.ProductUnregisteredException;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IDocOperate;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IStrOperate;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
@Service
public class PgStrApiImpl implements IStringDao {
	private static Logger logger = LoggerFactory.getLogger(PgStrApiImpl.class);
	@Autowired
	private PgDataNodeBalancerAdapter datanodes;

	@Autowired
	private IStrOperate strOperator;

	@Autowired
	private IDocOperate docOperate;
	
	@Autowired
	private IPgVipDbTabApi pgVipDbTabApi;
	


	@Override
	public boolean add(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException {
		// TODO Auto-generated method stub
		
		JdbcTemplate addtempt =null;
		try {
			addtempt = datanodes.getDataNodeByProduct(productName);
			
		}catch(ProductUnregisteredException pre) {
			logger.warn( "the product "+productName+ " no register" ,pre);
			
		}
		
		int result = 0;
		
	   if(addtempt != null) {

		boolean query = this.strOperator.existedComponent(productName, version, component, locale,addtempt);
		if (query) {
			result = strOperator.addStrs(productName, version, component, locale, messages,
					this.datanodes.getDataNodeByProduct(productName));

		} else {
			result = docOperate.saveDoc(productName, version, component, locale, messages,
					this.datanodes.getDataNodeByProduct(productName));
		}

	   }else {
		   
		  boolean registResult = pgVipDbTabApi.registerProduct(productName);
		  
		  if(registResult) {
			  result = docOperate.saveDoc(productName, version, component, locale, messages,
					  datanodes.getDataNodeByProduct(productName));
			  
		  }else {
		  String regErr ="auto register product error and product name is case insensitive";
			  logger.error(regErr);
			  throw new DataException(regErr);
		  }
		   
	   }
		
		
		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public ResultI18Message getBykeys(String productName, String version, String component, String locale,
			List<String> keys) throws DataException {
		// TODO Auto-generated method stub
		Map<String, String> result = strOperator.findByKeys(productName, version, component, locale, keys,
				this.datanodes.getDataNodeByProduct(productName));
		ResultI18Message msgs = new ResultI18Message(productName, version, component, locale);
		msgs.setMessages(result);

		return msgs;
	}

	@Override
	public String get2JsonStr(String productName, String version, String component, String locale, String key)
			throws DataException {
		// TODO Auto-generated method stub
		Map<String, String> result = strOperator.findByStrKey(productName, version, component, locale, key,
				this.datanodes.getDataNodeByProduct(productName));

		ObjectMapper objectMapper = new ObjectMapper();

		String strResult = null;
		try {
			strResult = objectMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.error("convert to json String error", e);
		}
		return strResult;
	}

	@Override
	public boolean update(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException {
		// TODO Auto-generated method stub
		int result = strOperator.addAndUpdateStrs(productName, version, component, locale, messages,
				this.datanodes.getDataNodeByProduct(productName));
		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(String productName, String version, String component, String locale, String key)
			throws DataException {
		// TODO Auto-generated method stub
		int result = strOperator.delStrBykey(productName, version, component, locale, key,
				this.datanodes.getDataNodeByProduct(productName));
		if (result > 0) {
			return true;
		}

		return false;
	}

}
