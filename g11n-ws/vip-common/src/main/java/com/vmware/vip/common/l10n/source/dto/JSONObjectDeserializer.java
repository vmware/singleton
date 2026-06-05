/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;

public class JSONObjectDeserializer extends JsonDeserializer<JSONObject> {

    @Override
    public JSONObject deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        JSONObject jsonObject = new JSONObject();

        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode valueNode = node.get(fieldName);
                jsonObject.put(fieldName, convertJsonNode(valueNode));
            }
        }
        return jsonObject;
    }

    private Object convertJsonNode(JsonNode node) {
      if (node.isTextual()) {
          return node.asText();
      } else if (node.isNumber()) {
          return node.numberValue();
      } else if (node.isBoolean()) {
          return node.asBoolean();
      } else if (node.isObject()) {
          JSONObject jsonObject = new JSONObject();
          Iterator<String> fieldNames = node.fieldNames();
          while (fieldNames.hasNext()) {
              String fieldName = fieldNames.next();
              JsonNode valueNode = node.get(fieldName);
              jsonObject.put(fieldName, convertJsonNode(valueNode));
          }
          return jsonObject;
      } else if (node.isArray()) {
          org.json.JSONArray jsonArray = new org.json.JSONArray();
          for (JsonNode arrayElement : node) {
              jsonArray.put(convertJsonNode(arrayElement));
          }
          return jsonArray;
      } else if (node.isNull()) {
        return null;
      }
        return null;
    }
}
