/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.l10n.source.common.Constants;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationSyncServerControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

	@Test
	public void testUpdateTranslation() throws Exception {
		String API = L10NAPIV1.UPDATE_TRANSLATION_L10N.replace("{"+ APIParamName.PRODUCT_NAME + "}", "vCG").replace("{"+APIParamName.VERSION2+"}", "1.0.0");
		 MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	        mockMvc.perform(MockMvcRequestBuilders.post(API)
	                        .content(Constants.UPDATETRANSLATIONAPIREQUESTBODY)
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
	                .andExpect(MockMvcResultMatchers.content().string(Constants.UPDATETRANSLATIONEXPECTRESULT));
	}
	
	
	


}
