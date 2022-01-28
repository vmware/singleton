/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.operate.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.vmware.vip.messages.data.dao.pgimpl.model.VipProductConfig;
import com.vmware.vip.messages.data.dao.pgimpl.operate.ITabOperate;
@Repository
public class TabOperateImpl implements ITabOperate{
	
	@Autowired
	private JdbcTemplate configJdbcTemplate;
	private static Logger logger = LoggerFactory.getLogger(TabOperateImpl.class);

	@Override
	public List<VipProductConfig> getAllProductConfig() {
		// TODO Auto-generated method stub
		String sql ="select id, product, datasource, status, created_userid, crt_time from vip_product where status=0";
		logger.debug(sql);
		List<VipProductConfig> result = this.configJdbcTemplate.query(sql, new VipProductConfigRowMapper());
		return result;
	}

	

	public Map<String, Integer> aggrByDataSource(Map<String, Integer> map) {
		// TODO Auto-generated method stub
		String sql = "select t.datasource, count(t.datasource) numb from vip_product t where t.status =0 group by t.datasource";
		
		List<VipProductConfig> result = this.configJdbcTemplate.query(sql, new VipDatasourceCountRowMapper());
		
		for(VipProductConfig obj : result) {
			map.put(obj.getDatasource(), obj.getDatasourceCount());
		}
		
		return map;
	}
	
	

	class VipDatasourceCountRowMapper implements RowMapper<VipProductConfig> {

		@Override
		public VipProductConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			VipProductConfig vp = new VipProductConfig();
			vp.setDatasource(rs.getString("datasource"));
			vp.setDatasourceCount(rs.getInt("numb"));
			return vp;
		}
		
	}
	
	class VipProductConfigRowMapper implements RowMapper<VipProductConfig> {

		@Override
		public VipProductConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			VipProductConfig vp = new VipProductConfig();
			vp.setId(rs.getLong("id"));
			vp.setProduct(rs.getString("product"));
		    vp.setDatasource(rs.getString("datasource"));
			vp.setStatus(rs.getInt("status"));
			vp.setCrt_time(rs.getDate("crt_time"));
			
			return vp;
		}

	
	}



    public boolean isExistedProduct(String productName) {
	String sql = "select count(t.product) from vip_product t where lower(t.product)=?";
		String[] params = {productName.toLowerCase()};
		Integer result = this.configJdbcTemplate.queryForObject(sql, params, Integer.class);
	    if(result >0) {
	    	return true;
	    }
    	
		return false;
    }

	@Override
	public boolean createProductTab(String product, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String createTabSql="CREATE TABLE vip_msg_{ProductName} PARTITION OF vip_msg FOR VALUES IN ('{ProductName}');";
		
		
		String createTabSeqSql="create sequence vip_msg_{ProductName}_seq increment by 1 minvalue 1 no maxvalue start with 1;";
		
		String addTabIdSql ="alter table vip_msg_{ProductName} alter column id set default nextval('vip_msg_{ProductName}_seq')";
		String addPmkeySql = "alter table vip_msg_{ProductName} add primary key(id)";
		String addunKeySql = "alter table vip_msg_{ProductName} add constraint uk_{ProductName} unique (product,version,component,locale)";
		
		logger.debug(createTabSql.replaceAll("\\{ProductName\\}", product));
		jdbcTemplate.update(createTabSql.replaceAll("\\{ProductName\\}", product));
		logger.debug(createTabSeqSql.replaceAll("\\{ProductName\\}", product));
		jdbcTemplate.update(createTabSeqSql.replaceAll("\\{ProductName\\}", product));
		logger.debug(addTabIdSql.replaceAll("\\{ProductName\\}", product));
		jdbcTemplate.update(addTabIdSql.replaceAll("\\{ProductName\\}", product));
		logger.debug(addPmkeySql.replaceAll("\\{ProductName\\}", product));
		jdbcTemplate.update(addPmkeySql.replaceAll("\\{ProductName\\}", product));
		logger.debug(addunKeySql.replaceAll("\\{ProductName\\}", product));
		jdbcTemplate.update(addunKeySql.replaceAll("\\{ProductName\\}", product));
		return true;
	}



	@Override
	public int addProduct(VipProductConfig config) {
		// TODO Auto-generated method stub
		String insertSql = "INSERT INTO vip_product(product, datasource, crt_time) VALUES ( ?, ?, CURRENT_TIMESTAMP)";
		logger.debug(insertSql);
		return this.configJdbcTemplate.update(insertSql, config.getProduct(), config.getDatasource());
	}



	@Override
	public int delProduct(String product) {
		// TODO Auto-generated method stub
		String delSql = "delete from vip_product where product = ?";
		logger.debug(delSql);
		return this.configJdbcTemplate.update(delSql, product);
		
	}



	@Override
	public void delProductTab(String product, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		
		String  deltabSql = String.format("drop table  vip_msg_%s",product);
		logger.debug(deltabSql);
		jdbcTemplate.update(deltabSql);
		String  delSeqSql=String.format("drop sequence vip_msg_%s_seq",product);
		logger.debug(delSeqSql);
		jdbcTemplate.update(delSeqSql);
	}



	@Override
	public int clearProductData(String productName, String version, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		
		if(version == null) {
			String delProductSql = String.format("delete from vip_msg_%s where product=?", productName);
			return jdbcTemplate.update(delProductSql, productName);
		}else {
			String delVersionSql = String.format("delete from vip_msg_%s where product=? and version=?", productName);
			
			return jdbcTemplate.update(delVersionSql, productName, version);
		}
		
		
		
	}



}
