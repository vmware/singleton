/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.io.Serializable;
import java.util.Map;


public class CldrMetaZone implements Serializable {
    
    
    private static final long serialVersionUID = 7143245476086839983L;

    public CldrMetaZone() {}
    public CldrMetaZone(String zoneKey, String exemplarCity, String metazoneKey, String timeZone,
    		Map<String, TimeZoneDisplayName> metazoneValue, String territory) {
        this.zoneKey = zoneKey;
        this.exemplarCity = exemplarCity;
        this.metazoneKey = metazoneKey;
        this.timeZone = timeZone;
        this.setMetazoneValue(metazoneValue);
        this.territory = territory;
    }
    private String zoneKey;
    private String exemplarCity;
    private String metazoneKey;
    private String timeZone;
    private Map<String, TimeZoneDisplayName> metazoneValue;
    private String  territory;
    
    public String getZoneKey() {
        return zoneKey;
    }
    public void setZoneKey(String zoneKey) {
        this.zoneKey = zoneKey;
    }
    public String getExemplarCity() {
        return exemplarCity;
    }
    public void setExemplarCity(String exemplarCity) {
        this.exemplarCity = exemplarCity;
    }
    public String getMetazoneKey() {
        return metazoneKey;
    }
    public void setMetazoneKey(String metazoneKey) {
        this.metazoneKey = metazoneKey;
    }

    public String getTerritory() {
        return territory;
    }
    public void setTerritory(String territory) {
        this.territory = territory;
    }
    public String getTimeZone() {
        return timeZone;
    }
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
	public Map<String, TimeZoneDisplayName> getMetazoneValue() {
		return metazoneValue;
	}
	public void setMetazoneValue(Map<String, TimeZoneDisplayName> metazoneValue) {
		this.metazoneValue = metazoneValue;
	}
   
}
