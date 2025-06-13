/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.Response;

public class ResponseUtil {

    public static Object getMessagesFromResponse(String responseStr, String node) {
        Object msgObject = null;
        if (responseStr == null || responseStr.equalsIgnoreCase(""))
            return msgObject;
        try {
            JSONObject responseObj = new JSONObject(responseStr);
            if (responseObj != null) {
                JSONObject dataObj = (JSONObject) responseObj
                        .get(ConstantsForTest.DATA);
                if (dataObj != null) {
                    msgObject = dataObj.get(node);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return msgObject;
    }

    public static Response getResponse(String responseStr) {
        Response response = null;
        if (responseStr == null || responseStr.equalsIgnoreCase(""))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        APIResponseDTO apiResponseDTO = null;
        try {
            apiResponseDTO = mapper.readValue(responseStr, APIResponseDTO.class);
            response = apiResponseDTO.getResponse();
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }

    public static int getResponseCode(String responseStr) {
        int responseCode = 0;
        try {
            Response response = getResponse(responseStr);
            if(response != null) {
            	responseCode=response.getCode();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseCode;
    }

    public static Object getMainDataFromResponse(String responseStr) {
        Object data = null;
        if (responseStr == null || responseStr.equalsIgnoreCase(""))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        APIResponseDTO apiResponseDTO = null;
        try {
            apiResponseDTO = mapper.readValue(responseStr, APIResponseDTO.class);
            data = apiResponseDTO.getData();
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    public static Object getNodeDataFromResponse(String responseStr, String dataNode) {
        Object nodeValue = null;
        try{
            Map dataMap=(Map) getMainDataFromResponse(responseStr);
            if(dataMap != null) {
            	nodeValue = dataMap.get(dataNode);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return nodeValue;
    }
}
