/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.csp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.vmware.vip.core.login.AuthModel;
/**
 * 
 *
 * @author shihu
 *
 */
@Service
public class SqlLiteService {

	private static String Drivde = "org.sqlite.JDBC";
	private static String Db = "jdbc:sqlite:resource:vipauth.db";
	private static Logger logger = LogManager.getLogger(SqlLiteService.class);

	static {

		logger.info("begin init the SqlLite!!!!!");
		Connection connection = null;
		Statement statement = null;
		try {
			Class.forName(Drivde);
			connection = DriverManager.getConnection(Db);
			statement = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS vip_auth(username  VARCHAR NOT NUll, pubkey VARCHAR,  refreshkey VARCHAR, verifykey VARCHAR, CONSTRAINT vip_auth_pk PRIMARY KEY (username))";
			statement.executeUpdate(sql);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
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
			// TODO Auto-generated catch block
			
			logger.fatal(e.getMessage(), e);
			throw new NullPointerException("get the sqllite datasource error");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			logger.fatal(e.getMessage(), e);
			throw new NullPointerException("get the sqllite datasource error");
		}
		
		return connection;
	}

	public int createAuth(String username) {
		// TODO Auto-generated method stub

		String creatSql = "insert into vip_auth (username) values(?)";
		Connection conn =null;

		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(creatSql);
			ps.setString(1, username);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error("get the sqllite datasource error ", e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
				logger.error(e.getMessage(), e);
			}

		}

		return result;

	}

	public int addPubkey(AuthModel auth) {
		String updateSql = "update vip_auth set pubkey = ? where username=?";
		Connection conn =null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(updateSql);
			ps.setString(1, auth.getPubkey());
			ps.setString(2, auth.getUsername());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			logger.error(e.getMessage(), e);
		}catch (NullPointerException e) {
			logger.error("get the sqllite datasource error ", e);
		}  finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}

		}

		return result;

	}

	public int addVerfyKey(AuthModel auth) {
		String addSql = "update vip_auth set verifykey = ? where username= ?";

		Connection conn = null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(addSql);
			ps.setString(1, auth.getVerifyKey());
			ps.setString(2, auth.getUsername());
			result = ps.executeUpdate();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error("get the sqllite datasource or ResultSet error ", e);
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
				logger.error(e.getMessage(), e);
			}

		}

		return result;
	}

	public int addRefreshkey(AuthModel auth) {
		String addSql = "update vip_auth  set refreshkey = ? where username=?";

		Connection conn =null;
		PreparedStatement ps = null;
		int result = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(addSql);
			ps.setString(1, auth.getRefreshKey());
			ps.setString(2, auth.getUsername());
			result = ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			logger.error("get the sqllite datasource or ResultSet error ", e);
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
				logger.error(e.getMessage(), e);
			}

		}

		return result;
	}

	public AuthModel getAuth(String username) {
		String querySql = "select username, pubkey, refreshkey, verifykey from vip_auth  where username=?";
		AuthModel am = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(querySql);

			ps.setString(1, username);
			ResultSet resultSet=null;
			try {	
				resultSet=  ps.executeQuery();

			if (resultSet.next()) {
				am = new AuthModel();
				am.setUsername(resultSet.getString(1));
				am.setPubkey(resultSet.getString(2));
				am.setRefreshKey(resultSet.getString(3));
				am.setVerifyKey(resultSet.getString(4));
			}
			}finally{
				if(resultSet != null) {
					resultSet.close();
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
	
			logger.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.error("get the sqllite datasource or ResultSet error ", e);
		} finally {
			try {
				
				if(ps != null) {
					ps.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
			
				logger.error(e.getMessage(), e);
			}

		}

		return am;
	}

}
