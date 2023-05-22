/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.pattern;

import com.vmware.i18n.l2.dao.pattern.ICountryFlagDao;
import com.vmware.vip.core.messages.exception.L2APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;

@Service
public class CountryFlagServiceImpl implements ICountryFlagService{
    private static Logger logger = LoggerFactory.getLogger(CountryFlagServiceImpl.class);

    @Autowired
    private ICountryFlagDao countryFlagDao;

    @Override
    public FileChannel getCountryFlagChannel(String region, int scale) throws L2APIException {
        String result = null;
        switch(scale){
            case 2 :  result = "3x2"; break;
            default: result = "1x1";
        }
        try {
            return this.countryFlagDao.getCountryFlagChannel(result, region.toUpperCase());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new L2APIException(e.getMessage());
        }

    }
}
