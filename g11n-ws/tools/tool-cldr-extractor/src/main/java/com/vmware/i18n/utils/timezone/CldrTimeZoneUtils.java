/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.utils.JSONUtil;

public class CldrTimeZoneUtils {
    
  
    
    @SuppressWarnings("unchecked")
    public static String createTimeZoneNameJson(JSONObject metaZonesJson, JSONObject timeZoneNamesPath,
            String language) {
        JSONArray arry = (JSONArray) select(metaZonesJson, "supplemental.metaZones.metazones");
        JSONObject dates = (JSONObject) select(timeZoneNamesPath,"main." + language + ".dates");
        String gmtZeroFormat = (String) select(dates, "timeZoneNames.gmtZeroFormat");
        String gmtFormat = (String) select(dates, "timeZoneNames.gmtFormat");
        String hourFormat = (String) select(dates, "timeZoneNames.hourFormat");
        String regionFormat = (String) select(dates, "timeZoneNames.regionFormat");
        String regionFormatTypeDaylight = (String) select(dates,
                "timeZoneNames.regionFormat-type-daylight");
        String regionFormatTypeStandard = (String) select(dates,
                "timeZoneNames.regionFormat-type-standard");
        String fallbackFormat = (String) select(dates, "timeZoneNames.fallbackFormat");
        Iterator<JSONObject> iterator = arry.iterator();
        List<CldrMetaZone> metaZones = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject objZone = iterator.next();
            String zoneKey = (String) select(objZone, "mapZone._type");
            String territory = (String) select(objZone, "mapZone._territory");
            String metazoneKey = (String) select(objZone, "mapZone._other");
            JSONObject metazoneValue = (JSONObject) select(dates,
                    "timeZoneNames.metazone." + metazoneKey);
            String timeZone = findTimeZone(zoneKey, gmtFormat, gmtZeroFormat, hourFormat);
            String exemplarCity = null;
            try {
                exemplarCity = (String) select(dates,
                        "timeZoneNames.zone." + zoneKey.replace("/", ".") + ".exemplarCity");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                exemplarCity = "";
                e.printStackTrace();
            }

            TimeZoneDisplayName name = new TimeZoneDisplayName();
            if (metazoneValue == null) {
                String stard = MessageFormat.format(regionFormatTypeStandard, exemplarCity);
                name.setLongStandard(stard);
            } else {
                String longStandard = (String) select(metazoneValue, "long.standard");
                String longDaylight = (String) select(metazoneValue, "long.daylight");
                String longGeneric = (String) select(metazoneValue, "long.generic");
                String shortStandard = (String) select(metazoneValue, "short.standard");
                String shortDaylight = (String) select(metazoneValue, "short.daylight");
                String shortGeneric = (String) select(metazoneValue, "short.generic");
                name.setLongStandard(longStandard);
                name.setLongDaylight(longDaylight);
                name.setLongGeneric(longGeneric);
                name.setShortStandard(shortStandard);
                name.setShortDaylight(shortDaylight);
                name.setShortGeneric(shortGeneric);
            }
            metaZones.add(new CldrMetaZone(zoneKey, exemplarCity, metazoneKey, timeZone, name,
                    territory));
        }

        TimeZoneName zone = new TimeZoneName(language, gmtZeroFormat, gmtFormat, hourFormat,
                regionFormat, regionFormatTypeDaylight, regionFormatTypeStandard, fallbackFormat,
                metaZones);
        String result = null;
        try {
            result = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(zone);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private static String findTimeZone(String targetId, String gmtFormat, String gmtZeroFormat,
            String hourFormat) {

        TimeZone timeZone = TimeZone.getTimeZone(targetId);
        

        int offset = timeZone.getRawOffset();
        // System.out.println(timeZone.getDisplayName()+" gmt"+offset/3600000);

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
