/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.l10n.BootApplication;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class RemoteSyncServiceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Test
    public void testPing() {
        RemoteSyncService rss =  webApplicationContext.getBean(RemoteSyncService.class);
        
        try {
            rss.ping("localhost");
        } catch (L10nAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSend() {
        RemoteSyncService rss =  webApplicationContext.getBean(RemoteSyncService.class);
        
        try {
            rss.send(new ComponentSourceDTO(), "localhost");
        } catch (L10nAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    

}
