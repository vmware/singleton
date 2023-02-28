/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.api.IPgVipDbTabApi;
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
public class PgOneComponentApiImpl implements IOneComponentDao {
    private static Logger logger = LoggerFactory.getLogger(PgOneComponentApiImpl.class);
	@Autowired
	private PgDataNodeBalancerAdapter datanodes;

	@Autowired
	private IDocOperate docOperate;

	@Autowired
	private IStrOperate strOperator;
	
	@Autowired
	private IPgVipDbTabApi pgVipDbTabApi;

	@Override
	public ResultI18Message get(String productName, String version, String component, String locale)
			throws DataException {
		// TODO Auto-generated method stub

		String result = docOperate.findByDocId(productName, version, component, locale,
				datanodes.getDataNodeByProduct(productName));
		if(result == null) {
			String warnInfo = String.format("%s--%s--%s---%s-- query no data in DB",productName, version, component, locale);
			logger.warn(warnInfo);
			throw new DataException("this no component in DB");
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ResultI18Message rsmsg = null;
		try {
			rsmsg = objectMapper.readValue(result, ResultI18Message.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			
           logger.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		
			logger.error(e.getMessage(), e);
		}
		
		if(rsmsg != null) {
			rsmsg.setProduct(productName);
			rsmsg.setVersion(version);
			rsmsg.setComponent(component);
			rsmsg.setLocale(locale);
		}

		return rsmsg;
	}

	@Override
	public String get2JsonStr(String productName, String version, String component, String locale)
			throws DataException {
		// TODO Auto-generated method stub
		String result = docOperate.findByDocId(productName, version, component, locale,
				datanodes.getDataNodeByProduct(productName));
		if(result == null) {
			String warnInfo = String.format("%s--%s--%s---%s-- query no data in DB",productName, version, component, locale);
			logger.warn(warnInfo);
			throw new DataException("this no component in DB by get json String");
		}
		return result;
		
	}

	@Override
	public boolean add(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException {
		// TODO Auto-generated method stub
		JdbcTemplate addtempt =null;
		try {
			addtempt = datanodes.getDataNodeByProduct(productName);
			
		}catch(ProductUnregisteredException pre) {
			String warnInfo = String.format("the product %s no register, and next run auto register",productName) ;
			logger.warn(warnInfo,pre);
		}
		
	   int result =0;
		
		if(addtempt != null) {
			result = docOperate.saveDoc(productName, version, component, locale, messages,
					addtempt);
		}else {
			logger.info("begin to auto register product: "+productName);
			boolean registResult = pgVipDbTabApi.registerProduct(productName);
			logger.info("end auto register product: "+productName);
			
			 if(registResult) {
				  result = docOperate.saveDoc(productName, version, component, locale, messages,
						  datanodes.getDataNodeByProduct(productName));
				  
			  }else {
				  logger.error("auto register product error or repetition, product name is case insensitive");
				  throw new DataException("auto register product error or repetition,  notice:  product name is case insensitive");
			  }
		}
		

		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(String productName, String version, String component, String locale,
			Map<String, String> messages) throws DataException {
		// TODO Auto-generated method stub

		
		
		JdbcTemplate jdbctmp =null;
		try {
			jdbctmp = datanodes.getDataNodeByProduct(productName);
			
		}catch(ProductUnregisteredException pre) {
			logger.warn( "the product "+productName+ " no register" ,pre);
			
		}
		
		int result = 0;
		
		if(jdbctmp != null) {
			boolean query = strOperator.existedComponent(productName, version, component, locale,
					datanodes.getDataNodeByProduct(productName));
			
			
			if (query) {
				result = docOperate.updateDoc(productName, version, component, locale, messages,
						datanodes.getDataNodeByProduct(productName));
			} else {
				result = docOperate.saveDoc(productName, version, component, locale, messages,
						datanodes.getDataNodeByProduct(productName));
			}
			
		}else {
			logger.info("begin to auto register product: "+productName);
			boolean registResult = pgVipDbTabApi.registerProduct(productName);
			logger.info("end auto register product: "+productName);
			if(registResult) {
				  result = docOperate.saveDoc(productName, version, component, locale, messages,
						  datanodes.getDataNodeByProduct(productName));
				  
			  }else {
				  throw new DataException("auto register product error and product name is case insensitive");
			  }
			
		}
		

		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(String productName, String version, String component, String locale) throws DataException {
		// TODO Auto-generated method stub
		int result = docOperate.removeDoc(productName, version, component, locale,
				datanodes.getDataNodeByProduct(productName));

		if (result > 0) {
			return true;
		}
		return false;
	}
	
	


}
