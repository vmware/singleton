/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class RequestUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(RequestUtil.class);

    public static HttpHeaders getHeaders(WebApplicationContext webApplicationContext) {
        MockServletContext application = (MockServletContext) webApplicationContext.getServletContext();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("sessionID", (String) application.getAttribute("sessionID"));
        httpHeaders.add("token", (String) application.getAttribute("token"));
        return httpHeaders;
    }
    
    public static Cookie getCookies(WebApplicationContext webApplicationContext) {
        MockServletContext application = (MockServletContext) webApplicationContext.getServletContext();
        Cookie cookies = new Cookie("JSESSIONID",(String) application.getAttribute("sessionID"));
        return cookies;
    }
    
    public static MockHttpSession getSession(WebApplicationContext webApplicationContext) {
        MockServletContext application = (MockServletContext) webApplicationContext.getServletContext();
        MockHttpSession session=new MockHttpSession();
        session.setAttribute("token", application.getAttribute("token"));
        return session;
    }
    


    public static String sendRequest(WebApplicationContext webApplicationContext,String requestType, String uriWithParam, String ...requestJsons) throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockHttpServletResponse mockResponse =null;
        if(ConstantsForTest.POST.equals(requestType)){
            if(requestJsons.length > 0){
                mockResponse =sendPost(mockMvc,uriWithParam,requestJsons[0]);
            }else{
                mockResponse =sendPost(mockMvc,uriWithParam,"");
            }
        }else if(ConstantsForTest.GET.equals(requestType)){
            HttpHeaders httpHeaders=getHeaders(webApplicationContext);
            Cookie cookies =getCookies(webApplicationContext);
            MockHttpSession session=getSession(webApplicationContext);
            mockResponse =sendGet(mockMvc,httpHeaders,cookies,session,uriWithParam);
        }else if(ConstantsForTest.PUT.equals(requestType)){
            if(requestJsons.length > 0){
                mockResponse =sendPut(mockMvc,uriWithParam,requestJsons[0]);
            }
        }
        int status = 400;
        if(mockResponse != null) {
           status =  mockResponse.getStatus();
        }
        String uri = "";
        if (uriWithParam.indexOf(ConstantsForTest.QuestionMark) > 0) {
            uri = uriWithParam.substring(0, uriWithParam.indexOf(ConstantsForTest.QuestionMark));
        } else {
            uri = uriWithParam;
        }
        if (status != 200) {
            LOGGER.error(MessageUtil.getFailureString(uri, status));
            throw new RuntimeException(MessageUtil.getFailureString(uri, status));
        }
        String res = null;
        if(mockResponse != null) {
            res = mockResponse.getContentAsString();
        }
        System.out.println(MessageUtil.getSuccessString(uri, res));
        LOGGER.info(MessageUtil.getSuccessString(uri, res));
        return res;
    }

    public static MockHttpServletResponse sendPost(MockMvc mockMvc,String uriWithParam,String requestJson){
        MockHttpServletResponse mockResponse = null;
        try {
            mockResponse = mockMvc
                    .perform(
                            post(uriWithParam, ConstantsForTest.JSON)
                            .characterEncoding(ConstantsForTest.UTF8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            )
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mockResponse;
    }
    
    public static MockHttpServletResponse sendGet(MockMvc mockMvc,HttpHeaders httpHeaders,Cookie cookies,MockHttpSession session,String uriWithParam){
        MockHttpServletResponse mockResponse = null;
        try {
            mockResponse = mockMvc
                    .perform(
                            get(uriWithParam, ConstantsForTest.JSON).characterEncoding(
                                    ConstantsForTest.UTF8).headers(httpHeaders).cookie(cookies).session(session)).andReturn().getResponse();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mockResponse;
    } 
    
    public static MockHttpServletResponse sendPut(MockMvc mockMvc,String uriWithParam,String requestJson){
        MockHttpServletResponse mockResponse = null;
        try {
            mockResponse = mockMvc
                    .perform(
                            put(uriWithParam, ConstantsForTest.JSON)
                            .characterEncoding(ConstantsForTest.UTF8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            )
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mockResponse;
    }
}
