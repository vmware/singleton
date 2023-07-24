/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.image;

import com.vmware.i18n.l2.dao.image.ICountryFlagDao;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.core.messages.exception.L2APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;

@Service
public class CountryFlagServiceImpl implements ICountryFlagService {
    private static Logger logger = LoggerFactory.getLogger(CountryFlagServiceImpl.class);

    @Autowired
    private ICountryFlagDao countryFlagDao;

    @Override
    public FileChannel getCountryFlagChannel(String region, int scale) throws L2APIException {
        String result = null;
        switch(scale){
            case 1 : result= "1x1"; break;
            case 2 : result = "3x2"; break;
            default: throw new L2APIException(ConstantsMsg.IMAGE_NOT_SUPPORT_SCALE);
        }
        try {
            return this.countryFlagDao.getCountryFlagChannel(result, region.toUpperCase());
        } catch (FileNotFoundException  fe){
            logger.warn(fe.getMessage(), fe);
            throw new L2APIException(String.format(ConstantsMsg.IMAGE_NOT_SUPPORT_REGION, region), fe);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new L2APIException(e.getMessage());
        }

    }
}
