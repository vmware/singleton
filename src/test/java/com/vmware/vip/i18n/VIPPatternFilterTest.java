/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.filters.VIPPatternFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class VIPPatternFilterTest extends BaseTestClass{
    VIPPatternFilter patternFilter = new VIPPatternFilter();

    @Before
    public void init() throws ServletException {
        patternFilter.init(null);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        String errorMsg = "{\"code\":400, \"message\": \"Request parameter 'locale' is required!\"}";

        String uri = "https://localhost/i18n/pattern";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        //test valid locale
        request.setQueryString("locale=zh-CN");
        patternFilter.doFilter(request, response, filterChain);
        Assert.assertNotEquals("var localeData ={}", response.getContentAsString());
        response.setCommitted(false);
        response.reset();

        //test empty locale
        request.setQueryString("");
        patternFilter.doFilter(request, response, filterChain);
        Assert.assertEquals(errorMsg, response.getContentAsString());
        response.reset();

        //test invalid locale
        request.setQueryString("locale=NSFTW");
        patternFilter.doFilter(request, response, filterChain);
        Assert.assertNotEquals("var localeData ={}", response.getContentAsString());
    }
}