/**
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.gcs.impl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.messages.data.dao.api.IMultComponentDao;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultI18Message;

/**
 * this class get the bundle json from bundle file
 */
@Repository
@Profile("gcs")
public class GcsMultComponentDaoImpl implements IMultComponentDao {
    private static Logger logger = LoggerFactory.getLogger(GcsMultComponentDaoImpl.class);

    @Autowired
    private IOneComponentDao oneComponentDao;

    /**
     * get the bundle files from gcs service
     */
    @Override
    public List<String> get2JsonStrs(String productName, String version, List<String> components, List<String> locales)
            throws DataException {
        logger.debug("begin get2JsonStrs");
        List<String> bundles = new ArrayList<>();
        if (components == null || locales == null) {
            throw new DataException("Gcs No component or locale");
        }
        for (String component : components) {
            for (String locale : locales) {
                try {
                    bundles.add(oneComponentDao.get2JsonStr(productName, version, component, locale));
                } catch (DataException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        logger.debug("end get2JsonStrs");
        return bundles;
    }

    /**
     * get the bundle files and convert to ResultI18Message
     */
    @Override
    public List<ResultI18Message> get(String productName, String version, List<String> components, List<String> locales)
            throws DataException {
        logger.debug("begin get");
        List<ResultI18Message> bundles = new ArrayList<>();
        if (components == null || locales == null) {
            throw new DataException("Gcs No component or locale");
        }
        for (String component : components) {
            for (String locale : locales) {
                try {
                    bundles.add(oneComponentDao.get(productName, version, component, locale));
                } catch (DataException e) {
                    throw new DataException("Gcs Failed to get for " + productName + ConstantsChar.BACKSLASH + version
                            + ConstantsChar.BACKSLASH + component + ConstantsChar.BACKSLASH + locale + ".", e);
                }
            }
        }
        logger.debug("end get");
        if (bundles.size() == 0) {
            throw new DataException("Gcs No bundle is found.");
        }
        return bundles;
    }
}