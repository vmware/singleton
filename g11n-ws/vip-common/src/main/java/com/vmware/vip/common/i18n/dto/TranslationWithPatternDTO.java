/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * this class is use to the get combine pattern and translation request parameters object
 *
 */
public class TranslationWithPatternDTO implements Serializable {

   private static final long serialVersionUID = 887039773528607451L;

   private String language;
   private String scope;
   private String productName;
   private String version;
   private List<String> components;
   private String region;
   private String pseudo;
   private int combine;
   private String scopeFilter;

   public String getLanguage() {
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public String getScope() {
      return scope;
   }

   public void setScope(String scope) {
      this.scope = scope;
   }

   public String getProductName() {
      return productName;
   }

   public void setProductName(String productName) {
      this.productName = productName;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public List<String> getComponents() {
      return components;
   }

   public void setComponents(List<String> components) {
      this.components = components;
   }

   public String getRegion() {
      return region;
   }

   public void setRegion(String region) {
      this.region = region;
   }

   public String getPseudo() {
      if (StringUtils.isEmpty(this.pseudo)) {
         return "false";
      }
      return pseudo;
   }

   public void setPseudo(String pseudo) {
      this.pseudo = pseudo;
   }

   public int getCombine() {
      return combine;
   }

   public void setCombine(int combine) {
      this.combine = combine;
   }

   public String getScopeFilter() {
      return scopeFilter;
   }

   public void setScopeFilter(String scopeFilter) {
      this.scopeFilter = scopeFilter;
   }
}
