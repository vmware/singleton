/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.utils.LocaleUtils;

/**
* This class represents the DTO for single component which contain one translation file's content.
* 
*/
public class SingleComponentDTO extends BaseDTO  implements Serializable{

   /**
	 * 
	 */
	private static final long serialVersionUID = 8137989045857846045L;

	// Component's name
   private String component = "";

   // The messages contains key/value pairs
   private Object messages;

   // The locale string for getting the translation
   private String locale = "";

   // The status of getting translation
   private String status = "";

   protected long id;

   public long getId() {
       return id;
   }

   public void setId(long id) {
       this.id = id;
   }

	public String getStatus() {
       return status;
   }

   public void setStatus(String status) {
       this.status = status;
   }

   public String getLocale() {
       return locale;
   }

   public void setLocale(String locale) {
       this.locale = LocaleUtils.normalizeToLanguageTag(locale);
   }

   public String getComponent() {
       return component;
   }

   public void setComponent(String component) {
       this.component = component;
   }

   public Object getMessages() {
       return messages;
   }

   public void setMessages(Object messages) {
   	if(messages == null) {
   		this.messages = "";
   	} else {
           this.messages = messages;
   	}
   }

   @SuppressWarnings("unchecked")
   public String toJSONString() {
       JSONObject jo = new JSONObject();
       jo.put(ConstantsKeys.COMPONENT, this.getComponent());
       jo.put(ConstantsKeys.lOCALE, this.getLocale());
       jo.put(ConstantsKeys.BUNDLES, this.getMessages());
       return jo.toJSONString();
   }

   public String toString() {
   	return "[productname-version-component-locale-pseudo-messages]: " + this.getProductName() + " - " + this.getVersion() + " - " + this.getComponent() + " - " + this.getLocale() + " - " + this.getPseudo() + " - " + this.getMessages();
   }

   public String toPrettyString() throws JsonProcessingException {
	Map<String, Object> json = new HashMap<>();
	json.put(ConstantsKeys.COMPONENT, getComponent());
	json.put(ConstantsKeys.lOCALE, getLocale());
	json.put(ConstantsKeys.MESSAGES, getMessages());
	json.put(ConstantsKeys.ID, getId());
	return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json);
}

/**
    * Get single component's DTO from one JSON string
    *
    * @param jsonStr One JSON string can convert to a SingleComponentDTO object
    * @return SingleComponentDTO
    */
   public static SingleComponentDTO getSingleComponentDTO(String jsonStr) throws ParseException {
       JSONObject genreJsonObject = null;
       genreJsonObject = (JSONObject) JSONValue.parseWithException(jsonStr);
       if (genreJsonObject == null) {
           return null;
       }
       SingleComponentDTO baseComponentMessagesDTO = new SingleComponentDTO();
       baseComponentMessagesDTO
               .setComponent((String) genreJsonObject.get(ConstantsKeys.COMPONENT));
       baseComponentMessagesDTO.setLocale((String) genreJsonObject.get(ConstantsKeys.lOCALE));
       Object object = genreJsonObject.get(ConstantsKeys.MESSAGES);
       baseComponentMessagesDTO.setMessages(object);
       return baseComponentMessagesDTO;
   }

   /**
    * Get the ordered SingleComponentDTO from one JSON string
    *
    * @param jsonStr One JSON string can convert to a SingleComponentDTO object
    * @return SingleComponentDTO
    */
   @SuppressWarnings("unchecked")
   public static SingleComponentDTO getSingleComponentDTOWithLinkedMessages(String jsonStr)
           throws ParseException {
       JSONParser parser = new JSONParser();
       ContainerFactory containerFactory = getContainerFactory();
       Map<String, Object> messages = new LinkedHashMap<String, Object>();
       Map<String, Object> bundle = null;
       bundle = (Map<String, Object>) parser.parse(jsonStr, containerFactory);
       if (bundle == null) {
           return null;
       }
       SingleComponentDTO baseComponentMessagesDTO = new SingleComponentDTO();
       baseComponentMessagesDTO.setId(Long.parseLong(bundle.get(ConstantsKeys.ID) == null ? "0" : bundle.get(ConstantsKeys.ID).toString()));
       baseComponentMessagesDTO.setComponent((String) bundle.get(ConstantsKeys.COMPONENT));
       baseComponentMessagesDTO.setLocale((String) bundle.get(ConstantsKeys.lOCALE));
       messages = (Map<String, Object>) bundle.get(ConstantsKeys.MESSAGES);
       baseComponentMessagesDTO.setMessages(messages);
       return baseComponentMessagesDTO;
   }

   private static ContainerFactory getContainerFactory() {
       ContainerFactory containerFactory = new ContainerFactory() {
           public List<Object> creatArrayContainer() {
               return new LinkedList<Object>();
           }

           public Map<String, Object> createObjectContainer() {
               return new LinkedHashMap<String, Object>();
           }
       };
       return containerFactory;
   }
}
