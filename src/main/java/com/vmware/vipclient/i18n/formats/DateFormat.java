/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.formats;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.BaseFormat;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class DateFormat extends BaseFormat {
    private Logger logger = LoggerFactory.getLogger(DateFormat.class);

    public String getLocalizedDateFormat(String longDate, String pattern) {
        if (null == longDate || longDate.length() == 0) {
            throw new VIPJavaClientException("longDate can't be empty");
        }
        if (null == pattern || pattern.length() == 0) {
            throw new VIPJavaClientException("pattern can't be empty");
        }
        if (VIPCfg.getInstance().getVipService().getHttpRequester().isConnected()) {
            return this.getFormatFromRemote(longDate, pattern);
        } else {
            return "";
        }
    }

    private String getFormatFromRemote(String longDate, String pattern) {
        String format = "";
        StringBuffer dateAPIUrl = new StringBuffer(
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        dateAPIUrl.append("/i18n/api/v1/date/localizedDate?locale=");
        dateAPIUrl.append(this.locale);
        dateAPIUrl.append("&longDate=");
        dateAPIUrl.append(longDate);
        dateAPIUrl.append("&pattern=");
        dateAPIUrl.append(pattern);
        String retJsonStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                dateAPIUrl.toString(), ConstantsKeys.GET, null);
        if (null == retJsonStr || retJsonStr.length() == 0) {
            return format;
        }
        JSONObject retJson;
        try {
            retJson = (JSONObject) JSONValue.parseWithException(retJsonStr);
            if (retJson != null) {
                JSONObject dataJson = (JSONObject) retJson
                        .get(ConstantsKeys.DATA);
                if (dataJson != null) {
                    format = dataJson.get(ConstantsKeys.FORMATTED_DATE) == null ? ""
                            : dataJson.get(ConstantsKeys.FORMATTED_DATE)
                                    .toString();
                }
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return format;
    }
}
