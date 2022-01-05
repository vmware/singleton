/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.operate.impl;

import java.io.IOException;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.messages.data.dao.pgimpl.model.I18nDocument;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IDocOperate;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */

@Repository
public class DocOperateImpl implements IDocOperate {

	private static Logger logger = LoggerFactory.getLogger(DocOperateImpl.class);

	@Override
	public I18nDocument findByDocId(I18nDocument doc, JdbcTemplate jdbcTemplate) {
		String sql = "select v.messages::text from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		String[] params = { doc.getProduct(), doc.getVersion(), doc.getComponent(), doc.getLocale() };
		logger.debug(String.join(", ", params));
		String resultjson = null;

		try {
			resultjson = jdbcTemplate.queryForObject(sql, params, String.class);

		} catch (EmptyResultDataAccessException empty) {

			logger.error(empty.getMessage(), empty);
		}

		if (resultjson != null) {

			logger.debug("result:" + resultjson);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = null;

			try {
				map = objectMapper.readValue(resultjson, new TypeReference<Map<String, String>>() {
				});
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

			doc.setMessages(map);
		} else {
			doc.setMessages(null);
		}

		return doc;
	}

	@Override
	public String findByDocId(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String sql = "select v.messages::text from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		String[] params = { productName, version, component, locale };
		logger.debug(String.join(", ", params));
		String resultjson = null;

		try {
			resultjson = jdbcTemplate.queryForObject(sql, params, String.class);

		} catch (EmptyResultDataAccessException empty) {

			logger.error(empty.getMessage(), empty);
		}

		if (resultjson != null) {
			resultjson = "{ \"component\" : \"" + component + "\", \"messages\" : " + resultjson + ", \"locale\" : \""
					+ locale + "\" }";
		}
		return resultjson;
	}

	@Override
	public int saveDoc(I18nDocument content, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String tableName = "vip_msg_" + content.getProduct().toLowerCase();
		String saveSql = "insert into " + tableName + " (product, version, component, locale, messages, crt_time) values (? ,? ,? ,? ,?::jsonb, CURRENT_TIMESTAMP)";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(saveSql);
		logger.debug(String.join(", ", content.getProduct(), content.getVersion(), content.getComponent(),
				content.getLocale()));
		String msg = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			msg = objectMapper.writeValueAsString(content.getMessages());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block

			logger.error(e.getMessage(), e);
		}
		return jdbcTemplate.update(saveSql, content.getProduct(), content.getVersion(), content.getComponent(),
				content.getLocale(), msg);

	}

	@Override
	public int saveDoc(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String tableName = "vip_msg_" + productName.toLowerCase();
		String saveSql = "insert into " + tableName + " (product, version, component, locale, messages, crt_time) values (? ,? ,? ,? ,?::jsonb, CURRENT_TIMESTAMP)";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(saveSql);

		logger.debug(String.join(", ", productName, version, component, locale));
		String msg = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			msg = objectMapper.writeValueAsString(messages);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block

			logger.error(e.getMessage(), e);
		}
		return jdbcTemplate.update(saveSql, productName, version, component, locale, msg);
	}

	@Override
	public int removeDoc(I18nDocument doc, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String delSql = "delete from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(delSql);
		logger.debug(String.join(", ", doc.getProduct(), doc.getVersion(), doc.getComponent(), doc.getLocale()));
		return jdbcTemplate.update(delSql, doc.getProduct(), doc.getVersion(), doc.getComponent(), doc.getLocale());

	}

	@Override
	public int removeDoc(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String delSql = "delete from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(delSql);
		logger.debug(String.join(", ", productName, version, component, locale));
		return jdbcTemplate.update(delSql, productName, version, component, locale);
	}

	private Map<String, String> findByDoc2Map(String productName, String version, String component, String locale,
			JdbcTemplate jdbcTemplate) {

		// TODO Auto-generated method stub
		String sql = "select v.messages::text from vip_msg v where v.product = ? and v.version = ? and v.component= ? and v.locale = ?";

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(sql);
		String[] params = { productName, version, component, locale };
		logger.debug(String.join(", ", params));
		String resultjson = null;

		try {
			resultjson = jdbcTemplate.queryForObject(sql, params, String.class);

		} catch (EmptyResultDataAccessException empty) {

			logger.error(empty.getMessage());
		}

		logger.debug("db json:" + resultjson);

		if (resultjson == null) {
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> map = null;

		try {
			map = objectMapper.readValue(resultjson, new TypeReference<Map<String, String>>() {
			});
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
	}

	@Override
	public int updateDoc(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) {

		int result = 0;
		Map<String, String> map = findByDoc2Map(productName, version, component, locale, jdbcTemplate);

		if (map != null) {
			if (messages != null) {
				for (Entry<String, String> entry : messages.entrySet()) {
					map.put(entry.getKey(), entry.getValue());
				}
				result = updateCompDoc(productName, version, component, locale, map, jdbcTemplate);
			}

		} else {

			result = updateCompDoc(productName, version, component, locale, messages, jdbcTemplate);

		}

		return result;

	}

	private int updateCompDoc(String productName, String version, String component, String locale,
			Map<String, String> messages, JdbcTemplate jdbcTemplate) { // TODO Auto-generated method stub
		String updateSql = "update vip_msg  set messages = ? ::jsonb  where product = ? and version = ? and component= ? and locale = ?";
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(updateSql);
		logger.debug(String.join(", ", productName, version, component, locale));

		String msg = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			msg = objectMapper.writeValueAsString(messages);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block e.printStackTrace();
			logger.warn(e.getMessage(), e);
			return 0;
		}
		logger.debug("update json---" + msg);
		return jdbcTemplate.update(updateSql, msg, productName, version, component, locale);
	}

	@Override
	public List<String> getComponentList(String productName, String version, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub

		String compSql = "select v.component from vip_msg v where v.product = ? and v.version = ? group by v.component";

		String[] params = { productName, version };

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(compSql);
		logger.debug(String.join(", ", params));
		List<String> result = jdbcTemplate.queryForList(compSql, params, String.class);

		return result;
	}

	@Override
	public List<String> getLocaleList(String productName, String version, JdbcTemplate jdbcTemplate) {

		String localeSql = "select v.locale from vip_msg v where v.product = ? and v.version = ? group by v.locale";
		String[] params = { productName, version };

		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(localeSql);
		logger.debug(String.join(", ", params));
		List<String> result = jdbcTemplate.queryForList(localeSql, params, String.class);

		return result;
	}

    /* (non-Javadoc) * @see com.vmware.vip.messages.data.dao.pgimpl.operate.IDocOperate#getVersionList(java.lang.String, org.springframework.jdbc.core.JdbcTemplate) */
	@Override
	public List<String> getVersionList(String productName, JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		String localeSql = "select v.version from vip_msg v where v.product = ? group by v.version";
		String[] params = { productName };
		if (jdbcTemplate.getDataSource() != null) {
			logger.debug(((DruidDataSource) (jdbcTemplate.getDataSource())).getName());
		}
		logger.debug(localeSql);
		logger.debug(String.join(", ", params));
		List<String> result = jdbcTemplate.queryForList(localeSql, params, String.class);
		return result;
	}

}
