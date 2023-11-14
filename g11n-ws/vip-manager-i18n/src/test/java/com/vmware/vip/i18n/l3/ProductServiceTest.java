/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.l3;

import com.vmware.vip.BootApplication;
import com.vmware.vip.core.messages.service.product.IProductService;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class ProductServiceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private IProductService iProductService;
    private static String testList1 = "classpath:bundle_unittest.json";
    private static String testList2 = "bundle_unittest.json";
    private static String testList2_content = "{\n" +
            "  \"unitTest\":[\"1.0.0\",\"2.0.0\"]\n" +
            "}";
    private static Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Test
    public void testProductAllowList1(){
        Map<String, Object> result = iProductService.getAllowProductList(testList1);
        logger.info(String.valueOf(result.size()));
        Assert.assertTrue(result.size()>0);
    }

    @Test
    public void testProductAllowList2(){
        File file = new File(testList2);
        file.deleteOnExit();
        try {
            file.createNewFile();
            FileUtils.write(file, testList2_content, "UTF-8");
            Map<String, Object> result = iProductService.getAllowProductList(file.getAbsolutePath());
            logger.info(String.valueOf(result.size()));
            Assert.assertTrue(result.size()>0);
            file.deleteOnExit();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }



    }


}
