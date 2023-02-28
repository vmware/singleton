/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
 package com.vmware.vip.messages.data.dao.pgimpl.operate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.messages.data.dao.pgimpl.model.I18nString;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IStrOperate;
/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
@Repository
public class StrOperateImpl implements IStrOperate {
	private static Logger logger = LoggerFactory.getLogger(StrOperateImpl.class);
	
	@Override
	public int addStrs(I18nString n18Str, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String sqlhead =  "update vip_msg set messages = ";   
		String sqltail= " where product = ? and version = ? and component= ? and locale = ?"; 
		int i=0;
		String updateStr =null;
		for(Entry<String, String> entry: n18Str.getMessages().entrySet()) {
			if(i<1) { 
				updateStr = "jsonb_set(messages, '{"+entry.getKey()+"}', '\""+entry.getValue()+"\"'::jsonb,true)";
			}else {
				updateStr = "jsonb_set("+updateStr+", '{"+entry.getKey()+"}', '\""+entry.getValue()+"\"'::jsonb,true)";
			}
			
			i++;
			
		}


		String sql = sqlhead + updateStr + sqltail;
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		int result = jdbcTemplate.update(sql, n18Str.getProduct(), n18Str.getVersion(), n18Str.getComponent(),n18Str.getLocale());
		return result;
	}
	
	@Override
	public int addStrs(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String sqlhead =  "update vip_msg set messages = ";   
		String sqltail= " where product = ? and version = ? and component= ? and locale = ?"; 
		int i=0;
		String updateStr =null;
		for(Entry<String, String> entry: messages.entrySet()) {
			if(i<1) { 
				updateStr = "jsonb_set(messages, '{"+entry.getKey()+"}', '\""+entry.getValue()+"\"'::jsonb,true)";
			}else {
				updateStr = "jsonb_set("+updateStr+", '{"+entry.getKey()+"}', '\""+entry.getValue()+"\"'::jsonb,true)";
			}
			
			i++;
			
		}


		String sql = sqlhead + updateStr + sqltail;
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		logger.debug(String.join(", ", productName, version, component, locale));
		int result = jdbcTemplate.update(sql, productName, version, component,locale);
		return result;
	}
	
	
	
	
	

	@Override
	public int addAndUpdateStrs(I18nString strs, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		return addStrs(strs,jdbcTemplate);
	}
	@Override
	public int addAndUpdateStrs(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		return addStrs(productName, version, component, locale,messages, jdbcTemplate);
	}

	
	
	
	
	
	
	
	@Override
	public  Map<String, String> findAllStr(I18nString strs, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String queryByKeySql=" select v.messages from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(queryByKeySql);
		String[] params = {strs.getProduct(), strs.getVersion(),  strs.getComponent(), strs.getLocale()};
		logger.debug(String.join(", ", params));
		String resultjson = null;
	 try {
		 resultjson =  jdbcTemplate.queryForObject(queryByKeySql, params, String.class);
		 }catch(EmptyResultDataAccessException empty) {
			 
			 logger.error(empty.getMessage(),empty);
		 }
		
	 if(resultjson != null) {
		 
		 logger.debug("result:"+resultjson);
		 
		 ObjectMapper objectMapper = new ObjectMapper();  
			Map<String, String> map = null;
			
				try {
					map = objectMapper.readValue(resultjson, new TypeReference<Map<String, String>>() {});
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
		
		 return map;
		 
	 }else {
		return null;
	 }
	
		 
	}

	@Override
	public Map<String, String> findAllStr(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String queryByKeySql=" select v.messages from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(queryByKeySql);
		String[] params = {productName, version,  component, locale};
		logger.debug(String.join(", ", params));
		String resultjson = null;
	 try {
		 resultjson =  jdbcTemplate.queryForObject(queryByKeySql, params, String.class);
		 }catch(EmptyResultDataAccessException empty) {
			 
			 logger.error(empty.getMessage(), empty);
		 }
		
	 if(resultjson != null) {
		 
		 logger.debug("result:"+resultjson);
		 
		 ObjectMapper objectMapper = new ObjectMapper();  
			Map<String, String> map = null;
			
				try {
					map = objectMapper.readValue(resultjson, new TypeReference<Map<String, String>>() {});
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
		
		 return map;
		 
	 }else {
		return null;
	 }
	}
	
	
	
	
	
	
	
	
	@Override
	public int delStrBykey(I18nString n18Str, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		String sql=  "update vip_msg  set messages = messages #- '{"+n18Str.getKeys().get(0)+"}'::text[] where product = ? and version = ? and component= ? and locale = ?";   
		logger.debug(sql);
		logger.debug(String.join(", ",  n18Str.getProduct(), n18Str.getVersion(), n18Str.getComponent(),
				n18Str.getLocale()));
		 return  jdbcTemplate.update(sql, n18Str.getProduct(), n18Str.getVersion(), n18Str.getComponent(),n18Str.getLocale());
	}

	
	@Override
	public int delStrBykey(String productName, String version, String component, String locale, String key,
			JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		
		String sql=  String.format("update vip_msg  set messages = messages #- '{%s}'::text[] where product = ? and version = ? and component= ? and locale = ?",key);   
		logger.debug(sql);
		logger.debug(String.join(", ", productName, version, component, locale));
		 return  jdbcTemplate.update(sql, productName, version, component,locale);
		
	}
	
	
	
	
	
	
	@Override
	public Map<String, String> findByStrKey(I18nString strs, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String queryByKeySql=" select (v.messages->>'"+strs.getKeys().get(0)+"') as messages from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(queryByKeySql);
		
		String[] params ={strs.getProduct(), strs.getVersion(),  strs.getComponent(), strs.getLocale()};
		logger.debug(String.join(", ", params));
		String resultVal = null;
		
		 try {
			 resultVal = jdbcTemplate.queryForObject(queryByKeySql, params, String.class);
			 }catch(EmptyResultDataAccessException empty) {
				 
				 logger.error(empty.getMessage(), empty);
			 }
		
		
		 
		 Map<String,String> map = new HashMap<>();
		 if(resultVal !=null) {
			 logger.debug("result:"+resultVal);
			 map.put(strs.getKeys().get(0), resultVal);
		 }else {
			 map.put(strs.getKeys().get(0), null);
		 }
		 return map;
		
	}

	




	@Override
	public Map<String, String> findByStrKey(String productName, String version, String component, String locale,
			String key, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
	
		String queryByKeySql=String.format(" select (v.messages->>'%s') as messages from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?",key); 
		
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(queryByKeySql);
		
		String[] params ={productName, version,  component, locale};
		logger.debug(String.join(", ", params));
		String resultVal = null;
		
		 try {
			 resultVal = jdbcTemplate.queryForObject(queryByKeySql, params, String.class);
			 }catch(EmptyResultDataAccessException empty) {
				 
				 logger.error(empty.getMessage(), empty);
			 }
		
		
		 
		 Map<String,String> map = new HashMap<>();
		 if(resultVal !=null) {
			 logger.debug("result:"+resultVal);
			 map.put(key, resultVal);
		 }else {
			 logger.debug("the null key:"+key);
			 map.put(key, null);
		 }
		 return map;
	}

	@Override
	public Map<String, String> findByKeys(String productName, String version, String component, String locale,
			List<String> keys, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		 Map<String,String> map = new HashMap<>();
		 
		 for(String key: keys) {
		String queryByKeySql=" select (v.messages->>'"+key+"') as messages from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource)(jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(queryByKeySql);
		
		String[] params ={productName, version,  component, locale};
		logger.debug(String.join(", ", params));
		String resultVal = null;
		
		 try {
			 resultVal = jdbcTemplate.queryForObject(queryByKeySql, params, String.class);
			 }catch(EmptyResultDataAccessException empty) {
				 
				 logger.error(empty.getMessage(), empty);
			 }
		
		
		 if(resultVal !=null) {
			 logger.debug("result:"+resultVal);
			 map.put(key, resultVal);
		 }else {
			 map.put(key, null);
		 }
		
		 }
		
		
		return map;
	}

	@Override
	public boolean existedComponent(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String sql = "select count(v.id) from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		String[] params = { productName, version, component, locale };
		logger.debug(String.join(", ", params));
		int resultcount = 0;

		try {
			resultcount = jdbcTemplate.queryForObject(sql, params, Integer.class);

		} catch (EmptyResultDataAccessException empty) {
			
			logger.error(empty.getMessage(), empty);
		}

		
		if(resultcount >0) {
			return true;
		}else {
			return false;
		}
	}
	


}
