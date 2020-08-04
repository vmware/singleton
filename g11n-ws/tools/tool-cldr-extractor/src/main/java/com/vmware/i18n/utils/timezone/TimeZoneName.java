/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.io.Serializable;
import java.util.List;


public class TimeZoneName implements Serializable {
    
   
    private static final long serialVersionUID = 9060484294234938026L;
    
    
    public String getGmtZeroFormat() {
        return gmtZeroFormat;
    }
    public void setGmtZeroFormat(String gmtZeroFormat) {
        this.gmtZeroFormat = gmtZeroFormat;
    }
    public String getGmtFormat() {
        return gmtFormat;
    }
    public void setGmtFormat(String gmtFormat) {
        this.gmtFormat = gmtFormat;
    }
    public String getHourFormat() {
        return hourFormat;
    }
    public void setHourFormat(String hourFormat) {
        this.hourFormat = hourFormat;
    }
    public String getRegionFormat() {
        return regionFormat;
    }
    public void setRegionFormat(String regionFormat) {
        this.regionFormat = regionFormat;
    }
    public String getRegionFormatTypeDaylight() {
        return regionFormatTypeDaylight;
    }
    public void setRegionFormatTypeDaylight(String regionFormatTypeDaylight) {
        this.regionFormatTypeDaylight = regionFormatTypeDaylight;
    }
    public String getRegionFormatTypeStandard() {
        return regionFormatTypeStandard;
    }
    public void setRegionFormatTypeStandard(String regionFormatTypeStandard) {
        this.regionFormatTypeStandard = regionFormatTypeStandard;
    }
    public String getFallbackFormat() {
        return fallbackFormat;
    }
    public void setFallbackFormat(String fallbackFormat) {
        this.fallbackFormat = fallbackFormat;
    }
    public List<CldrMetaZone> getMetaZones() {
        return metaZones;
    }
    public void setMetaZones(List<CldrMetaZone> metaZones) {
        this.metaZones = metaZones;
    }
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
            String fallbackFormat, List<CldrMetaZone> metaZones) {
        this.language = language;
        this.gmtZeroFormat = gmtZeroFormat;
        this.gmtFormat = gmtFormat;
        this.hourFormat = hourFormat;
        this.regionFormat = regionFormat;
        this.regionFormatTypeDaylight = regionFormatTypeDaylight;
        this.regionFormatTypeStandard = regionFormatTypeStandard;
        this.fallbackFormat = fallbackFormat;
        this.metaZones = metaZones;
    }
    public TimeZoneName() {}
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    private String language;
    private String gmtZeroFormat;
    private String gmtFormat;
    private String hourFormat;
    private String regionFormat;
    private String regionFormatTypeDaylight;
    private String regionFormatTypeStandard;
    private String fallbackFormat;
    private List<CldrMetaZone> metaZones;
}
