/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.common.Constants;


public class TimeZoneName implements Serializable {
    
   
    private static final long serialVersionUID = 9060484294234938026L;
    
    /** * @param gmtZeroFormat
    /** * @param gmtFormat
    /** * @param hourFormat
    /** * @param regionFormat
    /** * @param regionFormatTypeDaylight
    /** * @param regionFormatTypeStandard
    /** * @param fallbackFormat
    /** * @param metaZones */
    public TimeZoneName(String language, String gmtZeroFormat, String gmtFormat, String hourFormat,
            String regionFormat, String regionFormatTypeDaylight, String regionFormatTypeStandard,
            String fallbackFormat, List<LinkedHashMap<String,Object>> metaZones) {
        this.language = language;
        this.timeZoneNames = new LinkedHashMap<String,Object>();
        this.timeZoneNames.put(Constants.TIMEZONENAME_HOUR_FORMAT, hourFormat);
        this.timeZoneNames.put(Constants.TIMEZONENAME_GMT_FORMAT, gmtFormat);
        this.timeZoneNames.put(Constants.TIMEZONENAME_GMT_ZERO_FORMAT,gmtZeroFormat);
        this.timeZoneNames.put(Constants.TIMEZONENAME_REGION_FORMAT,regionFormat);
        this.timeZoneNames.put(Constants.TIMEZONENAME_REGION_FORMAT_TYPE_DAYLIGHT,regionFormatTypeDaylight);
        this.timeZoneNames.put(Constants.TIMEZONENAME_REGION_FORMAT_TYPE_STANDARD,regionFormatTypeStandard);
        this.timeZoneNames.put(Constants.TIMEZONENAME_FALLBACK_FORMAT,fallbackFormat);
        this.timeZoneNames.put(Constants.TIMEZONENAME_METAZONES,metaZones);
    }
    public TimeZoneName() {}
   
    private String language;
    private LinkedHashMap <String, Object> timeZoneNames;
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
   
	public LinkedHashMap<String, Object> getTimeZoneNames() {
		return timeZoneNames;
	}
	
	public void setTimeZoneNames(LinkedHashMap<String, Object> timeZoneNames) {
		this.timeZoneNames = timeZoneNames;
	}
	
	@SuppressWarnings("unchecked")
	public  List<LinkedHashMap<String,Object>> queryMetaZones() {
		return (List<LinkedHashMap<String,Object>>) this.timeZoneNames.get(Constants.TIMEZONENAME_METAZONES);
	}
	
	public void resetMetaZones(List<LinkedHashMap<String,Object>> metaZones) {
		this.timeZoneNames.put(Constants.TIMEZONENAME_METAZONES,metaZones);
	}
}
