/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.messages.data.dao.api.IMultComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;
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
public class PgMultCompApiImpl implements IMultComponentDao {
    private static Logger logger = LoggerFactory.getLogger(PgMultCompApiImpl.class);
	@Autowired
	private PgDataNodeBalancerAdapter datanodes;

	@Autowired
	private IDocOperate docOperate;

	@Override
	public List<String> get2JsonStrs(String productName, String version, List<String> components, List<String> locales)
			throws DataException {
		// TODO Auto-generated method stub
		List<String> results = new ArrayList<String>();
		for (String comp : components) {
			for (String locale : locales) {
				String result = docOperate.findByDocId(productName, version, comp, locale,
						datanodes.getDataNodeByProduct(productName));;

				if (result != null) {
					results.add(result);
				}else {
					String warnMsg = String.format("%s--%s--%s---%s-- query no data in DB",productName, version, comp, locale);
					logger.warn(warnMsg);
				}
			}
		}
		
		
	if(results.size()==0) {
		throw new DataException("this no component in DB return json");
	}

		return results;
	}

	@Override
	public List<ResultI18Message> get(String productName, String version, List<String> components, List<String> locales)
			throws DataException {
		// TODO Auto-generated method stub
		List<ResultI18Message> results = new ArrayList<ResultI18Message>();
		List<String> list = get2JsonStrs(productName, version, components, locales);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		
		for(String result: list) {
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
			
			results.add(rsmsg);
			
		}
	
		}
		
		
	if(results.size()==0) {
		throw new DataException("this no components in DB when return ResultI18Message Object");
	}

		return results;

	}

}
