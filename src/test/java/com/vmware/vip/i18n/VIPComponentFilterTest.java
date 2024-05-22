/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.filters.VIPComponentFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class VIPComponentFilterTest extends BaseTestClass{
    VIPComponentFilter componentFilter = new VIPComponentFilter();

    @Before
    public void init() throws ServletException {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.createTranslationCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);

        componentFilter.init(null);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        String normalMsg = "var translation = {\"messages\" : {\"LeadTest\":\"[{0}] 测试警示\",\"table.host\":\"主机\",\"global_text_username\":\"用户名\"," +
                "\"sample.plural.key1\":\"{0, plural, other{\\\"{1}\\\"上有#个文件。}}\"}," +
                " \"productName\" : \"JavaclientTest\", \"vipServer\" : \"http://localhost:8099\", " +
                "\"pseudo\" : \"false\", \"collectSource\" : \"false\"};";

        String errorMsg = "{\"code\":400, \"message\": \"Request parameter 'locale' is required!\"}";

        String uri = "https://localhost/i18n/component/JAVA";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        //test normal output
        request.setQueryString("locale=zh-CN");
        componentFilter.doFilter(request, response, filterChain);
        Assert.assertEquals(normalMsg, response.getContentAsString());
        response.reset();

        //test error output
        request.setQueryString("");
        componentFilter.doFilter(request, response, filterChain);
        Assert.assertEquals(errorMsg, response.getContentAsString());
        response.reset();
    }
}
