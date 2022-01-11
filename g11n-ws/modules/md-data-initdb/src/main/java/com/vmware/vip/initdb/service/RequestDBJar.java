/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vmware.vip.initdb.model.DBI18nDocument;
import com.vmware.vip.initdb.model.DbResponseStatus;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.api.IPgVipDbTabApi;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * 
 *
 * @author shihu
 *
 */
@Component
public class RequestDBJar {

	@Autowired
	private IPgVipDbTabApi vipDbtab;

	@Autowired
	private IOneComponentDao compDao;
    private static String resultStr="done failure";

	private static Logger logger = LoggerFactory.getLogger(RequestDBJar.class);

	public boolean checkProductAndAdd(String productName) {

		logger.debug("do check product and add");

		boolean result = vipDbtab.productIsRegistered(productName);

		if (result) {
			vipDbtab.clearProductData(productName, null);
			return true;
		} else {
			result = false;
			result = vipDbtab.registerProduct(productName);

			if (result) {
				return true;
			} else {
				return false;
			}

		}

	}

	public DbResponseStatus AddDoc2DB(DBI18nDocument dbDoc) {

		logger.debug("do add doc to db!!!");

		DbResponseStatus dbstatus = new DbResponseStatus();
		try {
			boolean result = compDao.update(dbDoc.getProduct(), dbDoc.getVersion(), dbDoc.getComponent(),
					dbDoc.getLocale(), dbDoc.getMessages());
			if (result) {
				dbstatus.setCode(0);
				dbstatus.setStatus("done successfully");
			} 

		} catch (DataException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);

		}
		dbstatus.setCode(1);
		dbstatus.setStatus(resultStr);
		logger.debug("end add doc to db!!!\n");
		return dbstatus;

	}

	public DbResponseStatus DelDocAndAdd2DB(DBI18nDocument dbDoc) {
		// TODO Auto-generated method stub
		logger.debug("do clear and add doc to db!!!");

		DbResponseStatus dbstatus = new DbResponseStatus();
		try {
			 compDao.delete(dbDoc.getProduct(), dbDoc.getVersion(), dbDoc.getComponent(),
					dbDoc.getLocale());
			boolean result = compDao.update(dbDoc.getProduct(), dbDoc.getVersion(), dbDoc.getComponent(),
					dbDoc.getLocale(), dbDoc.getMessages());
			if (result) {
				dbstatus.setCode(0);
				dbstatus.setStatus("clear and add doc done successfully");
			}
		} catch (DataException e) {
			// TODO Auto-generated catch block
			logger.warn(e.getMessage(), e);
		}
		dbstatus.setCode(1);
		dbstatus.setStatus("clear and add doc done failure" );
		logger.debug("end clear and add doc to db!!!\n");
		return dbstatus;
	}

}
