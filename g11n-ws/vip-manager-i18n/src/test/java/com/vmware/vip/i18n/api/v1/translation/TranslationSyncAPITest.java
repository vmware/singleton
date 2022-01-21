/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.BootApplication;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationSyncAPITest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Before
    public void authentication() throws Exception{
        String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }

	@Test
	public void testUpdateTranslation() throws Exception{
		UpdateTranslationDTO updateTranslationDTO = new UpdateTranslationDTO();
		UpdateTranslationDataDTO updateTranslationDataDTO = new UpdateTranslationDataDTO();
		List<TranslationDTO> translationDTOList = new ArrayList<TranslationDTO>();
		TranslationDTO translationDTO = new TranslationDTO();
		translationDTO.setComponent(ConstantsForTest.CIM);
		translationDTO.setLocale(ConstantsForTest.ZH_HANS);
		String key = "Partner_Name";
		String translation = "合作伙伴名称";
		Map<String,String> messages = new HashMap<String,String>();
		messages.put(key, translation);
		translationDTO.setMessages(messages);
		translationDTOList.add(translationDTO);
		updateTranslationDataDTO.setProductName(ConstantsForTest.VCG);
		updateTranslationDataDTO.setTranslation(translationDTOList);
		updateTranslationDataDTO.setVersion(ConstantsForTest.VERSION);
		updateTranslationDTO.setData(updateTranslationDataDTO);
		updateTranslationDTO.setRequester(ConstantsForTest.GRM);
		ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(updateTranslationDTO);
		RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.PUT,ConstantsForTest.StringUpdateTranslationAPIURI,requestJson);
		String content = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, ConstantsForTest.StringValidateUpdateTranslationAPIURI);
		APIResponseDTO apiResponseDTO = mapper.readValue(content, APIResponseDTO.class);
		Map dataMap = (Map)apiResponseDTO.getData();
		String bundleTranslation = (String)dataMap.get("translation");
		//assertEquals("equals",translation,bundleTranslation);
	}

}
