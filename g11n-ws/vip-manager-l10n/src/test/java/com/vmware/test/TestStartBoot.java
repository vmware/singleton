/*
* Copyright 2019-2022 VMware, Inc.
* SPDX-License-Identifier: EPL-2.0
*/

package com.vmware.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.l10n.BootApplication;


public class TestStartBoot {
   private static Logger logger = LoggerFactory.getLogger(TestStartBoot.class);

   @Test
   public void test001servicel10n() {
       String[] args = { "-c" };
       try {
           BootApplication.main(args);
       } catch (Exception e) {
           // TODO Auto-generated catch block
           logger.error(e.getMessage(), e);
       }
       

       logger.info("TestSpringBoot");
   }


}
