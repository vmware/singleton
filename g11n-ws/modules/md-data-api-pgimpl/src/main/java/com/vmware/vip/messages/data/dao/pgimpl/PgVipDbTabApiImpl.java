/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vmware.vip.messages.data.dao.api.IPgVipDbTabApi;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.pgimpl.balance.PgDataNodeBalancerAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.config.JdbcTemplateAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.exception.ProductUnregisteredException;
import com.vmware.vip.messages.data.dao.pgimpl.model.VipProductConfig;
import com.vmware.vip.messages.data.dao.pgimpl.operate.ITabOperate;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */

@Service
public class PgVipDbTabApiImpl implements IPgVipDbTabApi {

	private static Logger logger = LoggerFactory.getLogger(PgStrApiImpl.class);
	
	@Autowired
	private ITabOperate tabOperate;

	@Autowired
	private PgDataNodeBalancerAdapter pgDatanodes;

	@Autowired
	private JdbcTemplateAdapter dataNodeDBS;

	@Override
	public boolean registerProduct(String product) {
		// TODO Auto-generated method stub

		if (!tabOperate.isExistedProduct(product)) {
			Map<String, JdbcTemplate> datanodes = dataNodeDBS.getJdbctemplatemap();

			Map<String, Integer> countMap = new HashMap<String, Integer>();
			for (Entry<String, JdbcTemplate> entry : datanodes.entrySet()) {
				countMap.put(entry.getKey(), 0);
			}

			countMap = tabOperate.aggrByDataSource(countMap);

			List<Map.Entry<String, Integer>> list = new ArrayList<>();
			list.addAll(countMap.entrySet());

			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					// TODO Auto-generated method stub
					return o1.getValue() - o2.getValue();
				}

			});

			String datasourcName = list.get(0).getKey();
			JdbcTemplate jdbc = datanodes.get(datasourcName);

			this.tabOperate.createProductTab(product, jdbc);

			this.tabOperate.addProduct(new VipProductConfig(product, datasourcName));

			pgDatanodes.addProduct2DataNode(product, jdbc);
			return true;
		}

		return false;
	}

	@Override
	public boolean refreshDataNodes() {

		try {
			pgDatanodes.refreshDataNodes();

		} catch (Exception e) {
			logger.error("refresh data nodes error", e);
			return false;
		}

		return true;
	}

	@Override
	public boolean delProduct(String product) throws DataException {
		// TODO Auto-generated method stub
		if (tabOperate.isExistedProduct(product)) {
			tabOperate.delProduct(product);
			tabOperate.delProductTab(product, pgDatanodes.getDataNodeByProduct(product));
			pgDatanodes.removeProduct(product);
			return true;
		}
		return false;
	}

	@Override
	public boolean productIsRegistered(String product) {
		// TODO Auto-generated method stub
		return tabOperate.isExistedProduct(product);
	}

	@Override
	public boolean clearProductData(String product, String version) {
		// TODO Auto-generated method stub
		try {
			int result = tabOperate.clearProductData(product, version, pgDatanodes.getDataNodeByProduct(product));
		   if(result>0) {
			   return true;
			   
		   }else {
			   return false;
		   }
		
		
		} catch (ProductUnregisteredException e) {
			// TODO Auto-generated catch block
			logger.warn("prouct-- "+ product +"-- can't register", e);
		}
		
		
		
		return false;
	}

}
