
package com.vmware.test;

import java.util.HashMap;

import org.junit.Test;

import com.vmware.l10n.record.model.ComponentSourceModel;
import com.vmware.l10n.source.dto.SourceDTO;
import  com.vmware.l10n.source.dto.GRMResponseDTO;

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
}
