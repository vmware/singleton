/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.record.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import com.vmware.l10n.record.model.RecordModel;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
/**
 * 
 *
 * @author shihu
 *
 */
@Repository
public class SqlLiteDaoImpl implements SqlLiteDao {
	private static String Drivde = "org.sqlite.JDBC";
	private static String Db = "jdbc:sqlite:resource:sourceRecord.db";
	private final static String LOGERRSTR= "get the sqllite datasource or ResultSet error";
	private static Logger logger = LogManager.getLogger(SqlLiteDaoImpl.class);
	static {
		logger.info("begin init the SqlLite!!!!!");
		Connection connection = null;
		try {
			Class.forName(Drivde);
			connection = DriverManager.getConnection(Db);
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS source_record(product VARCHAR NOT NUll, version VARCHAR NOT NULL, component VARCHAR NOT NULL,  locale VARCHAR NOT NULL, modify_edition integer not null, synch_edition integer not null, CONSTRAINT source_record_pk PRIMARY KEY (product, version, component, locale))";
			statement.executeUpdate(sql);
			statement.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("end init the SqlLite!!!!!");
		}
	}

	private static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(Drivde);
			connection = DriverManager.getConnection(Db);
		} catch (ClassNotFoundException e) {
			logger.fatal(e.getMessage(), e);
			throw new NullPointerException("get the sqllite datasource error");
		} catch (SQLException e) {
			logger.fatal(e.getMessage(), e);
			throw new NullPointerException("get the sqllite datasource error");
		}
		return connection;
	}

	public int createSourceRecord(SingleComponentDTO dto) {
		// TODO Auto-generated method stub
		logger.info("begin create souce record!!!");
		String creatSql = "insert into source_record (product, version, component, locale, modify_edition, synch_edition) values(?, ?, ?, ?, 1, 0)";
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(creatSql);
			ps.setString(1, dto.getProductName());
			ps.setString(2, dto.getVersion());
			ps.setString(3, dto.getComponent());
			ps.setString(4, dto.getLocale());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			logger.warn(e.getMessage(), e);
			updateModifySourceRecord(dto);
		} catch (NullPointerException e) {
			logger.error(LOGERRSTR, e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public int updateModifySourceRecord(SingleComponentDTO dto) {
		// TODO Auto-generated method stub
		logger.info("begin update souce record!!!");
		String creatSql = "update source_record set modify_edition= modify_edition+1 where product=? and version=? and component=? and locale=?";
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(creatSql);
			ps.setString(1, dto.getProductName());
			ps.setString(2, dto.getVersion());
			ps.setString(3, dto.getComponent());
			ps.setString(4, dto.getLocale());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error(LOGERRSTR, e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}
	public int updateSynchSourceRecord(RecordModel dto) {
		// TODO Auto-generated method stub
		logger.info("begin synch souce record!!!");
		String creatSql = "update source_record set synch_edition= synch_edition+"+dto.getStatus()+" where product=? and version=? and component=? and locale=?";
	  logger.debug(creatSql);
		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(creatSql);
			ps.setString(1, dto.getProduct());
			ps.setString(2, dto.getVersion());
			ps.setString(3, dto.getComponent());
			ps.setString(4, dto.getLocale());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error(LOGERRSTR, e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public List<RecordModel> getChangedRecords() {
		logger.info("begin get change souce record!!!");
		String querySql = "select product, version, component, locale, modify_edition, synch_edition from source_record  where modify_edition<>synch_edition";
		List<RecordModel> list = new ArrayList<RecordModel>();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(querySql);
			ResultSet resultSet = null;
			try {
				resultSet = ps.executeQuery();
			    while(resultSet.next()) {
					RecordModel model = new RecordModel();
					model.setProduct(resultSet.getString(1));
					model.setVersion(resultSet.getString(2));
					model.setComponent(resultSet.getString(3));
					model.setLocale(resultSet.getString(4));
					int status = resultSet.getInt(5)- resultSet.getInt(6);
					model.setStatus((long)status);
					list.add(model);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error(LOGERRSTR, e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}
}
