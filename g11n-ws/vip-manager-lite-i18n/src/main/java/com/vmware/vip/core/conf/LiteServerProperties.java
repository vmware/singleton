/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.common.constants.ConstantsTomcat;

/**
 * this is the tomcat configuration properties
 *
 */
@Configuration
public class LiteServerProperties implements Serializable {

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
   
   @Value("${server.max-http-header-size:8192}")
   private Integer maxHttpHeaderSize;
   
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
              try {
                   return new File(this.httpsKeyStore).getCanonicalPath();
              } catch (IOException e) {
                   return this.httpsKeyStore;
              }
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

   public void setHttpPort(Integer httpPort) {
       this.httpPort = httpPort;
   }

   public void setServerPort(Integer serverPort) {
       this.serverPort = serverPort;
   }

   public void setServerScheme(String serverScheme) {
       this.serverScheme = serverScheme;
   }

   public void setHttpsKeyStore(String httpsKeyStore) {
       this.httpsKeyStore = httpsKeyStore;
   }

   public void setHttpsKeyStorePassword(String httpsKeyStorePassword) {
       this.httpsKeyStorePassword = httpsKeyStorePassword;
   }

   public void setHttpsKeyStoreType(String httpsKeyStoreType) {
       this.httpsKeyStoreType = httpsKeyStoreType;
   }

   public void setHttpsKeyPassword(String httpsKeyPassword) {
       this.httpsKeyPassword = httpsKeyPassword;
   }

   public void setHttpsKeyAlias(String httpsKeyAlias) {
       this.httpsKeyAlias = httpsKeyAlias;
   }

   public void setAllowTrace(Boolean allowTrace) {
       this.allowTrace = allowTrace;
   }

public Integer getMaxHttpHeaderSize() {
	return maxHttpHeaderSize;
}

public void setMaxHttpHeaderSize(Integer maxHttpHeaderSize) {
	this.maxHttpHeaderSize = maxHttpHeaderSize;
}
}
