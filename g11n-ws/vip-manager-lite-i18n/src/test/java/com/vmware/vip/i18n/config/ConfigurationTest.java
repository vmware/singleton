package com.vmware.vip.i18n.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.core.conf.ServerProperties;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class ConfigurationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	
	@Test
	public void testServerProperites() {
	    ServerProperties sp = webApplicationContext.getBean(ServerProperties.class);
	    sp.getServerPort();
	    sp.getServerScheme();
	    sp.getHttpPort();
	    sp.getHttpsKeyStore();
	    sp.getHttpsKeyStorePassword();
	    sp.getHttpsKeyAlias();
	    sp.isAllowTrace();
	}
	
	   
    @Test
    public void testServerProperites1() {
        ServerProperties sp = new ServerProperties();
        sp.getServerPort();
        sp.getServerScheme();
        sp.getHttpPort();
        sp.getHttpsKeyStore();
        sp.getHttpsKeyStorePassword();
        sp.getHttpsKeyAlias();
        sp.isAllowTrace();
    }


}
