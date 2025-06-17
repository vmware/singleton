/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Iterator;
import java.io.IOException;

import org.json.JSONObject;

public class JSONObjectSerializer extends JsonSerializer<JSONObject> {

    @Override
    public void serialize(JSONObject jsonObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    	System.out.println("jsonObject=" + jsonObject.toString());
    	//jsonGenerator.writeString(jsonObject.toString());
    	jsonGenerator.writeStartObject();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            jsonGenerator.writeFieldName(key);
            serializerProvider.defaultSerializeValue(value, jsonGenerator);
        }
        jsonGenerator.writeEndObject();
    }
}