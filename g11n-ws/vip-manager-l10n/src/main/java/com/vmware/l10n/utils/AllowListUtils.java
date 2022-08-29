/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;

public class AllowListUtils {
	
    private AllowListUtils() {}
    
	public static Map<String, List<String>> parseAllowList(String jsonStr) {
		
		if (!StringUtils.isEmpty(jsonStr)) {
			ObjectMapper objmap = new ObjectMapper();
			JavaType stringType = objmap.constructType(String.class);
			CollectionLikeType arrayType = objmap.getTypeFactory().constructCollectionLikeType(ArrayList.class,
					String.class);
			MapLikeType mapType = objmap.getTypeFactory().constructMapLikeType(HashMap.class, stringType, arrayType);
			Map<String, List<String>> obj = null;
			try {
				obj = objmap.readValue(jsonStr, mapType);
			} catch (JsonProcessingException e) {
				return null;
			}
			return obj;
		}
		return null;
	}

}
