/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.common.Constants;
import com.vmware.i18n.utils.JSONUtil;

public class CldrTimeZoneUtils {

	@SuppressWarnings("unchecked")
	public static Map<String, JSONArray> findTimezoneKeys(JSONObject metaZonesJson) {
		JSONObject timzone = (JSONObject) select(metaZonesJson, "supplemental.metaZones.metazoneInfo.timezone");
		Map<String, JSONArray> result = new TreeMap<String, JSONArray>();
		for (Entry<String, Object> entry : (Set<Map.Entry<String, Object>>) timzone.entrySet()) {
			String zoneKeystr1 = entry.getKey();
			Object obj1 = entry.getValue();
			if (obj1 instanceof List) {
				JSONArray objArry = (JSONArray) obj1;
				JSONObject usesMetazones = (JSONObject) objArry.get(0);
				JSONObject usesMetazoneObj = (JSONObject) usesMetazones.get(Constants.TIMEZONENAME_USES_METAZONE);
				if (usesMetazoneObj != null) {
					result.put(zoneKeystr1, objArry);
					continue;
				}
			}
			JSONObject jsonObj1 = (JSONObject) obj1;
			for (Entry<String, Object> entry1 : (Set<Map.Entry<String, Object>>) jsonObj1.entrySet()) {
				String zoneKeystr2 = entry1.getKey();
				Object obj2 = entry1.getValue();
				if (obj2 instanceof List) {
					JSONArray objArry2 = (JSONArray) obj2;
					JSONObject usesMetazones2 = (JSONObject) objArry2.get(0);
					JSONObject usesMetazoneObj2 = (JSONObject) usesMetazones2.get(Constants.TIMEZONENAME_USES_METAZONE);
					if (usesMetazoneObj2 != null) {
						zoneKeystr2 = zoneKeystr1 + "/" + zoneKeystr2;
						result.put(zoneKeystr2, objArry2);
						continue;
					}
				}
				JSONObject jsonObj2 = (JSONObject) obj2;
				for (Entry<String, Object> entry2 : (Set<Map.Entry<String, Object>>) jsonObj2.entrySet()) {
					String zoneKeystr3 = entry2.getKey();
					Object obj3 = entry2.getValue();
					if (obj3 instanceof List) {
						JSONArray objArry3 = (JSONArray) obj3;
						JSONObject usesMetazones3 = (JSONObject) objArry3.get(0);
						JSONObject usesMetazoneObj3 = (JSONObject) usesMetazones3
								.get(Constants.TIMEZONENAME_USES_METAZONE);
						if (usesMetazoneObj3 != null) {
							zoneKeystr3 = zoneKeystr1 + "/" + zoneKeystr2 + "/" + zoneKeystr3;
							result.put(zoneKeystr3, objArry3);
							continue;
						}
					}
					JSONObject jsonObj3 = (JSONObject) obj3;
					for (Entry<String, Object> entry3 : (Set<Map.Entry<String, Object>>) jsonObj3.entrySet()) {
						String zoneKeystr4 = entry3.getKey();
						Object obj4 = entry3.getValue();
						if (obj4 instanceof List) {
							JSONArray objArry4 = (JSONArray) obj4;
							JSONObject usesMetazones4 = (JSONObject) objArry4.get(0);
							JSONObject usesMetazoneObj4 = (JSONObject) usesMetazones4
									.get(Constants.TIMEZONENAME_USES_METAZONE);
							if (usesMetazoneObj4 != null) {
								zoneKeystr4 = zoneKeystr1 + "/" + zoneKeystr2 + "/" + zoneKeystr3 + "/" + zoneKeystr4;
								result.put(zoneKeystr4, objArry4);
								continue;
							}
						}
						System.out.println("###############there have 5 level timezone key####################");
					}
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static String createTimeZoneNameJson(JSONObject metaZonesJson, JSONObject timeZoneNamesPath,
			String language) {
		JSONArray arry = (JSONArray) select(metaZonesJson, "supplemental.metaZones.metazones");
		JSONObject dates = (JSONObject) select(timeZoneNamesPath, "main." + language + ".dates");
		String gmtZeroFormat = (String) select(dates, "timeZoneNames.gmtZeroFormat");
		String gmtFormat = (String) select(dates, "timeZoneNames.gmtFormat");
		String hourFormat = (String) select(dates, "timeZoneNames.hourFormat");
		String regionFormat = (String) select(dates, "timeZoneNames.regionFormat");
		String regionFormatTypeDaylight = (String) select(dates, "timeZoneNames.regionFormat-type-daylight");
		String regionFormatTypeStandard = (String) select(dates, "timeZoneNames.regionFormat-type-standard");
		String fallbackFormat = (String) select(dates, "timeZoneNames.fallbackFormat");
		List<LinkedHashMap<String,Object>> metaZones = new ArrayList<LinkedHashMap<String,Object>>(); 
		Map<String, JSONArray> timezoneKeysProps = findTimezoneKeys(metaZonesJson);
		Iterator<JSONObject> iterator = arry.iterator();
		Map<String, List<JSONObject>> mapZonesMap = new TreeMap<String, List<JSONObject>>();
		while (iterator.hasNext()) {
			JSONObject objZone = iterator.next();
			String timezoneKey = (String) select(objZone, "mapZone._type");
			if (mapZonesMap.get(timezoneKey) != null) {
				mapZonesMap.get(timezoneKey).add(objZone);
			} else {
				List<JSONObject> list = new ArrayList<>();
				list.add(objZone);
				mapZonesMap.put(timezoneKey, list);
			}
		}
		for (Entry<String, JSONArray> entry : timezoneKeysProps.entrySet()) {
			LinkedHashMap<String,Object> cldrMetaZone = new LinkedHashMap<String,Object>();
			String timezoneKey = entry.getKey();
			cldrMetaZone.put(Constants.TIMEZONENAME_METAZONE_TIMEZONEKEY, timezoneKey);
			String timeZone = findTimeZone(timezoneKey, gmtFormat, gmtZeroFormat, hourFormat);
			String exemplarCity = (String) select(dates,
					"timeZoneNames.zone." + timezoneKey.replace("/", ".") + ".exemplarCity");
			JSONArray mataZoneP = entry.getValue();
			cldrMetaZone.put(Constants.TIMEZONENAME_METAZONE_EXEMPLARCITY, exemplarCity);
			cldrMetaZone.put(Constants.TIMEZONENAME_METAZONE_TIMEZONE, timeZone);
			Iterator<JSONObject> metaiterator = mataZoneP.iterator();
			List<Map<String,Object>> usesMetazones = new ArrayList<Map<String,Object>>();
			while (metaiterator.hasNext()) {
				JSONObject objZone = metaiterator.next();
				Map<String, Object> usesMetazoneMap = new TreeMap<String, Object>();
				String metazoneKey = (String) select(objZone, "usesMetazone._mzone");
				String _fromVal = (String) select(objZone, "usesMetazone._from");
				String _toVal = (String) select(objZone, "usesMetazone._to");
				usesMetazoneMap.put("_mzone", metazoneKey);
				if (_fromVal != null) {
					usesMetazoneMap.put("_from", _fromVal);
				}
				if (_toVal != null) {
					usesMetazoneMap.put("_to", _toVal);
				}
				JSONObject metazoneValue = (JSONObject) select(dates, "timeZoneNames.metazone." + metazoneKey);
				if (metazoneValue != null) {
					String longStandard = (String) select(metazoneValue, "long.standard");
					String longDaylight = (String) select(metazoneValue, "long.daylight");
					String longGeneric = (String) select(metazoneValue, "long.generic");
					String shortStandard = (String) select(metazoneValue, "short.standard");
					String shortDaylight = (String) select(metazoneValue, "short.daylight");
					String shortGeneric = (String) select(metazoneValue, "short.generic");
					if (longStandard != null || longDaylight != null || longGeneric != null) {
						TimeZoneDisplayName longPerp = new TimeZoneDisplayName();
						longPerp.setDaylight(longDaylight);
						longPerp.setGeneric(longGeneric);
						longPerp.setStandard(longStandard);
						usesMetazoneMap.put(Constants.LONG, longPerp);
					}
					if (shortStandard != null || shortDaylight != null || shortGeneric != null) {
						TimeZoneDisplayName shortPerp = new TimeZoneDisplayName();
						shortPerp.setDaylight(shortDaylight);
						shortPerp.setGeneric(shortGeneric);
						shortPerp.setStandard(shortStandard);
						usesMetazoneMap.put(Constants.SHORT, shortPerp);
					}
					usesMetazones.add(usesMetazoneMap);
				}
			}
			cldrMetaZone.put(Constants.TIMEZONENAME_METAZONE_USESMETAZONES, usesMetazones);
			List<JSONObject> listMap = mapZonesMap.get(timezoneKey);
			cldrMetaZone.put(Constants.TIMEZONENAME_METAZONE_MAPZONES, listMap);
			metaZones.add(cldrMetaZone);
		}
		TimeZoneName zone = new TimeZoneName(language, gmtZeroFormat, gmtFormat, hourFormat, regionFormat,
				regionFormatTypeDaylight, regionFormatTypeStandard, fallbackFormat, metaZones);
		String result = null;
		try {
			ObjectMapper objMapper = new ObjectMapper();
			objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			result = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(zone);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String findTimeZone(String targetId, String gmtFormat, String gmtZeroFormat, String hourFormat) {

		TimeZone timeZone = TimeZone.getTimeZone(targetId);
		int offset = timeZone.getRawOffset();
		if (offset > 0) {
			Date date = new Date(offset);
			SimpleDateFormat outputFormat = new SimpleDateFormat(hourFormat.split(";")[0].trim());
			outputFormat.setTimeZone(TimeZone.getTimeZone(gmtZeroFormat));
			return MessageFormat.format(gmtFormat, outputFormat.format(date));

		} else if (offset < 0) {
			Date date = new Date(0 - offset);
			SimpleDateFormat outputFormat = new SimpleDateFormat(hourFormat.split(";")[1].trim());
			outputFormat.setTimeZone(TimeZone.getTimeZone(gmtZeroFormat));
			return MessageFormat.format(gmtFormat, outputFormat.format(date));

		} else {
			return gmtZeroFormat;
		}
	}

	/**
	 * Get the node value of JSON string. e.g. main.locale.day
	 * 
	 * @param jsonObj JSONObject
	 * @param keyPath JSON node path
	 * @return the node value
	 */
	private static Object select(JSONObject jsonObj, String keyPath) {
		try {
			return JSONUtil.select(jsonObj, keyPath);
		} catch (Exception e) {
			return null;
		}
	}

}
