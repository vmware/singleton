/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.vmware.l10n.record.model.ComponentSourceModel;
import  com.vmware.l10n.source.dto.GRMResponseDTO;
import com.vmware.l10n.source.dto.SourceDTO;
import com.vmware.l10n.translation.dto.UpdateListDTO;
import com.vmware.l10n.translation.readers.LocalJSONReader;
import com.vmware.l10n.utils.SourceCacheUtils;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

public class TestModels {
    @Test
    public void TestComponentSource() {
        ComponentSourceModel model = new ComponentSourceModel();
        model.setComponent("abc");
        model.setLocale("cn");
        model.setProduct("abc");
        model.setVersion("1.0.0");
        model.getComponent();
        model.getLocale();
        model.getProduct();
        model.getVersion();
        model.setMessages(new HashMap<String, Object>());
        model.getMessages();
        
    }
    @Test
    public void GRMResponseDTO() {
        GRMResponseDTO dto = new GRMResponseDTO();
        dto.setErrorMessage("test");
        dto.setResult("test Result");
        dto.setStatus(1);
        dto.setTimestamp(System.currentTimeMillis());
        
        dto.getErrorMessage();
        dto.getResult();
        dto.getStatus();
        dto.getTimestamp();
    }

    @Test
    public void SourceDTO() {
        SourceDTO source = new SourceDTO();
        source.setId(1000L);
        source.setSource("test");
        source.setVersion("1.0.0");
        source.setComments("keytest", "key test comment");
    }
    
    
    @Test
    public void TestUpdateListDTO() {
        UpdateListDTO dto = new UpdateListDTO();
        dto.setName("test");
        dto.setSubList(new ArrayList<Object>());
        dto.getName();
        dto.getSubList();
        
    }
    
    @Test 
    public void testSourceCacheUtils() {
        StringSourceDTO sourceDTO = new StringSourceDTO();
        sourceDTO.setProductName("devCenter");
        sourceDTO.setVersion("1.0.0");
        sourceDTO.setComponent("default");
        sourceDTO.setLocale(ConstantsKeys.LATEST);
        sourceDTO.setKey("dc.myhome.open3");
        sourceDTO.setSource("this open3's value");
        sourceDTO.setComment("dc new string");
        ComponentSourceDTO comp = new ComponentSourceDTO();
        BeanUtils.copyProperties(sourceDTO, comp);
        comp.setMessages(sourceDTO.getKey(), sourceDTO.getSource());
        String catcheKey = PathUtil.generateCacheKey(sourceDTO);
        try {
            SourceCacheUtils.addSourceCache(catcheKey, comp);
            SourceCacheUtils.getSourceCache(catcheKey);
            SourceCacheUtils.updateSourceCache(catcheKey, comp);
            SourceCacheUtils.delSourceCacheByKey(catcheKey);
            SourceCacheUtils.addSourceCache(catcheKey, comp);
            SourceCacheUtils.getSourceCacheWithDel(catcheKey);
            
        } catch (VIPCacheException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test 
    public void testLocalJSONReader() {
        try {
            LocalJSONReader jsonRd =  new LocalJSONReader(); 
            jsonRd.getTranslationOutJar(".json");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
