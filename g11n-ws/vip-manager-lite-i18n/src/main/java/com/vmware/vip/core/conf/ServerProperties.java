/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.common.constants.ConstantsTomcat;

/**
 * this is the tomcat configuration properties
 *
 */
@Configuration
public class ServerProperties implements Serializable {

   private static final long serialVersionUID = 9008828985564148771L;

   @Value("${server.http.port}")
   private Integer httpPort;

   @Value("${server.port}")
   private Integer serverPort;

   @Value("${server.scheme}")
   private String serverScheme;

   @Value("${server.https.key-store}")
   private String httpsKeyStore;

   @Value("${server.https.key-store-password}")
   private String httpsKeyStorePassword;

   @Value("${server.https.key-store-type}")
   private String httpsKeyStoreType;

   @Value("${server.https.key-password}")
   private String httpsKeyPassword;

   @Value("${server.https.key-alias}")
   private String httpsKeyAlias;

   @Value("${server.trace.enable}")
   private Boolean allowTrace;

   public Integer getServerPort() {
      return serverPort;
   }

   public String getServerScheme() {
      return serverScheme;
   }

   public Integer getHttpPort() {
      return this.httpPort;
   }

   public String getHttpsKeyStore() {
      if (this.httpsKeyStore.startsWith(ConstantsTomcat.CLASSPATH_STR)) {
         return this.httpsKeyStore.replace(ConstantsTomcat.CLASSPATH_STR, "");
      } else {
         return this.httpsKeyStore;
      }
   }

   public String getHttpsKeyStorePassword() {
      return httpsKeyStorePassword;
   }

   public String getHttpsKeyStoreType() {
      return httpsKeyStoreType;
   }

   public String getHttpsKeyPassword() {
      return httpsKeyPassword;
   }

   public String getHttpsKeyAlias() {
      return httpsKeyAlias;
   }
   
   public Boolean isAllowTrace() {
      return allowTrace;
   }

}
