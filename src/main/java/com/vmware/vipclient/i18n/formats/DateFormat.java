/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.formats;

import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.BaseFormat;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
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
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                dateAPIUrl.toString(), ConstantsKeys.GET, null);
        String retJsonStr = (String) response.get(URLUtils.BODY);
        if (null == retJsonStr || retJsonStr.length() == 0) {
            return format;
        }
        JSONObject retJson;
        try {
            retJson = new JSONObject(retJsonStr);
            if (retJson != null) {
                JSONObject dataJson = (JSONObject) retJson
                        .get(ConstantsKeys.DATA);
                if (dataJson != null) {
                    format = dataJson.get(ConstantsKeys.FORMATTED_DATE) == null ? ""
                            : dataJson.get(ConstantsKeys.FORMATTED_DATE)
                                    .toString();
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
        return format;
    }
}
