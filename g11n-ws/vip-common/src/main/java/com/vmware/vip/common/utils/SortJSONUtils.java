/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.util.IOUtil;
import com.vmware.vip.common.l10n.source.util.PathUtil;

public class SortJSONUtils {

   private SortJSONUtils() {
   }

   private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

   /**
    * get the json String with key in alphabet order
    */
   public static String getOrderJsonString(Map<String, Object> jsonMap)
         throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writer(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .withDefaultPrettyPrinter().writeValueAsString(jsonMap);
   }


   /**
    * write the json String with key in alphabet order to local file
    */
   public static void writeJSONObjectToJSONFile(String jsonFileName,
         SingleComponentDTO singleComponentDTO) throws VIPResourceOperationException {
      logger.info("Write JSON content to file: " + jsonFileName);
      Map<String, Object> json = new HashMap<String, Object>();
      json.put(ConstantsKeys.COMPONENT, singleComponentDTO.getComponent());
      json.put(ConstantsKeys.lOCALE, singleComponentDTO.getLocale());
      json.put(ConstantsKeys.MESSAGES, singleComponentDTO.getMessages());
      OutputStreamWriter write = null;
      BufferedWriter writer = null;
      FileOutputStream outputStream = null;
      try {
         File f = new File(PathUtil.filterPathForSecurity(jsonFileName));
         if (!f.exists()) {
            f.createNewFile();
         }
         outputStream = new FileOutputStream(f);
         write = new OutputStreamWriter(outputStream, ConstantsUnicode.UTF8);
         writer = new BufferedWriter(write);
         String jsonStr = getOrderJsonString(json);
         logger.debug("JSON content: {}", jsonStr);
         writer.write(jsonStr);
      } catch (IOException e) {
         throw new VIPResourceOperationException("Write file '" + jsonFileName + "' failed.");
      } finally {
         IOUtil.closeWriter(writer);
         IOUtil.closeWriter(write);
         IOUtil.closeOutputStream(outputStream);
      }
   }

   /**
    * write the json String with key in alphabet order to local file
    */
   public static void writeJSONObjectToJSONFile(String jsonFileName, String component,
         String locale, Map<String, String> messages) throws VIPResourceOperationException {
      logger.info("Write JSON content to file: " + jsonFileName);
      SingleComponentDTO dto = new SingleComponentDTO();
      dto.setComponent(component);
      dto.setLocale(locale);
      dto.setMessages(messages);
      writeJSONObjectToJSONFile(jsonFileName, dto);
   }
}
