/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.vmware.l10n.conf.RsaCryptUtil;
import com.vmware.l10n.conf.S3Cfg;
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
    
    @Test
    public void testS3Cfg() {
    	S3Cfg cfg = new S3Cfg();
    	cfg.setAccessKey("abc");
    	cfg.setBucketName("abc");
    	cfg.setEncryption(false);
    	cfg.setPublicKey("abccdf");
    	cfg.setS3Region("us");
    	cfg.setSecretkey("sdfadsf");
    	cfg.getAccessKey();
    	cfg.getBucketName();
    	cfg.isEncryption();
    	cfg.getPublicKey();
    	cfg.getS3Region();
    	cfg.getSecretkey();
    	
    }
    
    @Test
    public void testRsaUtil() {
    	String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCB2ahoumYOFtIN5W1I8NIPDQNH/wK1YQsWtxqrXAu67XEg6rCm7Lwdj889L5kXuI3+nW93833zxb5+K1W//R+srfcwA/jZMqs1EVKyrdareWqVW0b6DvZFPV38UQVJmwfBJBkdUoTSZtqBrFhSFMfFWSG7Qoxz1NlJaJylkaEk3QIDAQAB";
    	String data = "Jfe9vTfTsU6OQfZ4xikT+oKxytZOB1binD5wi10n2GMoI+4Jc/Yyy9x9WbTbzj1a4lM6nGBYnOQSjFn3e1QhcL8uj7eCg9mLt6bTec1FGi5ctPFrJexnkBX9lis+qQ1ntwkbf6h2g6U1SYN7N/t+/fq7ubybR6QdKY6hNwIJrCA=";
    	try {
			RsaCryptUtil.decryptData(data, pubKey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
}
