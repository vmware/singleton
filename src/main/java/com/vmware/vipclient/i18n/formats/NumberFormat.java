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

public class NumberFormat extends BaseFormat {
    Logger logger = LoggerFactory.getLogger(NumberFormat.class);

    public String getLocalizedNumber(String number, String scale) {
        if (null == number || number.length() == 0) {
            throw new VIPJavaClientException("number can't be empty");
        }
        if (null == scale || scale.length() == 0) {
            throw new VIPJavaClientException("scale can't be empty");
        }
        if (VIPCfg.getInstance().getVipService().getHttpRequester().isConnected()) {
            return this.getFormatFromRemote(number, scale);
        } else {
            return "";
        }
    }

    private String getFormatFromRemote(String number, String scale) {
        String format = "";
        StringBuffer numberAPIUrl = new StringBuffer(
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        numberAPIUrl.append("/i18n/api/v1/number/localizedNumber?locale=");
        numberAPIUrl.append(this.locale);
        numberAPIUrl.append("&number=");
        numberAPIUrl.append(number);
        numberAPIUrl.append("&scale=");
        numberAPIUrl.append(scale);
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                numberAPIUrl.toString(), ConstantsKeys.GET, null);
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
                    format = dataJson.get(ConstantsKeys.FORMATTED_NUMBER) == null ? ""
                            : dataJson.get(ConstantsKeys.FORMATTED_NUMBER)
                                    .toString();
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
        return format;
    }
}
